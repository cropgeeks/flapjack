// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data;

import java.util.*;
import java.util.stream.*;

import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.table.*;

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
	// Textual traits are tracked separately
	private int[] txtTraits = new int[0];

	private int[] linkedModelCols = new int [0];

	// Display line "scores" after performing a sort?
	private boolean displayLineScores = false;

	private int[] graphs = { 0, -1, -1 };

	private UndoManager undoManager = new UndoManager();
	private LinkedTableHandler tableHandler = new LinkedTableHandler(this);

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
		{
			GTView view = new GTView(this, dataSet.getMapByIndex(i), true);
			view.linkMarkerInfos();
			views.add(view);
		}

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

		// 18/08/2016 - Added lineInfo.filtered for lines hidden or filtered in
		// a linked-table view. For existing projects with hidden lines, set
		// this flag to true on those lines
		for (LineInfo lineInfo: hideLines)
			if (lineInfo.getVisibility() == LineInfo.VISIBLE)
				lineInfo.setVisibility(lineInfo.HIDDEN);
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

	public String getSelectedTxtTraits()
		{ return MatrixXML.arrayToString(txtTraits); }

	public void setSelectedTxtTraits(String txtTraitsStr)
		{ this.txtTraits = MatrixXML.stringToIntArray(txtTraitsStr); }

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

	public LinkedTableHandler getTableHandler()
		{ return tableHandler; }

	public void setTableHandler(LinkedTableHandler tableHandler)
		{ this.tableHandler = tableHandler; }

	// Transient (non marshalled) methods

	public int[] getTraits()
		{ return traits; }

	public void setTraits(int[] traits)
		{ this.traits = traits; }

	public int[] getTxtTraits()
		{ return txtTraits; }

	public void setTxtTraits(int[] txtTraits)
		{ this.txtTraits = txtTraits; }

	public int[] getGraphs()
		{ return graphs; }

	public void setGraphs(int[] graphs)
		{ this.graphs = graphs; }

	public int[] getLinkedModelCols()
		{ return linkedModelCols; }

	public void setLinkedModelCols(int[] linkedModelCols)
		{ this. linkedModelCols = linkedModelCols; }

	// Other methods

	public LinkedTableHandler tableHandler()
		{ return tableHandler; }

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

	public ArrayList<LineInfo> copyLines(int visibility)
	{
		if (visibility == LineInfo.HIDDEN)
			return hideLines.stream()
				.filter(li -> li.visibility == LineInfo.HIDDEN)
				.collect(Collectors.toCollection(ArrayList::new));
		else if (visibility == LineInfo.FILTERED)
			return hideLines.stream()
				.filter(li -> li.visibility == LineInfo.FILTERED)
				.collect(Collectors.toCollection(ArrayList::new));

		return lines.stream()
			.collect(Collectors.toCollection(ArrayList::new));
	}

	public void setLinesFromCopies(ArrayList<LineInfo> lines, ArrayList<LineInfo> hidden, ArrayList<LineInfo> filtered)
	{
		this.lines.clear();
		this.lines.addAll(lines);
		lines.forEach(li -> li.visibility = LineInfo.VISIBLE);

		if (hidden != null && filtered != null)
		{
			hideLines.clear();
			hideLines.addAll(hidden);
			hideLines.addAll(filtered);

			hidden.forEach(li -> li.visibility = LineInfo.HIDDEN);
			filtered.forEach(li -> li.visibility = LineInfo.FILTERED);
		}

		tableHandler.copyViewToTable(true);
	}

	public UndoManager getUndoManager()
		{ return undoManager; }

	public String toString()
		{ return name; }

	public GTViewSet createClone(String cloneName, boolean selectedLMOnly)
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
		clone.txtTraits = new int[txtTraits.length];
		for (int i = 0; i < txtTraits.length; i++)
			clone.txtTraits[i] = txtTraits[i];

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
		{
			GTView viewClone = views.get(i).createClone(clone, selectedLMOnly);
			viewClone.linkMarkerInfos();
			clone.views.add(viewClone);
		}

		clone.removeAllDummyLines();
		clone.removeAllDuplicates();
		clone.removeSortSplitter();

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

		tableHandler.copyViewToTable(true);
	}

	public void removeAllDummyLines()
	{
		// Search backwards, stripping out each dummy line as it is found
		for (int i = lines.size()-1; i >= 0; i--)
			if (lines.get(i).line == dataSet.getDummyLine())
				lines.remove(i);

		tableHandler.copyViewToTable(false);
	}

	public void removeAllDuplicates()
	{
		// Search backwards, stripping out each duplicate line as it is found
		for (int i = lines.size()-1; i >= 0; i--)
			if (lines.get(i).getDuplicate())
				lines.remove(i);

		tableHandler.copyViewToTable(false);
	}

	public void removeSortSplitter()
	{
		for (int i = 0; i < lines.size(); i++)
			if (lines.get(i).getLine() == dataSet.getSplitter())
			{
				lines.remove(i);
				break;
			}
	}

	public void duplicateLine(int index)
	{
		LineInfo original = lines.get(index);
		LineInfo duplicate = original.makeDuplicate();

		lines.add(index+1, duplicate);

		tableHandler.copyViewToTable(true);
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

		tableHandler.copyViewToTable(true);
	}

	public void moveLine(int fromIndex, int toIndex)
	{
		// Check we're not out of bounds
		if (toIndex < 0 || fromIndex < 0)
			return;
		if (toIndex >= lines.size() || fromIndex >= lines.size())
			return;

		// Swap the lines
		LineInfo oldValue = lines.get(fromIndex);
		lines.set(fromIndex, lines.get(toIndex));
		lines.set(toIndex, oldValue);

		// But also check and deal with the comparison line being moved
		if (comparisonLineIndex == fromIndex)
			comparisonLineIndex = toIndex;
		else if (comparisonLineIndex == toIndex)
			comparisonLineIndex = fromIndex;

		tableHandler.copyViewToTable(true);
	}

	/** Restores all hidden lines to the view. */
	public void restoreHiddenLines()
	{
		// Restore manually hidden lines to the visible list and remove them
		// from the hidden list. Update the LineInfo's visibility state as part
		// of this
		for (LineInfo lineInfo: hideLines)
			if (lineInfo.getVisibility() == LineInfo.HIDDEN)
				lines.add(lineInfo);
		hideLines.removeIf(li -> li.visibility == LineInfo.HIDDEN);

		for (LineInfo lineInfo: lines)
			lineInfo.setVisibility(LineInfo.VISIBLE);

		tableHandler().copyViewToTable(false);
		// The restored line(s) could be in the wrong place, or not filtered out
		// unless we force the table (which will have sorted/filtered them) to
		// reapply *its* view. This is a special case (so far) we don't usually
		// do this
		tableHandler().copyTableToView();
	}

	public long hiddenLineCount()
	{
		return hideLines.stream()
			.filter(li -> li.getVisibility() == LineInfo.HIDDEN)
			.count();
	}
}