package flapjack.data;

import java.util.*;

/**
 * Represents a "set" of views for the genotype visualizations - basically one
 * view per chromosome for the set.
 */
public class GTViewSet
{
	private Vector<GTView> views = new Vector<GTView>();

	// Because the line info is the same across all views, it gets stored here
	// Holds the index positions of the lines as they appear in the actual
	// dataset's vector of lines
	private Vector<Integer> lines;

	private String name;
	private int viewIndex;

	// Color model in use
	private int colorScheme;

	public GTViewSet()
	{
	}

	/**
	 * Constructs a new set of views for the dataset (one view per chromosome).
	 */
	public GTViewSet(DataSet dataSet, String name)
	{
		this.name = name;

		// For each (original) line in the dataset, we add the index of it to
		// the mapping for this viewset
		lines = new Vector<Integer>(dataSet.countLines());
		for (int i = 0; i < dataSet.countLines(); i++)
			lines.add(i);

		for (int i = 0; i < dataSet.countChromosomeMaps(); i++)
			views.add(new GTView(dataSet, dataSet.getMapByIndex(i), lines));
	}


	// Methods required for XML serialization

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


	// Other methods

	void recreateReferences(DataSet dataSet)
	{
		for (GTView view: views)
		{
			ChromosomeMap map = dataSet.getMapByName(view.getMapName(), false);
			view.recreateReferences(dataSet, map, lines);
		}
	}

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
}