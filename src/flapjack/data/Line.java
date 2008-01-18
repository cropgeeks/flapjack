package flapjack.data;

import java.util.*;

public class Line
{
	private String name;

	private Vector<GenotypeData> genotypes = new Vector<GenotypeData>();

	public Line()
	{
	}

	public Line(String name)
	{
		this.name = new String(name);
	}


	// Methods required for XML serialization

	public String getName()
		{ return name; }

	public void setName(String name)
		{ this.name = name; }

	public Vector<GenotypeData> getGenotypes()
		{ return genotypes; }

	public void setGenotypes(Vector<GenotypeData> genotypes)
		{ this.genotypes = genotypes; }


	// Other methods

	public String toString()
		{ return name; }

	void initializeMap(ChromosomeMap map)
	{
		GenotypeData genoData = new GenotypeData(map);

		genotypes.add(genoData);
	}

	/**
	 * Sets state information for a given loci position within a chromosome map.
	 */
	public void setLoci(int mapIndex, int lociIndex, int stateCode)
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