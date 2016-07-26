// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.*;
import java.util.*;
import java.util.logging.*;
import javax.net.ssl.*;
import javax.xml.bind.*;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;

import org.restlet.*;
import org.restlet.data.*;
import org.restlet.engine.application.*;
import org.restlet.engine.ssl.*;
import org.restlet.resource.*;
import org.restlet.util.*;

import jhi.brapi.resource.*;

public class BrapiClient
{
	private static ClientResource cr;
	private static String baseURL;

	public static void setXmlResource(XmlResource resource)
		throws Exception
	{
		baseURL = resource.getUrl();
		baseURL = "http://localhost:2000/brapi/cactuar/v1";
//		baseURL = "http://localhost:2000/brapi/gobii/v1";

		cr = new ClientResource(baseURL);

		// Set up the connection for both HTTP and HTTPS
		Protocol[] protocols = { Protocol.HTTP, Protocol.HTTPS };
		Client client = new Client(Arrays.asList(protocols));

//		cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, "user", "pass");


		// The decoder handles de-compression
		Decoder decoder = new Decoder(client.getContext(), false, true);
		decoder.setNext(client);
		cr.setNext(decoder);

		// So long as the server knows we can accept a compressed response
		cr.accept(MediaType.ALL);
		cr.accept(Encoding.GZIP);
		cr.accept(Encoding.DEFLATE);

		cr.getLogger().setLevel(Level.INFO);

		// Set up the connection to use any required SSL certificates
		initCertificates(client, resource);

		Context c = client.getContext();
		if (c == null)
		{
			c = new Context();
			client.setContext(c);
		}
		c.getParameters().add("socketTimeout", "10000");
//		c.getParameters().add("tracing", "true");
	}

	// Returns true if another 'page' of data should be requested
	private static boolean pageCheck(jhi.brapi.resource.Metadata metadata, String url)
	{
		Pagination p = metadata.getPagination();
		System.out.println(p);

		if (p.getTotalPages() == 0)
			return false;

		if (p.getCurrentPage() == p.getTotalPages()-1)
			return false;

		// If it's ok to request another page, update the URL (for the next call)
		// so that it does so
		cr.setReference(url);
		cr.addQueryParameter("pageSize", "" + p.getPageSize());
		cr.addQueryParameter("page", "" + (p.getCurrentPage()+1));

		return true;
	}

	private static String enc(String str)
	{
		try { return URLEncoder.encode(str, "UTF-8"); }
		catch (UnsupportedEncodingException e) { return str; }
	}

	public static void doAuthentication(String username, String password)
		throws Exception
	{
		if (username == null && password == null)
			return;

		String url = baseURL + "/token/";
		cr.setReference(url);

		String params = "grant_type=password&username=" + enc(username)
			+ "&password=" + enc(password) + "&client_id=flapjack";
		Form form = new Form(params);

		BrapiSessionToken token = cr.post(form.getWebRepresentation(), BrapiSessionToken.class);

		// Add the token information to all further calls
		ChallengeResponse challenge = new ChallengeResponse(ChallengeScheme.HTTP_OAUTH_BEARER);
		challenge.setRawValue(token.getSessionToken());
		cr.setChallengeResponse(challenge);
	}

	// Returns a list of available maps
	public static List<BrapiGenomeMap> getMaps()
		throws ResourceException
	{
		String url = baseURL + "/maps/";
		cr.setReference(url);

		List<BrapiGenomeMap> list = new ArrayList<>();
		boolean requestPage = true;

		while (requestPage)
		{
			LinkedHashMap hashMap = cr.get(LinkedHashMap.class);
			BasicResource<DataResult<BrapiGenomeMap>> br = new ObjectMapper().convertValue(hashMap,
				new TypeReference<BasicResource<DataResult<BrapiGenomeMap>>>() {});

			list.addAll(br.getResult().getData());
			requestPage = pageCheck(br.getMetadata(), url);
		}

		return list;
	}

	// Returns a list of available "MarkerProfileMethods" - this isn't something
	// that really exists in Germinate (yet)
	public static List<BrapiMarkerProfileMethod> getMarkerProfileMethods()
		throws ResourceException
	{
		String url = baseURL + "/markerprofiles/methods/";
		cr.setReference(url);

		List<BrapiMarkerProfileMethod> list = new ArrayList<>();
		boolean requestPage = true;

		while (requestPage)
		{
			LinkedHashMap hashMap = cr.get(LinkedHashMap.class);
			BasicResource<DataResult<BrapiMarkerProfileMethod>> br = new ObjectMapper().convertValue(hashMap,
				new TypeReference<BasicResource<DataResult<BrapiMarkerProfileMethod>>>() {});

			list.addAll(br.getResult().getData());
			requestPage = pageCheck(br.getMetadata(), url);
		}

		return list;
	}

	// Returns the details (markers, chromosomes, positions) for a given map
	public static List<BrapiMarkerPosition> getMapMarkerData(String mapID)
		throws ResourceException
	{
		// TODO: /map/{id} = map basics
		String url = baseURL + "/maps/" + enc(mapID) + "/positions";
		cr.setReference(url);

		List<BrapiMarkerPosition> list = new ArrayList<>();
		boolean requestPage = true;

		while (requestPage)
		{
			LinkedHashMap hashMap = cr.get(LinkedHashMap.class);
			BasicResource<DataResult<BrapiMarkerPosition>> br = new ObjectMapper().convertValue(hashMap,
				new TypeReference<BasicResource<DataResult<BrapiMarkerPosition>>>() {});

			list.addAll(br.getResult().getData());
			requestPage = pageCheck(br.getMetadata(), url);
		}

		return list;
	}

	// Returns a list of line names
	public static List<BrapiGermplasm> getGermplasms()
		throws ResourceException
	{
		String url = baseURL + "/germplasm/";
		cr.setReference(url);

		List<BrapiGermplasm> list = new ArrayList<>();
		boolean requestPage = true;

		while (requestPage)
		{
			LinkedHashMap hashMap = cr.get(LinkedHashMap.class);
			BasicResource<DataResult<BrapiGermplasm>> br = new ObjectMapper().convertValue(hashMap,
				new TypeReference<BasicResource<DataResult<BrapiGermplasm>>>() {});

			list.addAll(br.getResult().getData());
			requestPage = pageCheck(br.getMetadata(), url);
		}

		return list;
	}

	public static List<BrapiMarkerProfile> getMarkerProfiles(String methodID)
		throws ResourceException
	{
		String url = baseURL + "/markerprofiles/";// +
	//		methodID == null ? "/" : "&method=" + methodID);
		cr.setReference(url);

		List<BrapiMarkerProfile> list = new ArrayList<>();
		boolean requestPage = true;

		while (requestPage)
		{
			LinkedHashMap hashMap = cr.get(LinkedHashMap.class);
			BasicResource<DataResult<BrapiMarkerProfile>> br = new ObjectMapper().convertValue(hashMap, new TypeReference<BasicResource<DataResult<BrapiMarkerProfile>>>() {});
			list.addAll(br.getResult().getData());

			requestPage = pageCheck(br.getMetadata(), url);
		}

		return list;
	}

	public static List<BrapiAlleleMatrix> getAlleleMatrix(List<BrapiMarkerProfile> markerprofiles)
		throws ResourceException
	{
		String url = baseURL + "/allelematrix";
		cr.setReference(url);

		List<BrapiAlleleMatrix> list = new ArrayList<>();
		boolean requestPage = true;

		// Annoying to have to resend all this for every paged (re)POST
		StringBuilder sb = new StringBuilder();
		for (BrapiMarkerProfile mp: markerprofiles)
		{
			if (sb.length() > 0)
				sb.append("&");
			sb.append("markerprofileDbId=");
			sb.append(enc(mp.getMarkerprofileDbId()));
		}

		Form form = new Form(sb.toString());

		while (requestPage)
		{
			LinkedHashMap hashMap = cr.post(form.getWebRepresentation(), LinkedHashMap.class);
			BasicResource<BrapiAlleleMatrix> br = new ObjectMapper().convertValue(hashMap,
				new TypeReference<BasicResource<BrapiAlleleMatrix>>() {});

			ArrayList<BrapiAlleleMatrix> temp = new ArrayList<>();
			temp.add(br.getResult());
			list.addAll(temp);
			requestPage = pageCheck(br.getMetadata(), url);
		}

		return list;
	}

	public static URI getAlleleMatrixTSV(List<BrapiMarkerProfile> markerprofiles)
		throws Exception
	{
		String url = baseURL + "/allelematrix";
		cr.setReference(url);

		List<BrapiAlleleMatrix> list = new ArrayList<>();
		boolean requestPage = true;

		StringBuilder sb = new StringBuilder();
		for (BrapiMarkerProfile mp: markerprofiles)
		{
			if (sb.length() > 0)
				sb.append("&");
			sb.append("markerprofileDbId=");
			sb.append(enc(mp.getMarkerprofileDbId()));
		}
		if (sb.length() > 0)
			sb.append("&");
		sb.append("format=tsv");

		Form form = new Form(sb.toString());

		// Force no pagination
		LinkedHashMap hashMap = cr.post(form.getWebRepresentation(), LinkedHashMap.class);
		BasicResource<BrapiAlleleMatrix> br = new ObjectMapper().convertValue(hashMap,
			new TypeReference<BasicResource<BrapiAlleleMatrix>>() {});

		jhi.brapi.resource.Metadata md = br.getMetadata();
		List<Datafile> files = md.getDatafiles();

		System.out.println("FILES: " + files);

		return new URI(files.get(0).getUrl());
	}


	// This is commented out because it was probably mid-seattle code that probably
	// isn't used / needed anymore.

	// Returns allele information for a given germplasm (for a markerprofile)
	// TODO: The first call could return multiple MarkerProfile objects. Gordon
	// still needs to decide how this will work
//	public static BrapiMarkerProfile getMarkerProfile(int germplasmID)
//	{
////		System.out.println(baseURL + "/germplasm/" + germplasmID + "/markerprofiles/");
//		cr.setReference(baseURL + "/germplasm/" + germplasmID + "/markerprofiles/");
//		GermplasmMarkerProfileList list = cr.get(GermplasmMarkerProfileList.class);
//
//		if (list.getMarkerProfiles().size() > 0)
//		{
//			// TODO: Which one do we use?
//			String firstID = list.getMarkerProfiles().get(0);
//
////			System.out.println(baseURL + "/markerprofiles/" + firstID);
//			cr.setReference(baseURL + "/markerprofiles/" + firstID);
//
//			return cr.get(BrapiMarkerProfile.class);
//		}
//
//		return null;
//	}

	public static XmlBrapiProvider getBrapiProviders()
		throws Exception
	{
		URL url = new URL("https://ics.hutton.ac.uk/resources/brapi/brapi-resources.xml");

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