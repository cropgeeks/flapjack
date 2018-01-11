// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import ch.systemsx.cisd.hdf5.*;

/**
 * Hdf5Utils contains utility methods to interact with an HDF5 file.
 */
public class Hdf5Utils
{
	/**
	 * Returns all the lines from the HDF5 file. The lines will <b>NOT</b> be in a well-defined order.
	 *
	 * @param hdf5File The .hdf5 file containing the data
	 * @return All the lines from the HDF5 file. The lines will <b>NOT</b> be in a well-defined order.
	 */
	public static List<String> getLines(File hdf5File)
	{
		IHDF5Reader reader = HDF5Factory.openForReading(hdf5File);

		String[] hdf5MarkersArray = reader.readStringArray("Lines");

		// Just return the markers as an ArrayList
		List<String> lines = new ArrayList<String>(Arrays.asList(hdf5MarkersArray));

		reader.close();

		return lines;
	}

	/**
	 * Returns all the markers from the HDF5 file. The lines will <b>NOT</b> be in a well-defined order.
	 *
	 * @param hdf5File The .hdf5 file containing the data
	 * @return All the markers from the HDF5 file. The lines will <b>NOT</b> be in a well-defined order.
	 */
	public static List<String> getMarkers(File hdf5File)
	{
		IHDF5Reader reader = HDF5Factory.openForReading(hdf5File);

		String[] hdf5MarkersArray = reader.readStringArray("Markers");

		List<String> markers = new ArrayList<String>(Arrays.asList(hdf5MarkersArray));

		reader.close();

		// Just return the markers as an ArrayList
		return markers;
	}

	/**
	 * Reads all the lines from the HDF5 file and calls {@link List#retainAll(Collection)} on the parameter {@link List} with the result. <p> The
	 * resulting list is the same as the parameter, only now it'll only contain those lines that are actually in the file.
	 *
	 * @param hdf5File The .hdf5 file containing the data
	 * @param lines    The list containing the lines to check
	 * @return The list passed in as the parameter, only now it'll only contain those lines that are actually in the file.
	 */
	public static List<String> retainLinesFrom(File hdf5File, List<String> lines)
	{
		List<String> hdf5Lines = getLines(hdf5File);

		// Remove all the lines that aren't in the file
		lines.retainAll(hdf5Lines);

		return lines;
	}

	/**
	 * Reads all the markers from the HDF5 file and calls {@link List#retainAll(Collection)} on the parameter {@link List} with the result. <p> The
	 * resulting list is the same as the parameter, only now it'll only contain those markers that are actually in the file.
	 *
	 * @param hdf5File The .hdf5 file containing the data
	 * @param markers  The list containing the markers to check
	 * @return The list passed in as the parameter, only now it'll only contain those markers that are actually in the file.
	 */
	public static List<String> retainMarkersFrom(File hdf5File, List<String> markers)
	{
		List<String> hdf5Markers = getMarkers(hdf5File);

		// Remove all the markers that aren't in the file
		markers.retainAll(hdf5Markers);

		return markers;
	}
}