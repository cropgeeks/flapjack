package flapjack.io;

import java.io.*;
import java.util.*;

import flapjack.data.*;

import ch.systemsx.cisd.hdf5.*;

public class Hdf5ChromosomeMapImporter implements IMapImporter
{
	// Constants used to load data from HDF5 files
	private static final String POSITIONS_ROOT = "Positions/";
	private static final String POSITIONS = "Positions";
	private static final String SNP_IDS = "SnpIds";
	private static final String CHROMOSOMES = "Chromosomes";
	private static final String CHROMOSOME_INDICES = "ChromosomeIndices";
	private static final String SNP_POSITIONS = POSITIONS_ROOT + POSITIONS;
	private static final String POSITIONS_SNP_IDS = POSITIONS_ROOT + SNP_IDS;
	private static final String POSITIONS_CHROMOSOMES = POSITIONS_ROOT + CHROMOSOMES;
	private static final String POSITIONS_CHROMOSOME_INDICES = POSITIONS_ROOT + CHROMOSOME_INDICES;

	private final File file;
	private final DataSet dataSet;

	// Each marker's name is stored (only while loading) in a hashmap, along
	// with the index of the chromosome it is associated with
	private final HashMap<String, MarkerIndex> markers = new HashMap<>();

	private final LinkedList<String> duplicates = new LinkedList<>();
	private final ArrayList<Integer> markerChroms = new ArrayList<>();

	private boolean isOK = true;
	private long markerCount = 0;

	public Hdf5ChromosomeMapImporter(File file, DataSet dataSet)
	{
		this.file = file;
		this.dataSet = dataSet;
	}

	@Override
	public void importMap()
		throws IOException, DataFormatException
	{
		long s = System.currentTimeMillis();
		IHDF5ReaderConfigurator config = HDF5Factory.configureForReading(file);
		IHDF5Reader reader = config.reader();

		// Read the Map data from the HDF5 file
		double[] positions = reader.readDoubleArray(SNP_POSITIONS);
		String[] names = reader.readStringArray(POSITIONS_SNP_IDS);
		String[] chromosomes = reader.readStringArray(POSITIONS_CHROMOSOMES);
		int[] chromosomeIndices = reader.readIntArray(POSITIONS_CHROMOSOME_INDICES);

		for (int i=0; i < names.length && isOK; i++)
		{
			Marker marker = new Marker(names[i], (float)positions[i]);

			MarkerIndex index = markers.get(marker.getName());
			if (index == null)
			{
				// Retrieve the map it should be added to
				ChromosomeMap.Wrapper w = dataSet.getMapByName(chromosomes[chromosomeIndices[i]], true);
				// And add it
				w.map.addMarker(marker);

				// And store it in the hashmap too
				markers.put(marker.getName(), new MarkerIndex(w.index, 0));

				// Store the non-duplicate markerChromosome mappings
				markerChroms.add(chromosomeIndices[i]);

				markerCount++;
			}
		}

		if (isOK)
			dataSet.orderMarkersWithinMaps();

		Collections.sort(dataSet.getChromosomeMaps());

		System.out.println("markers.size() = " + markers.size());

		// Once the data is loaded, we need to update the hashmap with the
		// index (within each map) of each marker, so that the genotype importer
		// can use it during its loading
		for (ChromosomeMap map: dataSet.getChromosomeMaps())
		{
			int i = 0;
			for (Marker marker: map)
				markers.get(marker.getName()).mkrIndex = i++;
		}

		System.out.println("assigned marker indexes");
		System.out.println("Time taken to read map: " + (System.currentTimeMillis() - s) + " ms");
	}

	@Override
	public long getMarkerCount()
		{ return markerCount; }

	@Override
	public HashMap<String, MarkerIndex> getMarkersHashMap()
		{ return markers; }

	public ArrayList<Integer> markerChromosomes()
		{ return markerChroms; }

	@Override
	public void cancelImport()
		{ isOK = false; }

	@Override
	public LinkedList<String> getDuplicates()
		{ return duplicates; }

	@Override
	public long getBytesRead()
		{ return 0; }
}
