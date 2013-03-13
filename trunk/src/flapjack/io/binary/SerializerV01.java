// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io.binary;

import java.io.*;
import java.util.*;

import flapjack.data.*;

class SerializerV01 extends FlapjackSerializer
{
	SerializerV01(DataInputStream in, DataOutputStream out)
		{ super(in, out); }

	protected void saveProject(Project project)
		throws Exception
	{
		// The number of datasets
		out.writeInt(project.getDataSets().size());

		for (DataSet dataSet: project.getDataSets())
			saveDataSet(dataSet);
	}

	protected Project loadProject()
		throws Exception
	{
		Project project = new Project();

		// The number of datasets
		int dataSetCount = in.readInt();
		if (DEBUG)
			System.out.println("found " + dataSetCount + " data sets");

		for (int i = 0; i < dataSetCount; i++)
			project.getDataSets().add(loadDataSet(project));

		return project;
	}

	protected void saveDataSet(DataSet dataSet)
		throws Exception
	{
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

		// DBAssociation
		saveDBAssociation(dataSet.getDbAssociation());
	}

	protected DataSet loadDataSet(Project project)
		throws Exception
	{
		DataSet dataSet = new DataSet();

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
			dataSet.getChromosomeMaps().add(loadChromosomeMap());

		// Number traits
		int traitCount = in.readInt();
		if (DEBUG)
			System.out.println("found " + traitCount + " traits");
		// Trait data
		for (int i = 0; i < traitCount; i++)
			dataSet.getTraits().add(loadTrait(dataSet));

		// Number of lines
		int lineCount = in.readInt();
		if (DEBUG)
			System.out.println("found " + lineCount + " lines");
		for (int i = 0; i < lineCount; i++)
			dataSet.getLines().add(loadLine(dataSet));

		// Number of view sets
		int viewSetCount = in.readInt();
		for (int i = 0; i < viewSetCount; i++)
			dataSet.getViewSets().add(loadGTViewSet(dataSet));

		// Has dummy line?
		in.readBoolean();
//		if (in.readBoolean())
//			dataSet.setDummyLine(loadLine(dataSet));

		loadDBAssociation(dataSet);

		return dataSet;
	}

	protected void saveStateTable(StateTable table)
		throws Exception
	{
		// Number of states
		out.writeInt(table.getStates().size());

		// AlleleState objects
		for (AlleleState state: table.getStates())
		{
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

		// Number of states
		int statesCount = in.readInt();

		// AlleleState objects
		for (int i = 0; i < statesCount; i++)
		{
			AlleleState state = new AlleleState();

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
		out.writeInt(map.getQtls().size());

		// Feature Data
		for (QTL f: map.getQtls())
			saveFeature(f);
	}

	protected ChromosomeMap loadChromosomeMap()
		throws Exception
	{
		ChromosomeMap map = new ChromosomeMap();

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

		return map;
	}

	protected void saveMarker(Marker marker)
		throws Exception
	{
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

		// Name
		marker.setName(readString());

		// Position
		marker.setPosition(in.readFloat());

		// Real position
		marker.setRealPosition(in.readFloat());

		map.getMarkers().add(marker);
	}

	protected void saveFeature(QTL f)
			throws Exception
	{
		QTL feature = (QTL)f;
		// QTL Specific attributes
		// Position
		out.writeFloat(feature.getPosition());

		// Trait
		writeString(feature.getTrait());

		// Experiment
		writeString(feature.getExperiment());

		// VNames
		out.writeInt(feature.getVNames().length);
		for(String vName : feature.getVNames())
			writeString(vName);

		// Values
		out.writeInt(feature.getValues().length);
		for(String value : feature.getValues())
			writeString(value);
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

		//QTL Specific attributes
		// Position
		feature.setPosition(in.readFloat());

		// Trait
		feature.setTrait(readString());

		// Experiment
		feature.setExperiment(readString());

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

		map.getQtls().add(feature);
	}

	protected void saveTrait(Trait trait)
		throws Exception
	{
		// Name
		writeString(trait.getName());

		// Categories
		int noCategories = trait.getCategories().size();
		out.writeInt(noCategories);
		for(int i = 0; i < noCategories; i++)
			writeString(trait.getCategories().get(i));
	}

	protected Trait loadTrait(DataSet dataSet)
		throws Exception
	{
		Trait trait = new Trait();

		// Name
		trait.setName(readString());

		// Categories
		int noCategories = in.readInt();
		for( int i = 0; i < noCategories; i++)
			trait.getCategories().add(readString());

		return trait;
	}

	protected void saveLine(Line line, DataSet dataSet)
		throws Exception
	{
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

	protected Line loadLine(DataSet dataSet)
		throws Exception
	{
		Line line = new Line();

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

		return line;
	}

	protected void saveGenotypeData(GenotypeData data, DataSet dataSet)
		throws Exception
	{
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

	protected GTViewSet loadGTViewSet(DataSet dataSet)
		throws Exception
	{
		GTViewSet viewSet = new GTViewSet();
		// Rebuild the reference to the data set
		viewSet.setDataSet(dataSet);

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
			viewSet.getLines().add(loadLineInfo(dataSet));

		int hidelineInfoCount = in.readInt();
		// Hidden lines LineInfo data
		for (int i = 0; i < hidelineInfoCount; i++)
			viewSet.getHideLines().add(loadLineInfo(dataSet));

		// Number of views
		int viewCount = in.readInt();
		for (int i = 0; i < viewCount; i++)
			viewSet.getViews().add(loadGTView(viewSet, i));

		// Bookmarks
		int bookmarkCount = in.readInt();
		for (int i = 0; i < bookmarkCount; i++)
			loadBookmark(viewSet);

		// Rebuild the comparison line reference
		if (comparisonLineIndex != -1)
			viewSet.setComparisonLine(dataSet.getLines().get(comparisonLineIndex));

		return viewSet;
	}

	protected void saveGTView(GTView view)
		throws Exception
	{
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

	protected GTView loadGTView(GTViewSet viewSet, int index)
		throws Exception
	{
		GTView view = new GTView();
		// Rebuild the reference to the view set
		view.setViewSet(viewSet);

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

		return view;
	}

	protected void saveLineInfo(LineInfo lineInfo)
		throws Exception
	{
		// index
		out.writeInt(lineInfo.getIndex());
		// selected
		out.writeBoolean(lineInfo.getSelected());
		// score
		out.writeFloat(lineInfo.getScore());
	}

	protected LineInfo loadLineInfo(DataSet dataSet)
		throws Exception
	{
		LineInfo lineInfo = new LineInfo();

		// index
		int index = in.readInt();
		lineInfo.setIndex(index);
		// selected
		lineInfo.setSelected(in.readBoolean());
		// score
		lineInfo.setScore(in.readFloat());

		// Rebuild the reference to the Line this LineInfo wraps
		if (index >= 0)
		{
			Line line = dataSet.getLines().get(index);
			lineInfo.setLine(line);
		}

		return lineInfo;
	}

	protected void saveMarkerInfo(MarkerInfo markerInfo)
		throws Exception
	{
		// index
		out.writeInt(markerInfo.getIndex());
		// selected
		out.writeBoolean(markerInfo.getSelected());
	}

	protected void loadMarkerInfo(ArrayList<MarkerInfo> list, ChromosomeMap map)
		throws Exception
	{
		MarkerInfo markerInfo = new MarkerInfo();

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

	protected void loadBookmark(GTViewSet viewSet)
		throws Exception
	{
		Bookmark bookmark = new Bookmark();
		DataSet dataSet = viewSet.getDataSet();

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

	protected void saveDBAssociation(DBAssociation db)
		throws Exception
	{
		writeString(db.getLineSearch());
		writeString(db.getMarkerSearch());
	}

	protected void loadDBAssociation(DataSet dataSet)
		throws Exception
	{
		DBAssociation db = new DBAssociation();

		db.setLineSearch(readString());
		db.setMarkerSearch(readString());

		dataSet.setDbAssociation(db);
	}
}