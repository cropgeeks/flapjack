package jhi.flapjack.io;

import java.io.*;
import java.util.*;

import jhi.flapjack.data.*;

import ch.systemsx.cisd.hdf5.*;
import jhi.flapjack.gui.Prefs;
import java.util.Map.Entry;

import scri.commons.gui.RB;

public class Hdf5GenotypeDataImporter implements IGenotypeImporter
{
	private static final String LINES = "Genotypes";
	private static final String STATE_TABLE = "StateTable";
	private static final String LINE_ROOT = "Genotypes/";
	private static final String LINE_CALLS = "/calls";

	private final File file;
	private final DataSet dataSet;
	private final StateTable stateTable;

	private boolean isOk = true;

	// Also track line names, for duplicate detection
	private HashMap<String, Line> foundLines;

	// Each marker's name is stored (only while loading) in a hashmap, along
	// with the index of the chromosome it is associated with
	private final HashMap<String, MarkerIndex> markers;
	// A list of the index of the chromosome for each marker
	private final ArrayList<Integer> chromosomeIndices;

	private long markerCount;

	private long lineCount;

	Hdf5GenotypeDataImporter(File file, DataSet dataSet, HashMap<String, MarkerIndex> markers, ArrayList<Integer> markerChromosomes)
	{
		this.file = file;
		this.dataSet = dataSet;
		this.markers = markers;

		this.chromosomeIndices = markerChromosomes;

		stateTable = dataSet.getStateTable();
	}

	@Override
	public void importGenotypeData()
		throws IOException, DataFormatException
	{
		long s = System.currentTimeMillis();
		foundLines = new HashMap<String, Line>();

		System.out.println("Marker Chromosomes.size()" + chromosomeIndices.size());

		IHDF5ReaderConfigurator config = HDF5Factory.configureForReading(file);
		IHDF5Reader reader = config.reader();

		// Get a list of line names from the HDF5 file
		List<String> lines  = reader.getGroupMembers(LINES);

		// Load state table from the HDF5 file
		String[] states = reader.readStringArray(STATE_TABLE);
		for (String state : states)
			stateTable.getStateCode(state, true, Prefs.ioMissingData, Prefs.ioHeteroCollapse, Prefs.ioHeteroSeparator);

		// Determine if we can use byte storage rather than int storage for the
		// state table within Flapjack
		boolean useByteStorage = stateTable.size() < 127;

		for (String name : lines)
		{
			// Throw an error if we find duplicat line names
			if (foundLines.get(name) != null)
				throw new DataFormatException(RB.format("io.DataFormatException.duplicateLineError", name, lineCount+1));

			Line line = dataSet.createLine(name, useByteStorage);
			foundLines.put(name, line);

			// Load the values for this line from the HDF5 file
			int[] alleleStates = reader.readIntArray(LINE_ROOT + name + LINE_CALLS);

			HashMap<Integer, ArrayList<Integer>> allelesByChromosome = sortAllelesIntoChromosomes(alleleStates, chromosomeIndices);

			for (Entry<Integer, ArrayList<Integer>> entry : allelesByChromosome.entrySet())
			{
				if (!isOk)
					break;

				int i=0;
				for (int allele : entry.getValue())
				{
					line.setLoci(entry.getKey(), i++, allele);
					markerCount++;
				}
			}
			lineCount++;
		}

		System.out.println("Time taken to read genotypes: " + (System.currentTimeMillis() - s) + " ms");
	}

	private HashMap<Integer, ArrayList<Integer>> sortAllelesIntoChromosomes(int[] alleleStates, ArrayList<Integer> chromIndices)
	{
		// Stores the index of the last chromosome seen in the dataset
		int chromosomeIndex = 0;

		HashMap<Integer, ArrayList<Integer>> allelesByChromosome = new HashMap<>();
		ArrayList<Integer> alleles = new ArrayList<>();

		for (int i=0; i < chromIndices.size(); i++)
		{
			// When the chromosomeIndex doesn't equal the previous we have moved
			// on to the alleles from the next chromosome
			if (chromIndices.get(i) != chromosomeIndex)
			{
				allelesByChromosome.put(chromosomeIndex, alleles);
				alleles = new ArrayList<>();
				chromosomeIndex = chromIndices.get(i);
			}

			alleles.add(alleleStates[i]);
		}
		// Add the alleles for the final chromosome
		allelesByChromosome.put(chromosomeIndex, alleles);

		return allelesByChromosome;
	}

	@Override
	public void cleanUp()
	{
		markers.clear();
		foundLines.clear();
	}

	@Override
	public void cancelImport()
		{ isOk = false; }

	@Override
	public long getMarkerCount()
		{ return markerCount; }

	@Override
	public long getLineCount()
		{ return lineCount; }

	@Override
	public long getBytesRead()
		{ return 0; }
}