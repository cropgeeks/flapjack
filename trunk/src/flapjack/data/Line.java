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

	public String toString()
		{ return name; }

	public String getName()
		{ return name; }

	void initializeMap(ChromosomeMap map)
	{
		GenotypeData genoData = new GenotypeData(map);

		genotypes.add(genoData);
	}

	/**
	 * Sets state information for a given loci position within a chromosome map.
	 */
	public void setLoci(int mapIndex, int lociIndex, short stateCode)
	{
		genotypes.get(mapIndex).setLoci(lociIndex, stateCode);
	}

	public GenotypeData getGenotypeDataByMap(ChromosomeMap map)
	{
		for (GenotypeData data: genotypes)
			if (data.isGenotypeDataForMap(map))
				return data;

		return null;
	}
}