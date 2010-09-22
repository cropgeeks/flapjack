// Copyright 2007-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.io;

import java.io.*;
import java.util.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.io.*;

public class BinarySerializer
{
	protected static boolean GUID = false;
	protected static boolean DEBUG = false;

	protected DataOutputStream out;
	protected DataInputStream in;

	public BinarySerializer()
	{
	}

	public void serialize(Project project)
		throws Exception
	{
		long st = System.currentTimeMillis();

		File file = project.fjFile.getFile();
		out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));

		// Header information
		out.writeBytes("FLAPJACK\032");

		// Version information
		out.writeInt(1);
		// Created by
		writeString("Flapjack " + Install4j.VERSION);

		// The project itself
		saveProject(project);

		out.close();

		long et = System.currentTimeMillis();
		System.out.println("BIN project save in " + (et-st) + "ms");
	}

	public Project deserialize(FlapjackFile fjFile, boolean fullRead)
		throws Exception
	{
		long st = System.currentTimeMillis();

		in = new DataInputStream(new BufferedInputStream(fjFile.getInputStream()));

		// Header information
		byte[] header = new byte[9];
		int headerLength = in.read(header);

		if (headerLength != 9 || !(new String(header).equals("FLAPJACK\032")))
			throw new DataFormatException("File does not contain a Flapjack header");

		// Version information
		int version = in.readInt();
		// Created by
		String creator = readString();

		System.out.println("Project V" + version + " (" + creator + ")");


		// The project itself
		Project project = null;
		if (fullRead)
			project = loadProject();

		in.close();

		long et = System.currentTimeMillis();
		System.out.println("BIN project load in " + (et-st) + "ms");

		return project;
	}

	protected void saveProject(Project project)
		throws Exception
	{
		if (GUID)
			out.writeFloat(project.ID);

		// The number of datasets
		out.writeInt(project.getDataSets().size());

		for (DataSet dataSet: project.getDataSets())
			saveDataSet(dataSet);
	}

	protected Project loadProject()
		throws Exception
	{
		Project project = new Project();

		if (GUID)
			in.readFloat();

		// The number of datasets
		int dataSetCount = in.readInt();
		if (DEBUG)
			System.out.println("found " + dataSetCount + " data sets");

		for (int i = 0; i < dataSetCount; i++)
			loadDataSet(project);

		return project;
	}

	protected void saveDataSet(DataSet dataSet)
		throws Exception
	{
		if (GUID)
			out.writeFloat(dataSet.ID);

		// Name
		writeString(dataSet.getName());

		// State table
		saveStateTable(dataSet.getStateTable());

		// Number of chromosomes
		out.writeInt(dataSet.getChromosomeMaps().size());
		// Chromosome data
		for (ChromosomeMap map: dataSet.getChromosomeMaps())
			saveChromosomeMap(map, dataSet);

		// Number traits
		out.writeInt(dataSet.getTraits().size());
		// Trait data
		for (Trait trait : dataSet.getTraits())
			saveTrait(trait);

		// Number of lines
		out.writeInt(dataSet.getLines().size());
		// Line data
		for (Line line: dataSet.getLines())
			saveLine(line, dataSet);

		// Number of view sets
		out.writeInt(dataSet.getViewSets().size());
		for (GTViewSet viewSet: dataSet.getViewSets())
			saveGTViewSet(viewSet);

		// Has dummy line?
		out.writeBoolean(dataSet.getDummyLine() != null);
		// Dummy line data
		if (dataSet.getDummyLine() != null)
			saveLine(dataSet.getDummyLine(), dataSet);

		// DBAssociation
		saveDBAssociation(dataSet.getDbAssociation());
	}

	protected void loadDataSet(Project project)
		throws Exception
	{
		DataSet dataSet = new DataSet();

		if (GUID)
			in.readFloat();

		// Name
		dataSet.setName(readString());

		// State table
		loadStateTable(dataSet);

		// Number of chromosomes
		int chromosomeCount = in.readInt();
		if (DEBUG)
			System.out.println("found " + chromosomeCount + " chromosomes");
		// Chromosome data
		for (int i = 0; i < chromosomeCount; i++)
			loadChromosomeMap(dataSet);

		// Number traits
		int traitCount = in.readInt();
		if (DEBUG)
			System.out.println("found " + traitCount + " traits");
		// Trait data
		for (int i = 0; i < traitCount; i++)
			loadTrait(dataSet);

		// Number of lines
		int lineCount = in.readInt();
		if (DEBUG)
			System.out.println("found " + lineCount + " lines");
		for (int i = 0; i < lineCount; i++)
			loadLine(dataSet);

		// Number of view sets
		int viewSetCount = in.readInt();
		for (int i = 0; i < viewSetCount; i++)
			loadGTViewSet(dataSet);

		// Has dummy line?
		if (in.readBoolean())
			loadLine(dataSet);

		loadDBAssociation(dataSet);

		project.getDataSets().add(dataSet);
	}

	protected void saveStateTable(StateTable table)
		throws Exception
	{
		if (GUID)
			out.writeFloat(table.ID);

		// Number of states
		out.writeInt(table.getStates().size());

		// AlleleState objects
		for (AlleleState state: table.getStates())
		{
			if (GUID)
				out.writeFloat(state.ID);

			// isHomozygous
			out.writeBoolean(state.isHomozygous());

			// Raw data (eg A/T)
			writeString(state.getRawData());

			// Number of states
			String[] states = state.getStates();
			out.writeInt(states.length);
			// Individual state data (eg "A" and "T")
			for (String s: states)
				writeString(s);
		}
	}

	protected void loadStateTable(DataSet dataSet)
		throws Exception
	{
		StateTable table = new StateTable();

		if (GUID)
			in.readFloat();

		// Number of states
		int statesCount = in.readInt();

		// AlleleState objects
		for (int i = 0; i < statesCount; i++)
		{
			AlleleState state = new AlleleState();

			if (GUID)
				in.readFloat();

			// isHomozygous
			state.setHomozygous(in.readBoolean());

			// Raw data (eg A/T)
			state.setRawData(readString());

			// Number of states
			String[] states = new String[in.readInt()];
			// Individual state data (eg "A" and "T")
			for (int j = 0; j < states.length; j++)
				states[j] = readString();
			state.setStates(states);

			table.getStates().add(state);
		}

		dataSet.setStateTable(table);
	}

	protected void saveChromosomeMap(ChromosomeMap map, DataSet dataSet)
		throws Exception
	{
		if (GUID)
			out.writeFloat(map.ID);

		// Name
		writeString(map.getName());

		// Length
		out.writeFloat(map.getLength());

		// isSpecialChromosome
		out.writeBoolean(map.isSpecialChromosome());

		// Number of markers
		out.writeInt(map.countLoci());

		// Marker data
		for (Marker m: map)
			saveMarker(m);

		// Number of features
		out.writeInt(map.getFeatures().size());

		// Feature Data
		for (Feature f: map.getFeatures())
				saveFeature(f);
	}

	protected void loadChromosomeMap(DataSet dataSet)
		throws Exception
	{
		ChromosomeMap map = new ChromosomeMap();

		if (GUID)
			in.readFloat();

		// Name
		map.setName(readString());

		// Length
		map.setLength(in.readFloat());

		// isSpecialChromosome
		map.setSpecialChromosome(in.readBoolean());

		// Number of markers
		int markerCount = in.readInt();
		if (DEBUG)
			System.out.println("expecting " + markerCount + " markers in " + map.getName());
		// Marker data
		for (int i = 0; i < markerCount; i++)
			loadMarker(map);

		// Number of features
		int featureCount = in.readInt();
		if (DEBUG)
			System.out.println("expecting " + featureCount + " features in " + map.getName());
		// Feature data
		for (int i = 0; i < featureCount; i++)
			loadFeature(map);

		dataSet.getChromosomeMaps().add(map);
	}

	protected void saveMarker(Marker marker)
		throws Exception
	{
		if (GUID)
			out.writeFloat(marker.ID);

		// Name
		writeString(marker.getName());

		// Position
		out.writeFloat(marker.getPosition());

		// Real position
		out.writeFloat(marker.getRealPosition());
	}

	protected void loadMarker(ChromosomeMap map)
		throws Exception
	{
		Marker marker = new Marker();

		if (GUID)
			in.readFloat();

		// Name
		marker.setName(readString());

		// Position
		marker.setPosition(in.readFloat());

		// Real position
		marker.setRealPosition(in.readFloat());

		map.getMarkers().add(marker);
	}

	protected void saveFeature(Feature f)
			throws Exception
	{
		if (GUID)
			out.writeFloat(f.ID);

		QTL feature = (QTL)f;
		// QTL Specific attributes
		// Position
		out.writeFloat(feature.getPosition());

		// Trait
		writeString(feature.getTrait());

		// Experiment
		writeString(feature.getExperiment());

		// VNames
		if(feature.getVNames().length > 0)
		{
			out.writeInt(feature.getVNames().length);

			for(String vName : feature.getVNames())
				writeString(vName);
		}

		// Values
		if(feature.getValues().length > 0)
		{
			out.writeInt(feature.getValues().length);

			for(String value : feature.getValues())
				writeString(value);
		}
		// QTL specific end

		// Name
		writeString(feature.getName());

		// Min
		out.writeFloat(feature.getMin());

		// Max
		out.writeFloat(feature.getMax());

		// Visible
		out.writeBoolean(feature.isVisible());

		// Allowed
		out.writeBoolean(feature.isAllowed());

		// Red value
		out.writeInt(feature.getRed());

		// Green value
		out.writeInt(feature.getGreen());

		// Blue value
		out.writeInt(feature.getBlue());
	}

	protected void loadFeature(ChromosomeMap map)
		throws Exception
	{
		QTL feature = new QTL();

		if (GUID)
			in.readFloat();

		//QTL Specific attributes
		// Position
		feature.setPosition(in.readFloat());

		// Trait
		feature.setTrait(readString());

		// Experiment
		feature.setExperiment(readString());

		//ChromosomeMap map = dataSet.getChromosomeMaps().get(mapIndex);
		feature.setChromosomeMap(map);

		// VNames
		int vNameCount = in.readInt();
		String[] vNames = new String[vNameCount];
		for (int i = 0; i < vNames.length; i++)
			vNames[i] = readString();

		feature.setVNames(vNames);

		// Value
		int valueCount = in.readInt();
		String[] values = new String[valueCount];
		for (int i = 0; i < values.length; i++)
			values[i] = readString();

		feature.setValues(values);
		// QTL specific end

		// Name
		feature.setName(readString());

		// Min
		feature.setMin(in.readFloat());

		// Max
		feature.setMax(in.readFloat());

		// Visible
		feature.setVisible(in.readBoolean());

		// Allowed
		feature.setAllowed(in.readBoolean());

		// Red
		feature.setRed(in.readInt());

		// Green
		feature.setGreen(in.readInt());

		// Blue
		feature.setBlue(in.readInt());

		map.getFeatures().add(feature);
	}

	protected void saveTrait(Trait trait)
		throws Exception
	{
		if (GUID)
			out.writeFloat(trait.ID);

		// Name
		writeString(trait.getName());

		// Categories
		int noCategories = trait.getCategories().size();
		out.writeInt(noCategories);
		for(int i = 0; i < noCategories; i++)
			writeString(trait.getCategories().get(i));
	}

	protected void loadTrait(DataSet dataSet)
		throws Exception
	{
		Trait trait = new Trait();

		if (GUID)
			in.readFloat();

		// Name
		trait.setName(readString());

		// Categories
		int noCategories = in.readInt();
		for( int i = 0; i < noCategories; i++)
			trait.getCategories().add(readString());

		dataSet.getTraits().add(trait);
	}

	protected void saveLine(Line line, DataSet dataSet)
		throws Exception
	{
		if (GUID)
			out.writeFloat(line.ID);

		// Name
		writeString(line.getName());

		// Number of genotype data objects
		out.writeInt(line.getGenotypes().size());
		// GenotypeData
		for (GenotypeData data: line.getGenotypes())
			saveGenotypeData(data, dataSet);

		// Number of trait value objects
		out.writeInt(line.getTraitValues().size());
		// TraitValues
		for (TraitValue traitValue : line.getTraitValues())
			saveTraitValue(traitValue, dataSet);
	}

	protected void loadLine(DataSet dataSet)
		throws Exception
	{
		Line line = new Line();

		if (GUID)
			in.readFloat();

		// Name
		line.setName(readString());

		// Number of genotype data objects
		int genoCount = in.readInt();
		if (DEBUG)
			System.out.println("expecting " + genoCount + " GenotypeData objects in " + line.getName());
		// GenotypeData
		for (int i = 0; i < genoCount; i++)
			loadGenotypeData(line, dataSet);

		// Number of trait value objects
		int traitValueCount = in.readInt();
		// TraitValues
		for (int i = 0; i < traitValueCount; i++)
			loadTraitValue(line, dataSet);

		dataSet.getLines().add(line);
	}

	protected void saveGenotypeData(GenotypeData data, DataSet dataSet)
		throws Exception
	{
		if (GUID)
			out.writeFloat(data.ID);

		// REFERENCE to chromosome
		if (GUID)
			out.writeFloat(data.getChromosomeMap().ID);

		// Index in the data set of the chromosome this object belongs to
		ChromosomeMap map = data.getChromosomeMap();
		out.writeInt(dataSet.getChromosomeMaps().indexOf(map));

		// Using byte[] or int[] based storage?
		out.writeBoolean(data.getLoci() != null);

		// byte[] data array
		if (data.getLoci() != null)
		{
			out.writeInt(data.getLoci().length);
			out.write(data.getLoci());
		}
		// int[] data array
		else
		{
			int[] lociInt = data.getLociInt();
			out.writeInt(lociInt.length);
			for (int i: lociInt)
				out.writeInt(i);
		}
	}

	protected void loadGenotypeData(Line line, DataSet dataSet)
		throws Exception
	{
		GenotypeData data = new GenotypeData();

		if (GUID)
			in.readFloat();

		// REFERENCE to chromosome
		if (GUID)
			in.readFloat();

		// Index in the data set of the chromosome this object belongs to
		int mapIndex = in.readInt();
		ChromosomeMap map = dataSet.getChromosomeMaps().get(mapIndex);
		data.setChromosomeMap(map);

		if (DEBUG)
			System.out.println("  data for map: " + map.getName());

		// Using byte[] or int[] based storage?
		boolean useBytes = in.readBoolean();

		// Byte[] data array
		if (useBytes)
		{
			int count = in.readInt();
			byte[] loci = new byte[count];
			in.read(loci);

			data.setLoci(loci);
		}
		else
		{
			int[] lociInt = new int[in.readInt()];
			for (int i = 0; i < lociInt.length; i++)
				lociInt[i] = in.readInt();

			data.setLociInt(lociInt);
		}

		line.getGenotypes().add(data);
	}

	protected void saveTraitValue(TraitValue traitValue, DataSet dataSet)
		throws Exception
	{
		if (GUID)
			out.writeFloat(traitValue.ID);

		// Value
		out.writeFloat(traitValue.getValue());

		// Normal
		out.writeFloat(traitValue.getNormal());

		// Defined
		out.writeBoolean(traitValue.isDefined());

		// Trait - output its index
		int traitIndex = dataSet.getTraits().indexOf(traitValue.getTrait());
		out.writeInt(traitIndex);
	}

	protected void loadTraitValue(Line line, DataSet dataSet)
		throws Exception
	{
		TraitValue traitValue = new TraitValue();

		if (GUID)
			in.readFloat();

		// Value
		traitValue.setValue(in.readFloat());

		// Normal
		traitValue.setNormal(in.readFloat());

		// Defined
		traitValue.setDefined(in.readBoolean());

		// Trait - get from its index
		int traitIndex = in.readInt();
		traitValue.setTrait(dataSet.getTraits().get(traitIndex));

		line.getTraitValues().add(traitValue);
	}

	protected void saveGTViewSet(GTViewSet viewSet)
		throws Exception
	{
		if (GUID)
			out.writeFloat(viewSet.ID);

		// Name
		writeString(viewSet.getName());

		out.writeInt(viewSet.getViewIndex());
		out.writeInt(viewSet.getColorScheme());
		out.writeInt(viewSet.getRandomColorSeed());
		out.writeInt(viewSet.getComparisonLineIndex());
		out.writeFloat(viewSet.getAlleleFrequencyThreshold());
		out.writeBoolean(viewSet.getDisplayLineScores());

		// Selected-traits
		writeString(viewSet.getSelectedTraits());

		// Number of LineInfo objects
		out.writeInt(viewSet.getLines().size());
		// LineInfo data
		for (LineInfo lineInfo: viewSet.getLines())
			saveLineInfo(lineInfo);

		// Number of hidden lines LineInfo objects
		out.writeInt(viewSet.getHideLines().size());
		// Hidden lines LineInfo data
		for (LineInfo lineInfo: viewSet.getHideLines())
			saveLineInfo(lineInfo);

		// Number of views
		out.writeInt(viewSet.getViews().size());
		for (GTView view: viewSet.getViews())
			saveGTView(view);

		// Bookmarks
		out.writeInt(viewSet.getBookmarks().size());
		for (Bookmark bookmark: viewSet.getBookmarks())
			saveBookmark(bookmark, viewSet.getDataSet());
	}

	protected void loadGTViewSet(DataSet dataSet)
		throws Exception
	{
		GTViewSet viewSet = new GTViewSet();
		// Rebuild the reference to the data set
		viewSet.setDataSet(dataSet);

		if (GUID)
			in.readFloat();

		// Name
		viewSet.setName(readString());

		viewSet.setViewIndex(in.readInt());
		viewSet.setColorScheme(in.readInt());
		viewSet.setRandomColorSeed(in.readInt());
		int comparisonLineIndex = in.readInt();
		viewSet.setComparisonLineIndex(comparisonLineIndex);
		viewSet.setAlleleFrequencyThreshold(in.readFloat());
		viewSet.setDisplayLineScores(in.readBoolean());

		// Selected-traits
		viewSet.setSelectedTraits(readString());

		// Number of LineInfo objects
		int lineInfoCount = in.readInt();
		// LineInfo data
		for (int i = 0; i < lineInfoCount; i++)
			loadLineInfo(viewSet.getLines(), dataSet);

		int hidelineInfoCount = in.readInt();
		// Hidden lines LineInfo data
		for (int i = 0; i < hidelineInfoCount; i++)
			loadLineInfo(viewSet.getHideLines(), dataSet);

		// Number of views
		int viewCount = in.readInt();
		for (int i = 0; i < viewCount; i++)
			loadGTView(viewSet, i);

		// Bookmarks
		int bookmarkCount = in.readInt();
		for (int i = 0; i < bookmarkCount; i++)
			loadBookmark(viewSet);

		// Rebuild the comparison line reference
		if (comparisonLineIndex != -1)
			viewSet.setComparisonLine(dataSet.getLines().get(comparisonLineIndex));

		dataSet.getViewSets().add(viewSet);
	}

	protected void saveGTView(GTView view)
		throws Exception
	{
		if (GUID)
			out.writeFloat(view.ID);

		out.writeInt(view.getComparisonMarkerIndex());
		out.writeBoolean(view.getMarkersOrdered());

		// Number of marker infos
		out.writeInt(view.getMarkers().size());
		for (MarkerInfo markerInfo: view.getMarkers())
			saveMarkerInfo(markerInfo);

		// Number of hide marker infos
		out.writeInt(view.getHideMarkers().size());
		for (MarkerInfo markerInfo: view.getHideMarkers())
			saveMarkerInfo(markerInfo);
	}

	protected void loadGTView(GTViewSet viewSet, int index)
		throws Exception
	{
		GTView view = new GTView();
		// Rebuild the reference to the view set
		view.setViewSet(viewSet);

		if (GUID)
			in.readFloat();

		int comparisonMarkerIndex = in.readInt();
		view.setComparisonMarkerIndex(comparisonMarkerIndex);
		view.setMarkersOrdered(in.readBoolean());

		// Rebuild the reference to the chromosome map
		ChromosomeMap map = viewSet.getDataSet().getChromosomeMaps().get(index);
		view.setChromosomeMap(map);

		// Rebuild the comparison marker reference
		if (comparisonMarkerIndex != -1)
			view.setComparisonMarker(map.getMarkers().get(comparisonMarkerIndex));

		// Number of MarkerInfo objects
		int markerInfoCount = in.readInt();
		// MarkerInfo data
		for (int i = 0; i < markerInfoCount; i++)
			loadMarkerInfo(view.getMarkers(), map);

		// Number of hide marker objects
		int hidemarkerInfoCount = in.readInt();
		// MarkerInfo data
		for (int i = 0; i < hidemarkerInfoCount; i++)
			loadMarkerInfo(view.getHideMarkers(), map);

		viewSet.getViews().add(view);
	}

	protected void saveLineInfo(LineInfo lineInfo)
		throws Exception
	{
		if (GUID)
			out.writeFloat(lineInfo.ID);

		// index
		out.writeInt(lineInfo.getIndex());
		// selected
		out.writeBoolean(lineInfo.getSelected());
		// score
		out.writeFloat(lineInfo.getScore());
	}

	protected void loadLineInfo(ArrayList<LineInfo> list, DataSet dataSet)
		throws Exception
	{
		LineInfo lineInfo = new LineInfo();

		if (GUID)
			in.readFloat();

		// index
		int index = in.readInt();
		lineInfo.setIndex(index);
		// selected
		lineInfo.setSelected(in.readBoolean());
		// score
		lineInfo.setScore(in.readFloat());

		// Rebuild the reference to the Line this LineInfo wraps
		Line line = dataSet.getLines().get(index);
		lineInfo.setLine(line);

		list.add(lineInfo);
	}

	protected void saveMarkerInfo(MarkerInfo markerInfo)
		throws Exception
	{
		if (GUID)
			out.writeFloat(markerInfo.ID);

		// index
		out.writeInt(markerInfo.getIndex());
		// selected
		out.writeBoolean(markerInfo.getSelected());
	}

	protected void loadMarkerInfo(ArrayList<MarkerInfo> list, ChromosomeMap map)
		throws Exception
	{
		MarkerInfo markerInfo = new MarkerInfo();

		if (GUID)
			in.readFloat();

		// index
		int index = in.readInt();
		markerInfo.setIndex(index);
		// selected
		markerInfo.setSelected(in.readBoolean());

		// Rebuild the reference to the Marker this MarkerInfo wraps
		Marker marker = map.getMarkers().get(index);
		markerInfo.setMarker(marker);

		list.add(markerInfo);
	}

	protected void saveBookmark(Bookmark bookmark, DataSet dataSet)
		throws Exception
	{
		if (GUID)
			out.writeFloat(bookmark.ID);

		// Index of chromosome
		ChromosomeMap map = bookmark.getChromosome();
		out.writeInt(dataSet.getChromosomeMaps().indexOf(map));

		// Index of line
		Line line = bookmark.getLine();
		out.writeInt(dataSet.getLines().indexOf(line));

		// Index of marker
		Marker marker = bookmark.getMarker();
		out.writeInt(map.getMarkers().indexOf(marker));
	}

	private void loadBookmark(GTViewSet viewSet)
		throws Exception
	{
		Bookmark bookmark = new Bookmark();
		DataSet dataSet = viewSet.getDataSet();

		if (GUID)
			in.readFloat();

		int mapIndex = in.readInt();
		int lineIndex = in.readInt();
		int markerIndex = in.readInt();

		// Rebuild the references from the data
		ChromosomeMap map = dataSet.getChromosomeMaps().get(mapIndex);
		bookmark.setChromosome(map);
		bookmark.setLine(dataSet.getLines().get(lineIndex));
		bookmark.setMarker(map.getMarkers().get(markerIndex));

		viewSet.getBookmarks().add(bookmark);
	}

	private void saveDBAssociation(DBAssociation db)
		throws Exception
	{
		if (GUID)
			out.writeFloat(db.ID);

		writeString(db.getLineSearch());
		writeString(db.getMarkerSearch());
	}

	private void loadDBAssociation(DataSet dataSet)
		throws Exception
	{
		DBAssociation db = new DBAssociation();

		if (GUID)
			in.readFloat();

		db.setLineSearch(readString());
		db.setMarkerSearch(readString());

		dataSet.setDbAssociation(db);
	}

	// Reads and returns a string from the bytestream, by expecting to read a
	// single integer defining the string's length; then that number of bytes
	protected String readString()
		throws Exception
	{
		byte[] data = new byte[in.readInt()];

		in.read(data);

		return new String(data, "UTF8");
	}

	protected void writeString(String str)
		throws Exception
	{
		out.writeInt(str.length());
		out.write(str.getBytes("UTF8"));
	}
}