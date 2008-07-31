package flapjack.data;

/**
 * Holds information needed by Flapjack to associate the dataset with a database
 * (of some sort).
 */
public class DBAssociation extends XMLRoot
{
	private String lineSearch;
	private String markerSearch;

	public DBAssociation()
	{
	}

	public String getLineSearch()
		{ return lineSearch; }

	public void setLineSearch(String lineSearch)
		{ this.lineSearch = lineSearch; }

	public String getMarkerSearch()
		{ return markerSearch; }

	public void setMarkerSearch(String markerSearch)
		{ this.markerSearch = markerSearch; }
}