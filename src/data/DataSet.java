package flapjack.data;

import java.util.*;

public class DataSet
{
	private Vector<ChromosomeMap> chromosomes = new Vector<ChromosomeMap>();

	private Vector<Line> lines = new Vector<Line>();

	public DataSet()
	{
	}

	public void addLine(Line line)
	{
		lines.add(line);
	}

	/**
	 * Returns the chromosome map with the given name, or creates a new map with
	 * this name if it could not be found.
	 */
	public ChromosomeMap getChromosomeMap(String name, boolean create)
	{
		for (ChromosomeMap map: chromosomes)
			if (map.getName().equals(name))
				return map;

		ChromosomeMap map = new ChromosomeMap(name);
		chromosomes.add(map);

//		System.out.println("Added map " + map + " to dataset");

		return map;
	}

	public void printChromosomeMaps()
	{
		for (ChromosomeMap map: chromosomes)
			map.print();
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