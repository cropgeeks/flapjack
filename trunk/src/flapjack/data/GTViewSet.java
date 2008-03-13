package flapjack.data;

import java.util.*;

/**
 * Represents a "set" of views for the genotype visualizations - basically one
 * view per chromosome for the set.
 */
public class GTViewSet
{
	private Vector<GTView> views = new Vector<GTView>();

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

		for (int i = 0; i < dataSet.countChromosomeMaps(); i++)
			views.add(new GTView(dataSet, dataSet.getMapByIndex(i)));
	}


	// Methods required for XML serialization

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
			view.recreateReferences(dataSet, map);
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