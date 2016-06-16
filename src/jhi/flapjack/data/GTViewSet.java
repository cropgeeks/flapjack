// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data;

import java.util.*;

import jhi.flapjack.data.results.*;

import scri.commons.*;

/**
 * Represents a "set" of views for the genotype visualizations - basically one
 * view per chromosome for the set.
 */
public class GTViewSet extends XMLRoot
{
	// Track a reference to the owning dataSet
	private DataSet dataSet;

	private ArrayList<GTView> views = new ArrayList<>();
	private CustomMaps customMaps = new CustomMaps();

	// Because the line info is the same across all views, it gets stored here
	// Holds the index positions of the lines as they appear in the actual
	// dataset's vector of lines
	ArrayList<LineInfo> lines = new ArrayList<>();
	// Holds the lines that we don't currently want visible
	ArrayList<LineInfo> hideLines = new ArrayList<>();

	// Any bookmarks associated with this viewset
	private ArrayList<Bookmark> bookmarks = new ArrayList<>();
	// A list of similarity matrices generated from this view
	private ArrayList<SimMatrix> matrices = new ArrayList<>();
	// A list of dendrograms generated from this view
	private ArrayList<Dendrogram> dendrograms = new ArrayList<>();

	private String name;
	private int viewIndex;

	// Color model in use
	private int colorScheme;
	// Cutoff threshold if using the allele frequency color scheme
	private float alleleFrequencyThreshold = 0.05f;
	// A random seed that will be used if the random colour scheme is selected
	private int randomColorSeed = (int)(Math.random()*50000);

	// For comparisons between lines, we need to know the line itself:
	Line comparisonLine;
	// And its current index
	int comparisonLineIndex;

	// If traits are being displayed, which ones?
	private int[] traits = new int[0];// { -1, -1, -1 };

	// Display line "scores" after performing a sort?
	private boolean displayLineScores = false;

	private int[] graphs = { 0, -1, -1 };

	private UndoManager undoManager = new UndoManager();

	public GTViewSet()
	{
	}

	/**
	 * Constructs a new set of views for the dataset (one view per chromosome).
	 */
	public GTViewSet(DataSet dataSet, String name)
	{
		this.dataSet = dataSet;
		this.name = name;

		// For each (original) line in the dataset, we add the index of it to
		// the mapping for this viewset
		lines = new ArrayList<LineInfo>(dataSet.countLines());
		for (int i = 0; i < dataSet.countLines(); i++)
		{
			Line line = dataSet.getLineByIndex(i);
			lines.add(new LineInfo(line, i));
		}

		for (int i = 0; i < dataSet.countChromosomeMaps(); i++)
			views.add(new GTView(this, dataSet.getMapByIndex(i), true));

		// For a genuine new view on a new data import, this will have no effect
		// but for new (additional) views, traits may exist that can be used
		assignTraits();
	}

	void validate()
		throws NullPointerException
	{
		if (dataSet == null || name == null || lines == null)
			throw new NullPointerException();

		for (GTView view: views)
			view.validate();

		// This rebuilds the references between dummy and splitter LineInfos and
		// the actual objects held by the DataSet (because they are not part of
		// the saved project)
		for (LineInfo info : lines)
		{
			if (info.index == -1)
				info.line = dataSet.getDummyLine();
			else if (info.index == -2)
				info.line = dataSet.getSplitter();
		}
	}


	// Methods required for XML serialization

	public DataSet getDataSet()
		{ return dataSet; }

	public void setDataSet(DataSet dataSet)
		{ this.dataSet = dataSet; }

	public ArrayList<LineInfo> getLines()
		{ return lines; }

	public void setLines(ArrayList<LineInfo> lines)
		{ this.lines = lines; }

	public ArrayList<LineInfo> getHideLines()
		{ return hideLines; }

	public void setHideLines(ArrayList<LineInfo> hideLines)
		{ this.hideLines = hideLines; }

	public String getName()
		{ return name; }

	public void setName(String name)
		{ this.name = name; }

	public int getViewIndex()
		{ return viewIndex; }

	public void setViewIndex(int viewIndex)
		{ this.viewIndex = viewIndex; }

	public ArrayList<GTView> getViews()
		{ return views; }

	public void setViews(ArrayList<GTView> views)
	{ this.views = views; }

	public CustomMaps getCustomMaps()
		{ return customMaps; }

	public void setCustomMaps(CustomMaps customMaps)
		{ this.customMaps = customMaps; }

	public ArrayList<Bookmark> getBookmarks()
		{ return bookmarks; }

	public void setBookmarks(ArrayList<Bookmark> bookmarks)
		{ this.bookmarks = bookmarks; }

	public int getColorScheme()
		{ return colorScheme; }

	public void setColorScheme(int colorScheme)
		{ this.colorScheme = colorScheme; }

	public int getRandomColorSeed()
		{ return randomColorSeed; }

	public void setRandomColorSeed(int randomColorSeed)
		{ this.randomColorSeed = randomColorSeed; }

	public Line getComparisonLine()
		{ return comparisonLine; }

	public void setComparisonLine(Line comparisonLine)
		{ this.comparisonLine = comparisonLine; }

	public int getComparisonLineIndex()
		{ return comparisonLineIndex; }

	public void setComparisonLineIndex(int comparisonLineIndex)
		{ this.comparisonLineIndex = comparisonLineIndex; }

	public float getAlleleFrequencyThreshold()
		{ return alleleFrequencyThreshold; }

	public void setAlleleFrequencyThreshold(float alleleFrequencyThreshold)
		{ this.alleleFrequencyThreshold = alleleFrequencyThreshold; }

	public String getSelectedTraits()
		{ return MatrixXML.arrayToString(traits); }

	public void setSelectedTraits(String traitsStr)
		{ this.traits = MatrixXML.stringToIntArray(traitsStr); }

	public boolean getDisplayLineScores()
		{ return displayLineScores; }

	public void setDisplayLineScores(boolean displayLineScores)
		{ this.displayLineScores = displayLineScores; }

	public String getSelectedGraphs()
		{ return MatrixXML.arrayToString(graphs); }

	public void setSelectedGraphs(String graphs)
		{ this.graphs = MatrixXML.stringToIntArray(graphs); }

	public ArrayList<SimMatrix> getMatrices()
		{ return matrices; }

	public void setMatrices(ArrayList<SimMatrix> matrices)
		{ this.matrices = matrices; }

	public ArrayList<Dendrogram> getDendrograms()
		{ return dendrograms; }

	public void setDendrograms(ArrayList<Dendrogram> dendrograms)
		{ this.dendrograms = dendrograms; }


	// Transient (non marshalled) methods

	public int[] getTraits()
		{ return traits; }

	public void setTraits(int[] traits)
		{ this.traits = traits; }

	public int[] getGraphs()
		{ return graphs; }

	public void setGraphs(int[] graphs)
		{ this.graphs = graphs; }


	// Other methods

	public void addView(GTView view)
	{
		views.add(view);
	}

	public int chromosomeCount()
	{
		return views.size();
	}

	/**
	 * Returns the view at this index position.
	 */
	public GTView getView(int index)
	{
		return views.get(index);
	}

	/**
	 * Returns the view that corresponds to this chromsome map.
	 */
	public GTView getView(ChromosomeMap map)
	{
		for (GTView view: views)
			if (view.getChromosomeMap() == map)
				return view;

		return null;
	}

	/**
	 * Converts and returns the vector of line data into a primitive array of
	 * ints.
	 */
	public LineInfo[] getLinesAsArray(boolean getVisible)
	{
		if (getVisible)
			return lines.toArray(new LineInfo[] {});
		else
			return hideLines.toArray(new LineInfo[] {});
	}

	public void setLinesFromArray(LineInfo[] array, boolean setVisible)
	{
		if (setVisible)
		{
			lines.clear();
			for (LineInfo li: array)
				lines.add(li);
		}
		else
		{
			hideLines.clear();
			for (LineInfo li: array)
				hideLines.add(li);
		}
	}

	public UndoManager getUndoManager()
		{ return undoManager; }

	public String toString()
		{ return name; }

	public GTViewSet createClone(String cloneName, boolean selectedLMOnly, boolean[] selectedChromosomes)
	{
		GTViewSet clone = new GTViewSet(dataSet, cloneName);

		// Copy over the color data
		clone.colorScheme = colorScheme;
		clone.alleleFrequencyThreshold = alleleFrequencyThreshold;
		clone.randomColorSeed = randomColorSeed;
		clone.graphs = graphs;

		// Copy over the trait indices
		clone.traits = new int[traits.length];
		for (int i = 0; i < traits.length; i++)
			clone.traits[i] = traits[i];

		// Copy over the line data
		clone.lines.clear();
		for (LineInfo lineInfo : lines)
			if (!selectedLMOnly || (selectedLMOnly && lineInfo.getSelected()))
				clone.lines.add(new LineInfo(lineInfo));

		// Copy over the hidden line data
		if (selectedLMOnly == false)
			for (LineInfo lineInfo : hideLines)
				clone.hideLines.add(new LineInfo(lineInfo));

		clone.comparisonLine = comparisonLine;
		clone.comparisonLineIndex = comparisonLineIndex;

		// Copy over the chromosomes views
		clone.views.clear();
		for (int i=0; i < views.size(); i++)
			if (selectedChromosomes == null || selectedChromosomes[i])
				clone.views.add(views.get(i).createClone(clone, selectedLMOnly));

		return clone;
	}

	/**
	 * Returns the total number of markers across all chromosomes of this set.
	 */
	public int countAllMarkers()
	{
		int count = 0;
		for (GTView view: views)
			count += view.markerCount();

		return count;
	}

	/**
	 * Returns the index position of the given line, or -1 if it wasn't found.
	 */
	public int indexOf(Line line)
	{
		for (int i = 0; i < lines.size(); i++)
			if (lines.get(i).line == line)
				return i;

		return -1;
	}

	public int indexOf(LineInfo line)
	{
		return lines.indexOf(line);
	}

	public int indexof(GTView view)
	{
		for (int i = 0; i < views.size(); i++)
			if (views.get(i) == view)
				return i;

		return -1;
	}

	/**
	 * Maps trait column indices onto a view after trait data has been loaded.
	 * Assuming enough traits are available, and no previous indices were set,
	 * the viewSet will be told to display the first three traits: 0, 1, and 2.
	 */
	public void assignTraits()
	{
		if (traits.length > 0)
			return;

		// Don't assign any more than 3 (but it might be less)
		int count = dataSet.getTraits().size();
		int size = count < 3 ? count: 3;

		traits = new int[size];

		// For each column - if it's not been assigned yet (and there is a
		// trait available for that column)...
		for (int i = 0; i < traits.length; i++)
			traits[i] = i;
	}

	/**
	 * Inserts a "dummy" line into this viewset's line array.
	 */
	public void insertDummyLine(int index)
	{
		Line dummy = dataSet.getDummyLine();

		if (dummy == null)
		{
			Line line = lines.get(index).line;
			dummy = line.createDummy();

			dataSet.setDummyLine(dummy);
		}

		lines.add(index, new LineInfo(dummy, -1));
	}

	public void removeAllDummyLines()
	{
		// Search backwards, stripping out each dummy line as it is found
		for (int i = lines.size()-1; i >= 0; i--)
			if (lines.get(i).line == dataSet.getDummyLine())
				lines.remove(i);
	}

	public void removeAllDuplicates()
	{
		// Search backwards, stripping out each duplicate line as it is found
		for (int i = lines.size()-1; i >= 0; i--)
			if (lines.get(i).getDuplicate())
				lines.remove(i);
	}

	public void duplicateLine(int index)
	{
		LineInfo original = lines.get(index);
		LineInfo duplicate = original.makeDuplicate();

		lines.add(index+1, duplicate);
	}

	public void insertSplitterLine(int index)
	{
		Line splitter = dataSet.getSplitter();

		if (splitter == null)
		{
			Line line = lines.get(index).line;
			splitter = line.createDummy();

			dataSet.setSplitter(splitter);
		}

		lines.add(index, new LineInfo(splitter, -2));
	}
}