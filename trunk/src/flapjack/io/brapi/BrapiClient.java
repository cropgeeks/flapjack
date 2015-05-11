// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io.brapi;

import org.restlet.resource.*;

import uk.ac.hutton.brapi.resource.*;

public class BrapiClient
{
	// Returns a list of available maps
	public static MapList getMaps()
		throws ResourceException
	{
		ClientResource cr = new ClientResource("http://wildcat:8080/brapi/maps/");
		MapList list = cr.get(MapList.class);

		return list;
	}

	// Returns the details (markers, chromosomes, positions) for a given map
	public static MapDetail getMapDetail(int mapIndex)
		throws ResourceException
	{
		System.out.println("http://wildcat:8080/brapi/maps/" + mapIndex);

		ClientResource cr = new ClientResource("http://wildcat:8080/brapi/maps/" + mapIndex);
		MapDetail mapDetail = cr.get(MapDetail.class);

		return mapDetail;
	}

	// Returns a list of line names
	public static GermplasmList getGermplasms()
		throws ResourceException
	{
		ClientResource cr = new ClientResource("http://wildcat:8080/brapi/germplasm/");
		GermplasmList list = cr.get(GermplasmList.class);

		return list;
	}

	// Returns allele information for a given germplasm (for a markerprofile)
	public static MarkerProfile getMarkerProfile(int germplasmID)
	{
		ClientResource cr = new ClientResource("http://wildcat:8080/brapi/germplasm/" + germplasmID + "/markerprofiles/");
		MarkerProfile profile = cr.get(MarkerProfile.class);

		return profile;
	}
}