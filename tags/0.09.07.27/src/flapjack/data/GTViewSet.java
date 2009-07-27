package flapjack.data;

import java.util.*;

import scri.commons.*;

/**
 * Represents a "set" of views for the genotype visualizations - basically one
 * view per chromosome for the set.
 */
public class GTViewSet extends XMLRoot
{
	// Track a reference to the owning dataSet
	private DataSet dataSet;

	private Vector<GTView> views = new Vector<GTView>();

	// Because the line info is the same across all views, it gets stored here
	// Holds the index positions of the lines as they appear in the actual
	// dataset's vector of lines
	Vector<LineInfo> lines;
	// Holds the lines that we don't currently want visible
	Vector<LineInfo> hideLines = new Vector<LineInfo>();

	// Any bookmarks associated with this viewset
	private Vector<Bookmark> bookmarks = new Vector<Bookmark>();

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
	private int[] traits = new int[] { -1, -1, -1 };

	// Display line "scores" after performing a sort?
	private boolean displayLineScores = false;

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
		lines = new Vector<LineInfo>(dataSet.countLines());
		for (int i = 0; i < dataSet.countLines(); i++)
		{
			Line line = dataSet.getLineByIndex(i);
			lines.add(new LineInfo(line, i));
		}

		for (int i = 0; i < dataSet.countChromosomeMaps(); i++)
			views.add(new GTView(this, dataSet.getMapByIndex(i)));

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
	}


	// Methods required for XML serialization

	public DataSet getDataSet()
		{ return dataSet; }

	public void setDataSet(DataSet dataSet)
		{ this.dataSet = dataSet; }

	public Vector<LineInfo> getLines()
		{ return lines; }

	public void setLines(Vector<LineInfo> lines)
		{ this.lines = lines; }

	public Vector<LineInfo> getHideLines()
		{ return hideLines; }

	public void setHideLines(Vector<LineInfo> hideLines)
		{ this.hideLines = hideLines; }

	public String getName()
		{ return name; }

	public void setName(String name)
		{ this.name = name; }

	public int getViewIndex()
		{ return viewIndex; }

	public void setViewIndex(int viewIndex)
		{ this.viewIndex = viewIndex; }

	public Vector<GTView> getViews()
		{ return views; }

	public void setViews(Vector<GTView> views)
		{ this.views = views; }

	public Vector<Bookmark> getBookmarks()
		{ return bookmarks; }

	public void setBookmarks(Vector<Bookmark> bookmarks)
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

	public String getTraitsString()
		{ return MatrixXML.arrayToString(traits); }

	public void setTraitsString(String traitsStr)
		{ this.traits = MatrixXML.stringToIntArray(traitsStr); }

	public boolean getDisplayLineScores()
		{ return displayLineScores; }

	public void setDisplayLineScores(boolean displayLineScores)
		{ this.displayLineScores = displayLineScores; }


	// Other methods

	public int[] getTraits()
		{ return traits; }

	public void setTraits(int[] traits)
		{ this.traits = traits; }

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

	public GTViewSet createClone(String cloneName, boolean cloneHidden)
	{
		GTViewSet clone = new GTViewSet(dataSet, cloneName);

		// Copy over the color data
		clone.colorScheme = colorScheme;
		clone.alleleFrequencyThreshold = alleleFrequencyThreshold;
		clone.randomColorSeed = randomColorSeed;

		// Copy over the trait indices
		for (int i = 0; i < traits.length; i++)
			clone.traits[i] = traits[i];

		// Copy over the line data
		clone.setLinesFromArray(getLinesAsArray(true), true);
		// Copy over the hidden line data
		if (cloneHidden)
			clone.setLinesFromArray(getLinesAsArray(false), false);
		clone.comparisonLine = comparisonLine;
		clone.comparisonLineIndex = comparisonLineIndex;

		// Copy over the chromosomes views
		clone.views.clear();
		for (GTView view: views)
			clone.views.add(view.createClone(clone, cloneHidden));

		return clone;
	}

	/**
	 * Returns the total number of markers across all chromosomes of this set.
	 */
	public int countAllMarkers()
	{
		int count = 0;
		for (GTView view: views)
			count += view.getMarkerCount();

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

	public int indexof(GTView view)
	{
		for (int i = 0; i < views.size(); i++)
			if (views.get(i) == view)
				return i;

		return -1;
	}

	/** Returns the total number of alleles referenced by this viewset.
	 */
	public int countAllAlleles()
	{
		int total = 0;
		for (GTView view: views)
			total += view.getMarkerCount() * view.getLineCount();

		return total;
	}

	/**
	 * Maps trait column indices onto a view after trait data has been loaded.
	 * Assuming enough traits are available, and no previous indices were set,
	 * the viewSet will be told to display traits 0, 1, and 2.
	 */
	public void assignTraits()
	{
		int count = dataSet.getTraits().size();

		// For each column - if it's not been assigned yet (and there is a
		// trait available for that column)...
		for (int i = 0; i < traits.length && traits[i] == -1 && i < count; i++)
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

		lines.insertElementAt(new LineInfo(dummy, -1), index);
	}

	public void removeAllDummyLines()
	{
		// Search backwards, stripping out each dummy line as it is found
		for (int i = lines.size()-1; i >= 0; i--)
			if (lines.get(i).line == dataSet.getDummyLine())
				lines.removeElementAt(i);
	}
}