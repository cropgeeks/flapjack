// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data;

import java.util.*;

public class Line extends XMLRoot
{
	private String name;

	private ArrayList<GenotypeData> genotypes = new ArrayList<>();

	// Trait information (one trait value per trait associated with this line)
	private ArrayList<TraitValue> traitValues = new ArrayList<>();

	public Line()
	{
	}

	public Line(String name)
	{
		this.name = new String(name);
	}

	void validate()
		throws NullPointerException
	{
		if (name == null)
			throw new NullPointerException();

		for (GenotypeData data: genotypes)
			data.validate();
		for (TraitValue traitValue: traitValues)
			traitValue.validate();
	}


	// Methods required for XML serialization

	public String getName()
		{ return name; }

	public void setName(String name)
		{ this.name = name; }

	public ArrayList<GenotypeData> getGenotypes()
		{ return genotypes; }

	public void setGenotypes(ArrayList<GenotypeData> genotypes)
		{ this.genotypes = genotypes; }

	public ArrayList<TraitValue> getTraitValues()
		{ return traitValues; }

	public void setTraitValues(ArrayList<TraitValue> traitValues)
		{ this.traitValues = traitValues; }


	// Other methods

	public String toString()
		{ return name; }

	void initializeMap(ChromosomeMap map, boolean useByteStorage)
	{
		GenotypeData genoData = new GenotypeData(map, useByteStorage);

		genotypes.add(genoData);
	}

	/** Returns true if byte (rather than int) storage is in use. */
	boolean useByteStorage()
	{
		if (genotypes.size() > 0)
			return genotypes.get(0).useByteStorage();

		return true;
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
	 * duplicate instances are rewritten
	 */
	public void collapseStates(ArrayList<Integer> remap)
	{
		for (GenotypeData data: genotypes)
			data.collapseStates(remap);
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

	// Creates a dummy line using this line as a basis (for number of genotypes,
	// number of chromosomes, etc)
	Line createDummy()
	{
		Line dummy = new Line(" ");

		// For every existing chromosome the line knows about...
		for (GenotypeData data: genotypes)
			dummy.initializeMap(data.getChromosomeMap(), true);

		return dummy;
	}
}