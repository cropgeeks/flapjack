package flapjack.data;

import java.util.*;

import scri.commons.MatrixXML;

public class GenotypeData extends XMLRoot
{
	// A reference to the chromsome this data applies to
	private String mapName;

	private byte[] loci;

	public GenotypeData()
	{
	}

	public GenotypeData(ChromosomeMap map)
	{
		mapName = map.getName();

		// How many markers do we need to hold data for?
		int size = map.countLoci();

		// Initialize the array to the correct size
		loci = new byte[size];
	}


	// Methods required for XML serialization

	public String getMapName()
		{ return mapName; }

	public void setMapName(String mapName)
		{ this.mapName = mapName; }

/*	public String getLoci()
		{ return MatrixXML.arrayToString(loci); }

	public void setLoci(String lociArray)
		{ this.loci = MatrixXML.stringToByteArray(lociArray); }
*/
	public byte[] getLoci()
		{ return loci; }

	public void setLoci(byte[] loci)
		{ this.loci = loci; }


	// Other methods

	void setLoci(int index, int stateCode)
	{
		loci[index] = (byte) stateCode;
	}

	public int getState(int index)
	{
		return loci[index];
	}

	boolean isGenotypeDataForMap(ChromosomeMap map)
	{
		return mapName.equals(map.getName());
	}

	public int countLoci()
		{ return loci.length; }
}