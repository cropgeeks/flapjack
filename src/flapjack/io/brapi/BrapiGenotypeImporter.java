// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io.brapi;

import java.io.*;
import java.util.*;

import flapjack.data.*;
import flapjack.io.*;

import scri.commons.gui.*;

import hutton.brapi.resource.*;

public class BrapiGenotypeImporter implements IGenotypeImporter
{
	private DataSet dataSet;
	private StateTable stateTable;

	// Each marker's name is stored (only while loading) in a hashmap, along
	// with the index of the chromosome it is associated with
	private HashMap<String, MarkerIndex> markers;

	// Stores known states as we find them so we don't have to keep working them
	// out for each allele
	private HashMap<String, Integer> states = new HashMap<>();

	private String ioMissingData;
	private boolean ioUseHetSep;
	private String ioHeteroSeparator;

	private long alleleCount;

	private boolean useByteStorage = true;

	private boolean isOK = true;

	private BrapiRequest request;

	public BrapiGenotypeImporter(BrapiRequest request, DataSet dataSet, HashMap<String, MarkerIndex> markers,
		String ioMissingData, boolean ioUseHetSep, String ioHeteroSeparator)
	{
		this.request = request;
		this.dataSet = dataSet;
		this.markers = markers;
		this.ioMissingData = ioMissingData;
		this.ioUseHetSep = ioUseHetSep;
		this.ioHeteroSeparator = ioHeteroSeparator;

		stateTable = dataSet.getStateTable();
	}

	@Override
	public void cleanUp()
	{
		markers.clear();
	}

	@Override
	public void cancelImport()
		{ isOK = false; }

	@Override
	public long getLineCount()
		{ return dataSet.getLines().size(); }

	@Override
	public long getMarkerCount()
		{ return alleleCount; }

	@Override
	public void importGenotypeData()
		throws IOException, DataFormatException
	{
		// If the first read failed, clear everything and start again using int
		// storage rather than byte
		if (readData() == false)
		{
			dataSet.getLines().clear();
			stateTable.resetTable();
			states.clear();
			useByteStorage = false;
			alleleCount = 0;

			readData();
		}
	}

	private boolean readData()
		throws IOException, DataFormatException
	{
		GermplasmList list = BrapiClient.getGermplasms();
		System.out.println("Recevied info on " + list.getGermplasm().size() + " lines");

		// We need three (!) lookup tables - one to track duplicate names, one
		// to find them by BRAPI germplasm ID and another to find them by BRAPI
		// marker profile ID
		HashMap<String, Line> linesByName = new HashMap<>();
		HashMap<Integer, Line> linesByGermplasmID = new HashMap<>();
		HashMap<String,Line> linesByProfileID = new HashMap<>();

		for (Germplasm germplasm: list.getGermplasm())
		{
			// Check for duplicate line names
			String name = germplasm.getGermplasmName();
			if (name == null)
				name = "" + germplasm.getGermplasmId();

			if (linesByName.get(name) != null)
				throw new DataFormatException(RB.format("io.DataFormatException.duplicateLineError2", name));

			Line line = dataSet.createLine(name, useByteStorage);
			linesByName.put(name, line);
			linesByGermplasmID.put(germplasm.getGermplasmId(), line);

			System.out.println("ID: #" + germplasm.getGermplasmId() + "#");
		}

		// Call /markerprofiles for list of all profile IDs so those parameters
		// can be fed into the /allelematrix call
		String methodID = request.getMethodID();
		MarkerProfileList profilelist = BrapiClient.getMarkerProfiles(methodID);

		List<MarkerProfile> profiles = profilelist.getMarkerprofiles();

		for (MarkerProfile mp: profiles)
		{
			Line line = linesByGermplasmID.get(mp.getGermplasmId());
			linesByProfileID.put(mp.getMarkerprofileId(), line);
		}



		// Now retrieve the allele data using the /brapi/allelematrix call
		AlleleMatrix matrix = BrapiClient.getAlleleMatrix(profiles);


		// A list of IDs that need to be mapped back to the original list of marker profile IDs that
		// were asked for
		List<String> markerprofileIds = matrix.getMarkerprofileIds();


		// MarkerName, list of scores (index of each one mapped to index in markerprofile/germplasm index)
		HashMap<String, List<String>> scores = matrix.getScores();

		for (String markerName: scores.keySet())
		{
			MarkerIndex index = markers.get(markerName);   // todo build map from these markers?

			if (index != null)
			{
				List<String> alleles = scores.get(markerName);

				for (int i = 0; i < alleles.size(); i++)
				{
					// Retrieve the line matching this
					String mpID = markerprofileIds.get(i);
					Line line = linesByProfileID.get(mpID);

					if (line == null)
						break;

					String allele = alleles.get(i);

					// Determine its various states
					Integer stateCode = states.get(allele);
					if (stateCode == null)
					{
						stateCode = stateTable.getStateCode(allele, true,
							ioMissingData, ioUseHetSep, ioHeteroSeparator);
						states.put(allele, stateCode);
					}

					// Then apply them to the marker data
					line.setLoci(index.mapIndex, index.mkrIndex, stateCode);

					alleleCount++;
				}

				if (useByteStorage && stateTable.size() > 127)
					return false;

				if (isOK == false)
					break;
			}
		}

		return true;
	}

	@Override
	public long getBytesRead()
		{ return 0; }
}