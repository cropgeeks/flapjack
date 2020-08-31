// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io;

import java.awt.*;
import java.io.*;
import java.text.*;
import java.util.*;

import jhi.flapjack.data.*;

import scri.commons.io.*;
import scri.commons.gui.*;

public class QTLImporter extends SimpleJob
{
	private NumberFormat nf = NumberFormat.getInstance();

	private ProgressInputStream is;
	private File file;
	private DataSet dataSet;

	// Number of expected KNOWN headers in imported file
	private static final int HEADERCOUNT = 7;

	// Store a "track" per chromsome - the QTLs will be added to the appropriate
	// track as they are read
	HashMap<String, ArrayList<QTL>> chromosomes = new HashMap<>();
	// And they'll also be added to this for easy reference
	LinkedList<QTL> qtls = new LinkedList<>();

	// Store references to each trait, so colors can be assigned post-import
	HashMap<String, Color> traits = new HashMap<>();

	private int featuresRead, featuresAdded;

	private ChromosomeMap allMap = null;

	public QTLImporter(File file, DataSet dataSet)
	{
		this.file = file;
		this.dataSet = dataSet;

		maximum = 5555;

		// Add a storage track to each chromosome
		for (ChromosomeMap c: dataSet.getChromosomeMaps())
		{
			chromosomes.put(c.getName(), new ArrayList<QTL>());

			// And also look for the all-chromosome if it exists
			if (c.isSpecialChromosome())
				allMap = c;

			// Clear any existing MarkerProperties/linked QTL from the markers
			for (Marker m: c.getMarkers())
				m.setProperties(null);
		}
	}

	public void runJob(int index)
		throws Exception
	{
		is = new ProgressInputStream(new FileInputStream(file));
		BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));

		// Read the header (if any)
		String str = in.readLine();
		if (str != null & str.startsWith("#"))
		{
			// Strip out whitespace
			str = str.replaceAll("\\s+", "");
			if (str.toLowerCase().startsWith("#fjfile=qtl-gobii"))
				readGOBii(in);
			else
				readQTL(in);
		}
		// If there's no header (normally we'd fail), but exception for GOBII
		else if (str != null && str.split("\t").length == 5)
			readGOBii(in);


		// Quit before applying to the dataset if the user cancelled...
		if (okToRun == false)
			return;

		// Work out the colors for the traits and QTLs
		calculateTraitColors();

		// Finally, assign the QTL tracks to the chromosomes
		for (ChromosomeMap c: dataSet.getChromosomeMaps())
		{
			// Sort the QTLs into map order
			ArrayList<QTL> track = chromosomes.get(c.getName());
			Collections.sort(track);

			c.setQtls(track);
		}

		// NEW STEP:
		// Take the *sorted* QTL from the main chromosomes and duplicate them
		// onto the all-chromosome. This is done here rather than above because
		// if they all went onto a single track and then got sorted, different
		// QTL from different chromosomes (but at the same local positions)
		// would overlap, which screws up the TrackOptimiser when it runs
		if (allMap != null)
		{
			ArrayList<QTL> track = new ArrayList<>();

			for (ChromosomeMap map: dataSet.getChromosomeMaps())
			{
				if (map.isSpecialChromosome())
					continue;

				for (QTL qtl: map.getQtls())
				{
					// Note the QTL's chromosome is left referring to the actual
					// chromosome at this point, so we can work out the
					// mapOffset (for the QTLInfo wrapper) below
					QTL clone = qtl.createClone();
					track.add(clone);
				}
			}

			allMap.setQtls(track);
		}

		in.close();

		// And then assign QTLInfo wrappers to each of the views
		for (GTViewSet viewSet : dataSet.getViewSets())
		{
			for (GTView view: viewSet.getViews())
			{
				boolean isSuper = view.getChromosomeMap().isSpecialChromosome();

				ArrayList<QTLInfo> qtls = new ArrayList<>();
				ArrayList<QTL> track = view.getChromosomeMap().getQtls();

				for (int i = 0; i < track.size(); i++)
				{
					QTL qtl = track.get(i);

					// If it's not a super-chromosome, then just add a wrapper
					if (isSuper == false)
						qtls.add(new QTLInfo(qtl, i, 0));

					// Otherwise, work out what the mapOffset needs to be
					else
					{
						ChromosomeMap map = qtl.getChromosomeMap();
						double mapOffset = getMapOffset(map, viewSet);

						qtls.add(new QTLInfo(qtl, i, mapOffset));
						qtl.setChromosomeMap(view.getChromosomeMap());
					}
				}

				view.setQTLs(qtls);
			}
		}
	}

	private void readQTL(BufferedReader in)
		throws Exception
	{
		String str = in.readLine();

		// Read and process the header (column title) line
		String[] tokens = str.split("\t", -1);

		// Work out how many additional "data score" headers there are
		String[] scoreHeaders = new String[tokens.length-HEADERCOUNT];
		for (int i = HEADERCOUNT; i < tokens.length; i++)
			scoreHeaders[i-HEADERCOUNT] = new String(tokens[i]);

		int expColumnCount = HEADERCOUNT + scoreHeaders.length;
		System.out.println("expColumnCount=" + expColumnCount);


		// Now process the main batch of lines
		for (int line = 1; (str = in.readLine()) != null && okToRun; line++)
		{
			if (str.length() == 0 || str.startsWith("#"))
				continue;

			tokens = str.split("\t", -1);

			// Fail if the data per line doesn't match the expected number
			if (tokens.length != expColumnCount)
				throw new DataFormatException(RB.format("io.DataFormatException.traitColumnError", line));

			// Its name and chromosome
			QTL qtl = new QTL(new String(tokens[0]));
			String cName = tokens[1].trim();

			// Position values
			// Position values
			qtl.setPosition(nf.parse(tokens[2]).doubleValue());
			qtl.setMin(nf.parse(tokens[3]).doubleValue());
			qtl.setMax(nf.parse(tokens[4]).doubleValue());

			// Categorical information
			qtl.setTrait(tokens[5]);
			qtl.setExperiment(tokens[6]);
			traits.put(tokens[5], Color.white);

			// Zero or more score "values"
			String[] vNames = new String[scoreHeaders.length];
			String[] values = new String[scoreHeaders.length];

			for (int i = 0; i < values.length; i++)
			{
				vNames[i] = scoreHeaders[i];
				values[i] = new String(tokens[i+HEADERCOUNT]);
			}

			qtl.setVNames(vNames);
			qtl.setValues(values);

			featuresRead++;

			// Add this QTL to the correct chromosome's track
			ArrayList<QTL> track = chromosomes.get(cName);
			if (track != null)
			{
				track.add(qtl);
				checkChromosome(qtl, cName);
				featuresAdded++;
			}

			qtls.add(qtl);
		}
	}

	// This format is a little clunky, in that there are three required columns
	// (marker_group_name, marker_name, and fav_allele) but everything else is
	// optional (or has defaults) and the columns can be in any order too
	private void readGOBii(BufferedReader in)
		throws Exception
	{
		StateTable st = dataSet.getStateTable();
		HashMap<String, QTL> markerGroups = new HashMap<>();

		String[] headers = null;
		String str = null;

		for (int line = 1; (str = in.readLine()) != null && okToRun; line++)
		{
			if (str.length() == 0 || str.startsWith("#"))
				continue;

			// First non empty line (not starting with #) should be the headers
			if (headers == null)
			{
				headers = str.split("\t", -1);
				continue;
			}

			String[] tokens = str.split("\t", -1);

			// Fail if the data per line doesn't match the expected number
			if (tokens.length < 3)
				throw new DataFormatException(RB.format("io.DataFormatException.traitColumnError", line));


			// MarkerName
			String mkrName = null;
			for (int t = 0; t < tokens.length; t++)
				if (headers[t].toLowerCase().equals("marker_name"))
					mkrName = new String(tokens[t].trim());

			if (mkrName == null)
				continue;

			// Work out which chromosome this marker is on
			Marker marker = null;
			ChromosomeMap map = null;
			for (ChromosomeMap cm: dataSet.getChromosomeMaps())
			{
				// Don't search allChromosomes, and quit once a match is found
				if (cm == allMap || map != null)
					continue;

				for (Marker m: cm.getMarkers())
					if (m.getName().equals(mkrName))
					{
						marker = m;
						map = cm;
						break;
					}
			}

			// Ignore markers not on a chromosome
			if (map == null)
			{
				System.out.println("\"" + mkrName + "\" not found in any chromosome map");
				continue;
			}


			// Now, let's make (or retrieve) the MarkerGroupName (QTL) object
			String mkrGroupName = null;
			for (int t = 0; t < tokens.length; t++)
				if (headers[t].toLowerCase().equals("marker_group_name"))
					mkrGroupName = new String(tokens[t].trim());

			if (mkrGroupName == null)
				continue;

			int favAlleleIndex = -1;
			for (int t = 0; t < tokens.length; t++)
				if (headers[t].toLowerCase().equals("fav_allele") || headers[t].toLowerCase().equals("favorable_alleles"))
					favAlleleIndex = t;
			if (favAlleleIndex == -1)
				continue;

			if (tokens[favAlleleIndex].isBlank())
				throw new DataFormatException(RB.format("io.DataFormatException.gobbi.qtl.error2", line));

			// If we're all good at this point (three required cols) then make
			// a MarkerProperties object to hold everything
			MarkerProperties properties = new MarkerProperties(marker);

			// FavAllele info
			ArrayList<Integer> indices = new ArrayList<>();
			// Split out the comma-delimited string (removing whitespace too)
			String[] favAlleles = tokens[favAlleleIndex].replaceAll("\\s+", "").split(",");
			// This now uses 'new' code in StateTable that will add any missing
			// QTL states; deals with Kate's situations with low density data
			// that didn't contain all states, so QTL favAlleles were not found
			for (String favAllele: favAlleles)
				indices.add(st.getStateCodeForGOBiiQTL(favAllele));

			if (indices.size() > 0)
			{
				dataSet.getFavAlleleManager().addFavAllelesForMarker(mkrName, indices);
				dataSet.getFavAlleleManager().addHaplotypeAlleles(mkrGroupName, mkrName, indices);
				properties.setFavAlleles(indices);
			}

			boolean hasSubEffect = false;
			boolean hasRelWeight = false;

			// For all remaining (optional) columns:
			for (int t = 0; t < tokens.length; t++)
			{
				String TOKEN = tokens[t].trim();

				if (headers[t].toLowerCase().equals("germplasm_group"))
					properties.setGermplasmGroup(new String(TOKEN));

				else if (headers[t].toLowerCase().equals("platform"))
					properties.setPlatform(new String(TOKEN));

				else if (headers[t].toLowerCase().equals("unfav_allele") || headers[t].toLowerCase().equals("unfavorable_alleles"))
				{
					ArrayList<Integer> unfavIndices = new ArrayList<>();
					// Split out the comma-delimited string (removing whitespace too)
					String[] unfavAlleles = TOKEN.replaceAll("\\s+", "").split(",");
					// This now uses 'new' code in StateTable that will add any missing
					// QTL states; deals with Kate's situations with low density data
					// that didn't contain all states, so QTL favAlleles were not found
					for (String unfavAllele: unfavAlleles)
						unfavIndices.add(st.getStateCodeForGOBiiQTL(unfavAllele));

					if (unfavIndices.size() > 0)
					{
						dataSet.getFavAlleleManager().addUnfavAllelesForMarker(mkrName, unfavIndices);
						properties.setUnfavAlleles(unfavIndices);
					}
				}

				else if (headers[t].toLowerCase().equals("trait_allele_name_ref"))
				{
					if (TOKEN.isBlank() == false)
						properties.setAlleleName(new String(TOKEN.trim()));
				}

				else if (headers[t].toLowerCase().equals("trait_allele_name_alt"))
				{
					if (TOKEN.isBlank() == false)
						properties.setAlleleNameAlt(new String(TOKEN.trim()));
				}

				else if (headers[t].toLowerCase().equals("breeding_value"))
					properties.setBreedingValue(TOKEN.equalsIgnoreCase("yes"));

				else if (headers[t].toLowerCase().equals("model"))
				{
					if (TOKEN.equalsIgnoreCase("additive"))
						properties.setModel(MarkerProperties.ADDITIVE);
					else if (TOKEN.equalsIgnoreCase("dominant"))
						properties.setModel(MarkerProperties.DOMINANT);
				}

				else if (headers[t].toLowerCase().equals("substitution_effect"))
				{
					try {
						properties.setSubEffect(nf.parse(TOKEN).doubleValue());
						hasSubEffect = true;
					}
					catch (Exception e) {}
				}

				else if (headers[t].toLowerCase().equals("relative_weight"))
				{
					try {
						properties.setRelWeight(nf.parse(TOKEN).doubleValue());
						hasRelWeight = true;
					}
					catch (Exception e) {}
				}
			}

			// If there is no allele name, substitute with fav_allele value
			if (properties.getAlleleName() == null)
			{
				int state = properties.getFavAlleles().get(0);
				properties.setAlleleName(st.getAlleleState(state).toString());
			}
			// If there is no alt allele name, substitute with unfav allele or "-"
			if (properties.getAlleleNameAlt() == null)
			{
				if (properties.getUnfavAlleles().size() > 0)
				{
					int state = properties.getUnfavAlleles().get(0);
					properties.setAlleleNameAlt(st.getAlleleState(state).toString());
				}
				else
					properties.setAlleleNameAlt("-");
			}

			if (properties.isBreedingValue() && (hasSubEffect == false || hasRelWeight == false))
				throw new DataFormatException(RB.format("io.DataFormatException.gobbi.qtl.error1", line));

			if (markerGroups.putIfAbsent(mkrGroupName, new QTL(mkrGroupName, true)) == null)
				featuresRead++;
			QTL qtl = markerGroups.get(mkrGroupName);

			if (qtl.getChromosomeMap() == null)
				qtl.setChromosomeMap(map);
			if (qtl.getChromosomeMap() != map)
				throw new Exception("Marker group '" + mkrGroupName + "' has markers listed on more than one chromosome.");

			// Update the QTL's positions based on this marker
			if (qtl.getMax() == -1 || marker.getPosition() < qtl.getMin())
				qtl.setMin(marker.getPosition());
			if (qtl.getMax() == -1 || marker.getPosition() > qtl.getMax())
				qtl.setMax(marker.getPosition());

			// Associate the QTL with the MarkerProperties for this marker
			properties.setQtl(qtl);
			// And add to the marker's list
			marker.addMarkerProperties(properties);
		}

		// Now, for each QTL we've created, get it added to the tracks
		for (QTL qtl: markerGroups.values())
		{
			// Ignore ones that never got correct sizes
			if (qtl.getMin() == -1 || qtl.getMax() == -1)
				continue;

			// Set its position to the midpoint between min and max
			double width = qtl.getMax() - qtl.getMin();
			qtl.setPosition(qtl.getMin() + (width/2.0));

			qtl.setTrait("TRAIT");
			qtl.setExperiment("EXPERIMENT");
			traits.put("TRAIT", Color.white);

			qtl.setVNames(new String[0]);
			qtl.setValues(new String[0]);

			// Add it the correct chromosome track
			String cName = qtl.getChromosomeMap().getName();
			ArrayList<QTL> track = chromosomes.get(cName);
			if (track != null)
			{
				track.add(qtl);
				checkChromosome(qtl, cName);
				featuresAdded++;
			}

			qtls.add(qtl);
		}
	}

	// Works out the mapOffset for a given map. A mapOffset is the sum total
	// of the length of all previous chromosomes *before* this map, so that
	// when including them together in a single view you know where to start
	// drawing the QTL
	private double getMapOffset(ChromosomeMap map, GTViewSet viewSet)
	{
		double mapOffset = 0;

		for (GTView view: viewSet.getViews())
		{
			if (view.getChromosomeMap() == map)
				return mapOffset;

			mapOffset += view.getChromosomeMap().getLength();
		}

		return mapOffset;
	}

	private void calculateTraitColors()
	{
		// Work out colors for each trait type
		float colorCount = traits.size();
		float hue = 0;

		Set<String> keys = traits.keySet();
		for (String key : keys)
		{
			Color color = Color.getHSBColor(hue, 0.4f, 1);
			hue += 1/colorCount;

			traits.put(key, color);
		}

		// Assign every QTL a colour
		for (QTL qtl: qtls)
		{
			String traitName = qtl.getTrait();
			Color color = traits.get(traitName);

			qtl.setDisplayColor(color);
		}
	}

	// Adds a QTL to its chromosome, but also checks if it is on the map itself
	private void checkChromosome(QTL qtl, String cName)
	{
		ChromosomeMap.Wrapper cw = dataSet.getMapByName(cName, false);
		if (cw == null)
			return;

		if (qtl.getMin() < 0 || qtl.getMax() > cw.map.getLength() ||
			qtl.getMax() < 0 || qtl.getMin() > cw.map.getLength() ||
			qtl.getPosition() < 0 || qtl.getPosition() > cw.map.getLength())
		{
			qtl.setAllowed(false);
			qtl.setVisible(false);
		}

		qtl.setChromosomeMap(cw.map);
	}

	public int getValue()
	{
		if (is == null)
			return 0;

		return Math.round(is.getBytesRead() / (float) file.length() * 5555);
	}

	public int getFeaturesRead()
		{ return featuresRead; }

	public int getFeaturesAdded()
		{ return featuresAdded; }
}