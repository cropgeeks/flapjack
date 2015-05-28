// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io.brapi;

import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.*;
import java.util.*;
import java.util.logging.*;
import javax.net.ssl.*;
import javax.xml.bind.*;

import org.restlet.*;
import org.restlet.data.*;
import org.restlet.engine.application.*;
import org.restlet.engine.ssl.*;
import org.restlet.resource.*;
import org.restlet.util.*;

import uk.ac.hutton.brapi.resource.*;

public class BrapiClient
{
	private static ClientResource cr;
	private static String baseURL;

	public static void setXmlResource(XmlResource resource)
		throws Exception
	{
		baseURL = resource.getUrl();

		cr = new ClientResource(baseURL);

		// Set up the connection for both HTTP and HTTPS
		Protocol[] protocols = { Protocol.HTTP, Protocol.HTTPS };
		Client client = new Client(Arrays.asList(protocols));

		// The decoder handles de-compression
		Decoder decoder = new Decoder(client.getContext(), false, true);
		decoder.setNext(client);
		cr.setNext(decoder);

		// So long as the server knows we can accept a compressed response
//		cr.accept(MediaType.ALL);
		cr.accept(Encoding.GZIP);
		cr.accept(Encoding.DEFLATE);

		cr.getLogger().setLevel(Level.INFO);

		// Set up the connection to use any required SSL certificates
		initCertificates(client, resource);
	}

	// Returns a list of available maps
	public static MapList getMaps()
		throws ResourceException
	{
		cr.setReference(baseURL + "/maps/");
		MapList list = cr.get(MapList.class);

		return list;
	}

	// Returns the details (markers, chromosomes, positions) for a given map
	public static MapDetail getMapDetail(int mapID)
		throws ResourceException
	{
		cr.setReference(baseURL + "/maps/" + mapID);
		MapDetail mapDetail = cr.get(MapDetail.class);

		return mapDetail;
	}

	// Returns a list of line names
	public static GermplasmList getGermplasms()
		throws ResourceException
	{
		cr.setReference(baseURL + "/germplasm/");
		GermplasmList list = cr.get(GermplasmList.class);

		return list;
	}

	// Returns allele information for a given germplasm (for a markerprofile)
	// TODO: The first call could return multiple MarkerProfile objects. Gordon
	// still needs to decide how this will work
	public static MarkerProfile getMarkerProfile(int germplasmID)
	{
//		System.out.println(baseURL + "/germplasm/" + germplasmID + "/markerprofiles/");
		cr.setReference(baseURL + "/germplasm/" + germplasmID + "/markerprofiles/");
		GermplasmMarkerProfileList list = cr.get(GermplasmMarkerProfileList.class);

		if (list.getMarkerProfiles().size() > 0)
		{
			// TODO: Which one do we use?
			String firstID = list.getMarkerProfiles().get(0);

//			System.out.println(baseURL + "/markerprofiles/" + firstID);
			cr.setReference(baseURL + "/markerprofiles/" + firstID);

			return cr.get(MarkerProfile.class);
		}

		return null;
	}

	public static XmlBrapiProvider getBrapiProviders()
		throws Exception
	{
		URL url = new URL("http://ics.hutton.ac.uk/resources/brapi/brapi-resources.xml");

		JAXBContext jaxbContext = JAXBContext.newInstance(XmlBrapiProvider.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		XmlBrapiProvider p = (XmlBrapiProvider) jaxbUnmarshaller.unmarshal(url);

		return p;
	}

	private static void initCertificates(Client client, XmlResource resource)
		throws Exception
	{
		if (resource.getCertificate() == null)
			return;

		// Download the "trusted" certificate needed for this resource
		URLConnection yc = new URL(resource.getCertificate()).openConnection();

		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		InputStream in = new BufferedInputStream(yc.getInputStream());
		java.security.cert.Certificate cer;
		try {
			cer = cf.generateCertificate(in);
		} finally { in.close();	}

		// Create a KeyStore to hold the certificate
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		keyStore.load(null, null);
		keyStore.setCertificateEntry("cer", cer);

		// Create a TrustManager that trusts the certificate in the KeyStore
		String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
		tmf.init(keyStore);

		// Create an SSLContext that uses the TrustManager
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, tmf.getTrustManagers(), null);

		// Then *finally*, apply the TrustManager info to Restlet
		client.setContext(new Context());
		Context context = client.getContext();

		context.getAttributes().put("sslContextFactory", new SslContextFactory() {
		    public void init(Series<Parameter> parameters) { }
		   	public SSLContext createSslContext() throws Exception { return sslContext; }
		});
	}
}