package flapjack.data;

import java.util.*;

public class DataSet
{
	private Vector<ChromosomeMap> chromosomes = new Vector<ChromosomeMap>();

	private Vector<Line> lines = new Vector<Line>();

	private StateTable stateTable = new StateTable();

	public DataSet()
	{
	}

	public StateTable getStateTable()
	{
		return stateTable;
	}

	public Line createLine(String name)
	{
		// TODO: Check for duplicate lines
		Line line = new Line(name);

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

	public ChromosomeMap getMapByMarkerName(String markerName)
	{
		for (ChromosomeMap map: chromosomes)
			if (map.containsMarker(markerName))
				return map;

		return null;
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