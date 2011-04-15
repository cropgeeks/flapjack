package flapjack.data;

import java.util.*;

public class Line extends XMLRoot
{
	private String name;

	// A unique key assigned to every line
	// TODO: use the primary key from a Germinate DB import
	private int dbKey;

	private Vector<GenotypeData> genotypes = new Vector<GenotypeData>();

	public Line()
	{
	}

	public Line(String name, int dbKey)
	{
		this.name = new String(name);
		this.dbKey = dbKey;
	}

	void validate()
		throws NullPointerException
	{
		if (name == null)
			throw new NullPointerException();

		for (GenotypeData data: genotypes)
			data.validate();
	}


	// Methods required for XML serialization

	public String getName()
		{ return name; }

	public void setName(String name)
		{ this.name = name; }

	public int getDbKey()
		{ return dbKey; }

	public void setDbKey(int dbKey)
		{ this.dbKey = dbKey; }

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

	/**
	 * Collapses all genotype data (across all chromosomes) so that all
	 * instances of the s2 state are replaced by the s1 state.
	 */
	public void collapseStates(int s1, int s2)
	{
		for (GenotypeData data: genotypes)
			data.collapseStates(s1, s2);
	}

	public GenotypeData getGenotypeDataByMap(ChromosomeMap map)
	{
		for (GenotypeData data: genotypes)
			if (data.isGenotypeDataForMap(map))
				return data;

		return null;
	}

	/**
	 * Returns the allele (state) for the given chromosome and marker indicies.
	 */
	public int getState(int chromosome, int marker)
	{
		return genotypes.get(chromosome).getState(marker);
	}
}