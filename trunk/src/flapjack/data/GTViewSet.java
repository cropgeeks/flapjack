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
	Vector<Integer> lines;

	private String name;
	private int viewIndex;

	// Color model in use
	private int colorScheme;

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
		lines = new Vector<Integer>(dataSet.countLines());
		for (int i = 0; i < dataSet.countLines(); i++)
			lines.add(i);

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

	public Vector<Integer> getLines()
		{ return lines; }

	public void setLines(Vector<Integer> lines)
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

	public Line getComparisonLine()
		{ return comparisonLine; }

	public void setComparisonLine(Line comparisonLine)
		{ this.comparisonLine = comparisonLine; }

	public int getComparisonLineIndex()
		{ return comparisonLineIndex; }

	public void setComparisonLineIndex(int comparisonLineIndex)
		{ this.comparisonLineIndex = comparisonLineIndex; }


	// Other methods

	public void addView(GTView view)
	{
		views.add(view);
	}

	public int getChromosomeCount()
	{
		return views.size();
	}

	public GTView getView(int index)
	{
		return views.get(index);
	}

	public GTView getSelectedView()
	{
		return views.get(viewIndex);
	}

	/**
	 * Converts and returns the vector of line data into a primitive array of
	 * ints.
	 */
	public int[] getLinesAsArray()
	{
		int[] array = new int[lines.size()];

		for (int i = 0; i < array.length; i++)
			array[i] = lines.get(i);

		return array;
	}

	public void setLinesFromArray(int[] array)
	{
		lines.clear();

		for (int i: array)
			lines.add(i);
	}

	public UndoManager getUndoManager()
		{ return undoManager; }

	public String toString()
		{ return name; }

	public GTViewSet createClone(String cloneName)
	{
		GTViewSet clone = new GTViewSet(dataSet, cloneName);

		clone.colorScheme = colorScheme;

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
}