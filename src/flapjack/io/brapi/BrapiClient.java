// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io.brapi;

import java.util.logging.*;

import org.restlet.resource.*;

import uk.ac.hutton.brapi.resource.*;

public class BrapiClient
{
	private static ClientResource cr = new ClientResource("");

	// Returns a list of available maps
	public static MapList getMaps()
		throws ResourceException
	{
		cr.setReference("http://wildcat:8080/brapi/maps/");
		cr.getLogger().setLevel(Level.OFF);
		MapList list = cr.get(MapList.class);

		return list;
	}

	// Returns the details (markers, chromosomes, positions) for a given map
	public static MapDetail getMapDetail(int mapIndex)
		throws ResourceException
	{
		cr.setReference("http://wildcat:8080/brapi/maps/" + mapIndex);
		cr.getLogger().setLevel(Level.OFF);
		MapDetail mapDetail = cr.get(MapDetail.class);

		return mapDetail;
	}

	// Returns a list of line names
	public static GermplasmList getGermplasms()
		throws ResourceException
	{
		cr.setReference("http://wildcat:8080/brapi/germplasm/");
		cr.getLogger().setLevel(Level.OFF);
		GermplasmList list = cr.get(GermplasmList.class);

		return list;
	}

	// Returns allele information for a given germplasm (for a markerprofile)
	// TODO: The first call could return multiple MarkerProfile objects. Gordon
	// still needs to decide how this will work
	public static MarkerProfile getMarkerProfile(int germplasmID)
	{
		cr.setReference("http://wildcat:8080/brapi/germplasm/" + germplasmID + "/markerprofiles/");
		cr.getLogger().setLevel(Level.OFF);
		GermplasmMarkerProfileList list = cr.get(GermplasmMarkerProfileList.class);

		String firstID = list.getMarkerProfiles().get(0);

		cr.setReference("http://wildcat:8080/brapi/markerprofiles/" + firstID);
		cr.getLogger().setLevel(Level.OFF);
		MarkerProfile profile = cr.get(MarkerProfile.class);

		return profile;
	}
}