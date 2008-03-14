package flapjack.data;

import java.util.*;

public class Line
{
	private String name;

	// A unique key assigned to every line
	// TODO: use the primary key from a Germinate DB import
	private int key;

	private Vector<GenotypeData> genotypes = new Vector<GenotypeData>();

	public Line()
	{
	}

	public Line(String name, int key)
	{
		this.name = new String(name);
		this.key = key;
	}


	// Methods required for XML serialization

	public String getName()
		{ return name; }

	public void setName(String name)
		{ this.name = name; }

	public int getKey()
		{ return key; }

	public void setKey(int key)
		{ this.key = key; }

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