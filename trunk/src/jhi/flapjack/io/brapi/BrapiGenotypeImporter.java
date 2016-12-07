// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

import java.io.*;
import java.net.*;
import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.io.*;

import jhi.brapi.api.markerprofiles.*;

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

	private BrapiClient client;

	public BrapiGenotypeImporter(BrapiClient client, DataSet dataSet, HashMap<String, MarkerIndex> markers,
		String ioMissingData, boolean ioUseHetSep, String ioHeteroSeparator)
	{
		this.client = client;
		this.dataSet = dataSet;
		this.markers = markers;
		this.ioMissingData = ioMissingData;
		this.ioUseHetSep = ioUseHetSep;
		this.ioHeteroSeparator = ioHeteroSeparator;

		this.ioMissingData = "N";
		this.ioUseHetSep = true;
		this.ioHeteroSeparator = "/";


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
		throws Exception
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
		throws Exception
	{
		// Call /markerprofiles for list of all profile IDs so those parameters
		// can be fed into the /allelematrix call
		List<BrapiMarkerProfile> profiles = client.getMarkerProfiles();

		HashMap<String, Line> linesByProfileID = new HashMap<>();

		for (BrapiMarkerProfile mp: profiles)
		{
			String name = mp.getUniqueDisplayName();

			// TODO: Call specifies unique name but should we check for duplicates just in case???
			Line line = dataSet.createLine(name, useByteStorage);

			linesByProfileID.put(mp.getMarkerProfileDbId(), line);
		}

		if (client.hasAlleleMatrixSearchTSV())
			return readTSVAlleleMatrix(linesByProfileID, profiles);
		else
			return readJSONAlleleMatrix(linesByProfileID, profiles);
	}

	@Override
	public long getBytesRead()
		{ return 0; }

	private boolean readTSVAlleleMatrix(HashMap<String, Line> linesByProfileID, List<BrapiMarkerProfile> profiles)
		throws Exception
	{
		URI uri = client.getAlleleMatrixTSV(profiles);
		BufferedReader in = new BufferedReader(new InputStreamReader(uri.toURL().openStream()));

		// The first line is a list of marker profile IDs
		String str = in.readLine();

		// A list of IDs that need to be mapped back to the original list of marker profile IDs that
		// were asked for
		String[] tmpstr = str.split("\t");
		List<String> markerprofileIds = Arrays.asList(tmpstr);
//		markerprofileIds = markerprofileIds.subList(1, markerprofileIds.size()-1);

		while ((str = in.readLine()) != null && !str.isEmpty())
		{
			String[] tokens = str.split("\t");
			String markerID = tokens[0];

			MarkerIndex index = markers.get(markerID);

			for (int j = 1; j < tokens.length; j++)
			{
				// Retrieve the line matching this
				String mpID = markerprofileIds.get(j);
				Line line = linesByProfileID.get(mpID);

				if (line == null || index == null)
					break;

				String allele = tokens[j];

				// Determine its various states
				Integer stateCode = states.computeIfAbsent(allele,
					a -> stateTable.getStateCode(a, true, ioMissingData, ioUseHetSep, ioHeteroSeparator));

				// Then apply them to the marker data
				line.setLoci(index.mapIndex, index.mkrIndex, stateCode);

				alleleCount++;
			}

			if (useByteStorage && stateTable.size() > 127)
				return false;

			if (isOK == false)
				break;
		}

		in.close();

		return true;
	}

	private boolean readJSONAlleleMatrix(HashMap<String, Line> linesByProfileID, List<BrapiMarkerProfile> profiles)
		throws Exception
	{
		// Now retrieve the allele data using the /brapi/allelematrix call
		List<BrapiAlleleMatrix> matrixList = client.getAlleleMatrix(profiles);

		for (int m = 0; m < matrixList.size(); m++)
		{
			BrapiAlleleMatrix matrix = matrixList.get(m);

			for (int call = 0; call < matrix.getData().size(); call++)
			{
				String markerDbId = matrix.markerId(call);
				String markerprofileDbId = matrix.markerProfileId(call);
				String allele = matrix.allele(call);

				Line line = linesByProfileID.get(markerprofileDbId);
				MarkerIndex index = markers.get(markerDbId);
				if (line != null && index != null)
				{
					Integer stateCode = states.computeIfAbsent(allele,
						a -> stateTable.getStateCode(a, true, ioMissingData, ioUseHetSep, ioHeteroSeparator));

					// Then apply them to the marker data
					line.setLoci(index.mapIndex, index.mkrIndex, stateCode);

					alleleCount++;

					if (useByteStorage && stateTable.size() > 127)
						return false;
				}
			}

			if (isOK == false)
				break;
		}

		return true;
	}
}