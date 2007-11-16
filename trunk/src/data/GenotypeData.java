package flapjack.data;

import java.util.*;

public class GenotypeData
{
	// A reference to the chromsome this data applies to
	private ChromosomeMap map;

	private Vector<short[]> loci;

	public GenotypeData(ChromosomeMap map)
	{
		this.map = map;

		// How many markers do we need to hold data for?
		int size = map.countLoci();

		// Initialize the vector to the correct size...
		loci = new Vector<short[]>(size);
		// ...and fill it with blank data
		for (int i = 0; i < size; i++)
			loci.add(new short[] { 0 });
	}

	void setLoci(int index, short[] states)
	{
		loci.setElementAt(states, index);
	}

	boolean isGenotypeDataForMap(ChromosomeMap map)
	{
		return this.map == map;
	}

	void print()
	{
		for (short[] data: loci)
		{
			for (int i = 0; i < data.length; i++)
				if (i == 0)
					System.out.print(data[i]);
				else
					System.out.print("," + data[i]);

			System.out.print("\t");
		}

		System.out.println();
	}
}