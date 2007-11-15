package flapjack.data;

import java.util.*;

public class Line
{
	private String name;

	private Vector<GenotypeData> genotypes = new Vector<GenotypeData>();

	public Line()
	{
	}

	public void addChromosome(GenotypeData genotypeData)
	{
		genotypes.add(genotypeData);
	}
}