package flapjack.data;

import java.util.*;

public class DataSet extends XMLRoot
{
	private String name;

	// Data information
	private Vector<ChromosomeMap> chromosomes = new Vector<ChromosomeMap>();
	private Vector<Line> lines = new Vector<Line>();

	// View information
	private StateTable stateTable = new StateTable(0);
	private Vector<GTViewSet> viewSets = new Vector<GTViewSet>();

	public DataSet()
	{
	}

	void validate()
		throws NullPointerException
	{
		if (name == null)
			throw new NullPointerException();

		for (ChromosomeMap map: chromosomes)
			map.validate();
		for (Line line: lines)
			line.validate();

		stateTable.validate();
		for (GTViewSet viewSet: viewSets)
			viewSet.validate();
	}


	// Methods required for XML serialization

	public String getName()
		{ return name; }

	public void setName(String name)
		{ this.name = name; }

	public Vector<ChromosomeMap> getChromosomeMaps()
		{ return chromosomes; }

	public void setChromosomeMaps(Vector<ChromosomeMap> chromosomes)
		{ this.chromosomes = chromosomes; }

	public Vector<Line> getLines()
		{ return lines; }

	public void setLines(Vector<Line> lines)
		{ this.lines = lines; }

	public StateTable getStateTable()
		{ return stateTable; }

	public void setStateTable(StateTable stateTable)
		{ this.stateTable = stateTable; }

	public Vector<GTViewSet> getViewSets()
		{ return viewSets; }

	public void setViewSets(Vector<GTViewSet> viewSets)
		{ this.viewSets = viewSets; }


	// Other methods

	public Line createLine(String name)
	{
		// TODO: Check for duplicate lines
		Line line = new Line(name, lines.size());

		// This ensures each line has a set of (empty) loci data for each map
		for (ChromosomeMap map: chromosomes)
			line.initializeMap(map);

		lines.add(line);

		return line;
	}

	public ChromosomeMap getMapByIndex(int index)
		throws ArrayIndexOutOfBoundsException
	{
		return chromosomes.get(index);
	}

	public Line getLineByIndex(int index)
		throws ArrayIndexOutOfBoundsException
	{
		return lines.get(index);
	}

	/**
	 * Returns the chromosome map with the given name, or creates a new map with
	 * this name if it could not be found.
	 */
	public ChromosomeMap getMapByName(String mapName, boolean create)
	{
		for (ChromosomeMap map: chromosomes)
			if (map.getName().equals(mapName))
				return map;

		ChromosomeMap map = new ChromosomeMap(mapName);
		chromosomes.add(map);

//		System.out.println("Added map " + map + " to dataset");

		return map;
	}

	public int getMapIndexByMarkerName(String markerName)
	{
		for (ChromosomeMap map: chromosomes)
			if (map.containsMarker(markerName))
				return chromosomes.indexOf(map);

		return -1;
	}

	public int countChromosomeMaps()
	{
		return chromosomes.size();
	}

	public int countLines()
	{
		return lines.size();
	}

	/**
	 * Returns the total number of markers across all maps.
	 */
	public int countMarkers()
	{
		int count = 0;
		for (ChromosomeMap map: chromosomes)
			count += map.countLoci();

		return count;
	}

	/**
	 * Sorts the markers within each chromosome map so that they are held in
	 * ascending position order.
	 */
	public void sortChromosomeMaps()
	{
		for (ChromosomeMap map: chromosomes)
			map.sort();
	}
}