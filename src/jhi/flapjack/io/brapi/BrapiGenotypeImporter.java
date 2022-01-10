// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

import java.io.*;
import java.net.*;
import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.io.*;

//import jhi.brapi.api.markerprofiles.*;
import okhttp3.*;
import scri.commons.gui.*;
import scri.commons.io.*;
import uk.ac.hutton.ics.brapi.resource.genotyping.variant.*;

public class BrapiGenotypeImporter implements IGenotypeImporter
{
	private static final int UNKNOWN = 0;
	private static final int DOWNLOADING_JSON = 1;
	private static final int READING_JSON = 2;
	private static final int DOWNLOADING_FLAPJACK = 3;
	private static final int READING_FLAPJACK = 4;
	private int state = UNKNOWN;

	private ProgressInputStream is;
	private DataImporter importer;
	private DataSet dataSet;
	private StateTable stateTable;

	// Each marker's name is stored (only while loading) in a hashmap, along
	// with the index of the chromosome it is associated with
	private HashMap<String, MarkerIndex> markers;

	// Stores known states as we find them so we don't have to keep working them
	// out for each allele
	private HashMap<String, Integer> states = new HashMap<>();

	private String ioMissingData;
	private String ioHeteroSeparator;

	private long alleleCount;

	private boolean useByteStorage = true;

	private boolean isOK = true;

	private BrapiClient client;
	private boolean mapWasProvided;
	private boolean fakeMapCreated = false;

	//File created from BrAPI format = flapjack response (data downloads into this)
	private File cacheFile;
	private GenotypeDataImporter gdi;
	private long bytesRead;

	public BrapiGenotypeImporter(DataImporter importer, BrapiClient client, DataSet dataSet, HashMap<String, MarkerIndex> markers,
		 String ioMissingData, String ioHeteroSeparator)
	{
		this.importer = importer;
		this.client = client;
		this.dataSet = dataSet;
		this.markers = markers;
		this.ioMissingData = ioMissingData;
		this.ioHeteroSeparator = ioHeteroSeparator;

		this.ioMissingData = "N";
		this.ioHeteroSeparator = "/";

		stateTable = dataSet.getStateTable();

		mapWasProvided = markers.size() > 0;
	}

	@Override
	public void cleanUp()
	{
		markers.clear();
	}

	@Override
	public void cancelImport()
	{
		isOK = false;
		client.cancel();
	}

	@Override
	public long getLineCount()
	{
		if (state == DOWNLOADING_JSON)
			return client.jsonLineCount();
		else if (state == READING_JSON || state == READING_FLAPJACK)
			return dataSet.getLines().size();
		else
			return 0;
	}

	@Override
	public long getAlleleCount()
	{
		if (state == DOWNLOADING_JSON)
			return client.jsonAlleleCount();
		else if (state == READING_JSON)
			return alleleCount;
		else if (state == READING_FLAPJACK)
			return gdi.getAlleleCount();
		else
			return 0;
	}

	@Override
	// Returns false if storing the data using byte arrays failed
	public boolean importGenotypeDataAsBytes()
		throws Exception
	{
		return readData();
	}

	@Override
	public void importGenotypeDataAsInts()
		throws Exception
	{
		useByteStorage = false;

		readData();
	}

	private boolean readData()
		throws Exception
	{
		BufferedReader in = null;
		URI uri = null;

		// Attempt to set progress tracking info
		if (client.getTotalLines() != 0 && client.getTotalMarkers() != 0)
		{
			long expAlleles = client.getTotalLines() * client.getTotalMarkers();
			importer.setTotalBytes(expAlleles);
		}

		int format = -1; // 0=flapjack; 1=flapjack-transposed

		VariantSet vSet = client.getVariantSet();
		for (Format f: vSet.getAvailableFormats())
		{
			System.out.println("Found format: " + f.getDataFormat());

			if (f.getDataFormat().equalsIgnoreCase("flapjack"))
			{
				format = 0;
				uri = f.getFileURL();
			}
			else if (f.getDataFormat().equalsIgnoreCase("flapjack-transposed"))
			{
				format = 1;
				uri = f.getFileURL();
			}
		}

		System.out.println(uri);

		// TODO: Better warning if no flapjack format/url found?
		if (uri == null)
		{
			System.out.println("Reading from BrAPI-JSON");
			return readJSON();
		}

		if (uri.isAbsolute() == false)
			uri = new URI(client.baseURL + uri.getPath());

		if (isOK)
		{
			Response response = client.getResponse(uri);
            java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(getClass().getName());
            while (response.code() == javax.servlet.http.HttpServletResponse.SC_ACCEPTED) {
                String body = response.body().string();
                LOG.info(response.code() + " -> " + body);
                response.close();
//                if (TaskDialog.show("Remote file is being generated. " + body, TaskDialog.QST, 1, new String[]{RB.getString("gui.text.ok"), RB.getString("gui.text.cancel")}) != 0) {
//                    response.close();
//                    cancelImport();
//                    return false;
//                }
                Thread.sleep(5000);
                response = client.getResponse(uri);
            }
            String cl = response.header("Content-Length");

			long contentLength = 0;
			try { contentLength = Long.parseLong(cl); }
			catch (NumberFormatException ne) {}

			// If the file is 500MB in size or larger
			if (cl != null && contentLength >= 524288000)
			{
				String size = FlapjackUtils.getSizeString(contentLength);
				String msg = RB.format("io.BrapiGenotypeImporter.largeFileMsg", size);
				String[] options = new String[]{RB.getString("gui.text.ok"), RB.getString("gui.text.cancel")};

				if (TaskDialog.show(msg, TaskDialog.QST, 1, options) != 0)
				{
					response.close();
					cancelImport();
					return false;
				}
			}

			if (cacheFile == null)
			{
				String GID = SystemUtils.createGUID(12);
				cacheFile = new File(FlapjackUtils.getCacheDir(), GID + ".genotype");
				cacheFile.deleteOnExit();

				is = new ProgressInputStream(response.body().byteStream());
				//in = new BufferedReader(new InputStreamReader(is, "UTF-8"));

				state = DOWNLOADING_FLAPJACK;
				BufferedOutputStream out = new BufferedOutputStream(
					new FileOutputStream(cacheFile));
				BufferedInputStream inS = new BufferedInputStream(is);

				byte[] b = new byte[4096];
				for (int n; (n = inS.read(b)) != -1; )
				{
					//If the job is cancelled, exit the loop and stop reading.
					if(!isOK) { break; }

//					try { Thread.sleep(10); }
//					catch (Exception e) {}

					out.write(b, 0, n);
//					bytesRead += 4096;
				}

				out.close();
				is.close();

				state = READING_FLAPJACK;
				System.out.println("Reading from Flapjack file");
				gdi = new GenotypeDataImporter(cacheFile, dataSet, markers, ioMissingData, ioHeteroSeparator, (format == 1), false);

				if (useByteStorage)
					return gdi.importGenotypeDataAsBytes();
				else
				{
					gdi.importGenotypeDataAsInts();
					return true;
				}
			}

		}
		else
			return false;

//		if (format == 0)
//			return readFlapjack(in);
//		else if (format == 1)
//			return readFlapjackTransposed(in);

		return false;
	}

/*	private boolean readFlapjack(BufferedReader in)
		throws Exception
	{
		// The first line is a list of marker names
		String str;

		while ((str = in.readLine()) != null && !str.isEmpty() && isOK)
		{
			if (str.startsWith("#"))
				continue;

			// Split the first line up into an array of marker names (we ignore the
			// first element as it's a redundant column header: i=1 in loop below)
			String[] markerNames = str.split("\t");

			// Now work out the map indices of these markers and the indices within
			// the map itself. This speeds up loading by pre-caching this data so we
			// don't need to search per line, rather just once per file
			int[] mapIndex = new int[markerNames.length];
			int[] mkrIndex = new int[markerNames.length];

			for (int i = 1; i < markerNames.length && isOK; i++)
			{
				MarkerIndex index = queryMarker(markerNames[i].trim());

				// Check that the marker does exists on map
				if (index != null)
				{
					mapIndex[i] = index.mapIndex;
					mkrIndex[i] = index.mkrIndex;
				}
				// This ensures a marker - in the .dat file - that isn't in the .map
				// file will be ignored
				else
					mapIndex[i] = mkrIndex[i] = -1;
			}

			while ((str = in.readLine()) != null && isOK)
			{
				if (str.length() == 0)
					continue;

				String[] values = str.split("\t");

				if (values.length == 0)
					continue;

				String name = values[0].trim();
				Line line = dataSet.createLine(name, useByteStorage);

				for (int i = 1; i < values.length && isOK; i++)
				{
					// Assuming a map was found that contains this marker...
					if (mapIndex[i] != -1)
					{
						// Determine its various states
						Integer stateCode = states.computeIfAbsent(values[i],
							a -> stateTable.getStateCode(a, true, ioMissingData, ioHeteroSeparator));

						// Then apply them to the marker data
						line.setLoci(mapIndex[i], mkrIndex[i], stateCode);

						alleleCount++;
					}
				}

				if (useByteStorage && stateTable.size() > 127)
					return false;
			}
		}

		in.close();

		return true;
	}

	private boolean readFlapjackTransposed(BufferedReader in)
		throws Exception
	{
		System.out.println("File is");

		throw new Exception("Not implemented yet");
	}
*/
	private boolean readJSON()
		throws Exception
	{
		// Create somewhere to file-cache the incoming data
		String GID = SystemUtils.createGUID(12);
		File cacheFile = new File(FlapjackUtils.getCacheDir(), GID + ".brapi");
		cacheFile.deleteOnExit();

		// Parse over the pages (and pages) of line x marker x genotype objects
		System.out.println("Calling /variantsets/{id}/calls");

		state = DOWNLOADING_JSON;
		HashMap<String,String> jsonMarkers = client.getCallSetCallsDetails(cacheFile);
		state = READING_JSON;

		ioHeteroSeparator = client.getIoHeteroSeparator();
		ioMissingData = client.getIoMissingData();

		// Initial loop to query the markers and build a map if needed
		// TODO: THIS IS INCREDIBLY INEFFICIENT
		//  An alternative might be to use the (known?) marker count and initialize
		//  a map of that size (but with what marker names?), which would allow
		//  the Line.GenotypeData objects to be pre-created but you'd still have
		//  a problem with marker names as the initial map wouldn't have them
		for (String markerName: jsonMarkers.keySet())
		{
			if (isOK == false)
				break;

			queryMarker(markerName);
		}
		fakeMapCreated = true;

		// Temp object for tracking the line objects
		HashMap<String,Line> linesByName = new HashMap<String,Line>();

		BufferedReader in = new BufferedReader(new FileReader(cacheFile));
		String str = null;

		while ((str = in.readLine()) != null)
		{
			if (isOK == false)
				break;
			if (str.isBlank())
				continue;

			String[] tokens = str.split("\t");
			if (tokens.length != 3)
				continue;

			// Fetch (or create) the line
			Line line = linesByName.computeIfAbsent(new String(tokens[0]), k -> dataSet.createLine(k, useByteStorage));

			// Fetch the marker
			MarkerIndex mi = queryMarker(new String(tokens[1]));

			// Ignore markers that aren't on the map we have (if no map was imported then
			// everything will load because a dummy map is built)
			if (mi == null)
				continue;

			// Now assign the allele
			Integer stateCode = states.computeIfAbsent(new String(tokens[2]), k -> stateTable.getStateCode(k, true, ioMissingData, ioHeteroSeparator));

			line.setLoci(mi.mapIndex, mi.mkrIndex, stateCode);
			alleleCount++;

			if (useByteStorage && stateTable.size() > 127)
				return false;
		}

		in.close();

		return true;
	}

	@Override
	public long getBytesRead()
	{
		if (state == DOWNLOADING_JSON)
			return client.jsonAlleleCount();
		else if (state == READING_JSON)
			return alleleCount;
		else if (state == DOWNLOADING_FLAPJACK)
			return bytesRead;
		else if (state == READING_FLAPJACK)
			return gdi.getBytesRead();
		else
			return 0;
	}

	private MarkerIndex queryMarker(String name)
	{
		// If a map was provided, then just use the hashtable
		if (mapWasProvided || fakeMapCreated)
			return markers.get(name);

		// Otherwise, we're into the special case for no map

		// Make sure it's not a duplicate marker
		if (markers.get(name) != null)
			return null;

		// Its position will just be based on how many we've added so far
		int position = markers.size();
		Marker marker = new Marker(name, position);

		// There's only one map, so just grab it and add the marker to it
		ChromosomeMap map = dataSet.getChromosomeMaps().get(0);
		map.addMarker(marker);
		// Updating its length as we go
		map.setLength(position);

		// We can set the ChrIndex to 0 as we know there's only one
		MarkerIndex mi = new MarkerIndex(0, markers.size());
		markers.put(marker.getName(), mi);

		return mi;
	}

	public boolean isOK()
		{ return isOK; }
}