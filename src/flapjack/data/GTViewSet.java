package flapjack.data;

import java.util.*;

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

	private String name;
	private int viewIndex;

	// Color model in use
	private int colorScheme;
	// Cutoff threshold if using the allele frequency color scheme
	private float alleleFrequencyThreshold = 0.05f;
	// A random seed that will be used if the random colour scheme is selected
	private int randomColorSeed;

	// For comparisons between lines, we need to know the line itself:
	Line comparisonLine;
	// And its current index
	int comparisonLineIndex;

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


	// Other methods

	public void addView(GTView view)
	{
		views.add(view);
	}

	public int getChromosomeCount()
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

	public GTView getSelectedView()
	{
		return views.get(viewIndex);
	}

	/**
	 * Converts and returns the vector of line data into a primitive array of
	 * ints.
	 */
	public LineInfo[] getLinesAsArray()
	{
		LineInfo[] array = new LineInfo[lines.size()];

		for (int i = 0; i < array.length; i++)
			array[i] = lines.get(i);

		return array;
	}

	public void setLinesFromArray(LineInfo[] array)
	{
		lines.clear();

		for (LineInfo li: array)
			lines.add(li);
	}

	public UndoManager getUndoManager()
		{ return undoManager; }

	public String toString()
		{ return name; }

	public GTViewSet createClone(String cloneName)
	{
		GTViewSet clone = new GTViewSet(dataSet, cloneName);

		// Copy over the color data
		clone.colorScheme = colorScheme;
		clone.alleleFrequencyThreshold = alleleFrequencyThreshold;
		clone.randomColorSeed = randomColorSeed;

		// Copy over the line data
		clone.setLinesFromArray(getLinesAsArray());
		clone.comparisonLine = comparisonLine;
		clone.comparisonLineIndex = comparisonLineIndex;

		// Copy over the chromosomes views
		clone.views.clear();
		for (GTView view: views)
			clone.views.add(view.createClone(clone));

		return clone;
	}

	/**
	 * Returns the total number of markers across all chromosomes of this set.
	 */
	public int getMarkerCount()
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

	/** Returns the total number of alleles references by this viewset.
	 */
	public int getAlleleCount()
	{
		int total = 0;
		for (GTView view: views)
			total += view.getMarkerCount() * view.getLineCount();

		return total;
	}
}