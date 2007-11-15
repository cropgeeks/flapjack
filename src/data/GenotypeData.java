package flapjack.data;

import java.util.*;

public class GenotypeData
{
	private String name;

	private Vector<short[]> loci = new Vector<short[]>();

	public GenotypeData()
	{
	}

	public void addLoci(short state)
	{
		loci.add(new short[] { state });
	}

	public void addLoci(short[] states)
	{
		loci.add(states);
	}
}