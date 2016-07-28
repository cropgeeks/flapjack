// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

import java.io.*;
import java.net.URI;
import java.util.*;

import jhi.brapi.resource.*;
import jhi.flapjack.data.*;
import jhi.flapjack.io.*;

import scri.commons.gui.*;

import jhi.brapi.resource.*;

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
		String methodID = request.getMethodID();
		List<BrapiMarkerProfile> profiles = BrapiClient.getMarkerProfiles(methodID, request.getStudyID());

		HashMap<String, Line> linesByProfileID = new HashMap<>();

		for (BrapiMarkerProfile mp: profiles)
		{
			String name = mp.getUniqueDisplayName();

			// TODO: Call specifies unique name but should we check for duplicates just in case???
			Line line = dataSet.createLine(name, useByteStorage);

			linesByProfileID.put(mp.getMarkerprofileDbId(), line);
		}

//			return readTSVAlleleMatrix(linesByProfileID, profiles);
			return readJSONAlleleMatrix(linesByProfileID, profiles);
	}

	@Override
	public long getBytesRead()
		{ return 0; }

	private boolean readTSVAlleleMatrix(HashMap<String, Line> linesByProfileID, List<BrapiMarkerProfile> profiles)
		throws Exception
	{
		URI uri = BrapiClient.getAlleleMatrixTSV(profiles);

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

				if (line == null)
				{
					System.out.println("NULL:" + mpID);
					break;
				}

				String allele = tokens[j];

				// Determine its various states
				Integer stateCode = states.get(allele);
				if (stateCode == null)
				{
					stateCode = stateTable.getStateCode(allele, true, ioMissingData, ioUseHetSep, ioHeteroSeparator);
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

		in.close();

		return true;
	}

	private boolean readJSONAlleleMatrix(HashMap<String, Line> linesByProfileID, List<BrapiMarkerProfile> profiles)
		throws IOException
	{
		// Now retrieve the allele data using the /brapi/allelematrix call
		List<BrapiAlleleMatrix> matrixList = BrapiClient.getAlleleMatrix(profiles);
		if (matrixList.size() == 0)
			throw new IOException("List contains zero BRAPI AlleleMatrix objects");

//		BrapiAlleleMatrix matrix = matrixList.get(0);
		for (int m = 0; m < matrixList.size(); m++)
		{
			BrapiAlleleMatrix matrix = matrixList.get(m);

			// A list of IDs that need to be mapped back to the original list of marker profile IDs that
			// were asked for
			List<String> markerprofileIds = matrix.getMarkerprofileDbIds();

//			for (String s : markerprofileIds)
//				System.out.println(s);


			// MarkerName, list of scores (index of each one mapped to index in markerprofile/germplasm index)
			List<LinkedHashMap<String, List<String>>> scores = matrix.getData();

			for (int i = 0; i < scores.size(); i++)
			{
				LinkedHashMap<String, List<String>> score = scores.get(i);

				for (String markerName : score.keySet())
				{
					MarkerIndex index = markers.get(markerName);   // todo build map from these markers?

					if (index != null)
					{
						List<String> alleles = score.get(markerName);

						if(m > 0 && i == 0 && alleles.size() != markerprofileIds.size())
						{
							BrapiAlleleMatrix oldMatrix = matrixList.get(m - 1);
							LinkedHashMap<String, List<String>> oldScores = oldMatrix.getData().get(oldMatrix.getData().size() - 1);

							List<String> oldAlleles = oldScores.get(markerName);

							for (int j = 0; oldAlleles != null && j < oldAlleles.size(); j++)
							{
								// Retrieve the line matching this
								String mpID = markerprofileIds.get(j);
								Line line = linesByProfileID.get(mpID);

								if (line == null)
								{
									System.out.println("NULL:" + mpID);
									break;
								}

								String allele = oldAlleles.get(j);

								// Determine its various states
								Integer stateCode = states.get(allele);
								if (stateCode == null)
								{
									stateCode = stateTable.getStateCode(allele, true, ioMissingData, ioUseHetSep, ioHeteroSeparator);
									states.put(allele, stateCode);
								}

								// Then apply them to the marker data
								line.setLoci(index.mapIndex, index.mkrIndex, stateCode);

								alleleCount++;
							}
						}

						int offset = markerprofileIds.size() - alleles.size();

						for (int j = 0; j < alleles.size(); j++)
						{
							// Retrieve the line matching this
							String mpID = markerprofileIds.get(j + offset);
							Line line = linesByProfileID.get(mpID);

							if (line == null)
							{
								System.out.println("NULL:" + mpID);
								break;
							}

							String allele = alleles.get(j);

							// Determine its various states
							Integer stateCode = states.get(allele);
							if (stateCode == null)
							{
								stateCode = stateTable.getStateCode(allele, true, ioMissingData, ioUseHetSep, ioHeteroSeparator);
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
			}
		}

		return true;
	}
}