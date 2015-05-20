// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io.brapi;

import java.net.*;
import java.util.*;
import java.util.logging.*;
import javax.xml.bind.*;

import org.restlet.*;
import org.restlet.data.*;
import org.restlet.engine.application.*;
import org.restlet.resource.*;

import uk.ac.hutton.brapi.resource.*;

public class BrapiClient
{
	private static ClientResource cr;
	private static String baseURL;

	public static void setBaseURL(String url)
	{
		baseURL = url;

		cr = new ClientResource(baseURL);

		// Set up the connection for both HTTP and HTTPS
		Protocol[] protocols = { Protocol.HTTP, Protocol.HTTPS };
		Client client = new Client(Arrays.asList(protocols));

		// The decoder handles de-compression
		Decoder decoder = new Decoder(client.getContext(), false, true);
		decoder.setNext(client);
		cr.setNext(decoder);

		// So long as the server knows we can accept a compressed response
		cr.accept(Encoding.GZIP);
		cr.accept(Encoding.DEFLATE);

		cr.getLogger().setLevel(Level.INFO);
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
}