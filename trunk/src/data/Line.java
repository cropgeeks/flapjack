package flapjack.data;

import java.util.*;

public class Line
{
	private String name;

	private Vector<GenotypeData> genotypes = new Vector<GenotypeData>();

	public Line(String name)
	{
		this.name = name;
	}

	void initializeMap(ChromosomeMap map)
	{
		GenotypeData genoData = new GenotypeData(map);

		genotypes.add(genoData);
	}

	/**
	 * Sets state information for a given loci position within a chromosome map.
	 */
	public void setLoci(int mapIndex, int lociIndex, short[] states)
	{
		genotypes.get(mapIndex).setLoci(lociIndex, states);
	}

	public void print()
	{
		System.out.print("Line " + name + " with " + genotypes.size() + " maps");

		for (GenotypeData data: genotypes)
			data.print();
	}

	void printSummary(ChromosomeMap map)
	{
		System.out.print(name + "\t");

		for (GenotypeData data: genotypes)
		{
			if (data.isGenotypeDataForMap(map))
			{
				data.print();
			}
		}
	}
}