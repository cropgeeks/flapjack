package flapjack.analysis;

import flapjack.data.*;
import java.util.*;

public class QTLTrackOptimiser
{
	DataSet dataSet;

	public QTLTrackOptimiser(DataSet dataSet)
	{
		this.dataSet = dataSet;
	}

	// Attempt to work out the optimum number of active tracks for a new set
	public void optimizeTrackUsage()
	{
		long s = System.currentTimeMillis();

		// Optimum number of tracks to use
		int optimum = 0;

		for (int i = 1; i <= 15; i++)
		{
			int trackCount = 0;

			// Scan each chromosome
			for (ChromosomeMap c: dataSet.getChromosomeMaps())
				if (setTracks(i, c))
					trackCount = i;

			// And update the best number if required
			if (trackCount > optimum)
				optimum = trackCount;
			// If a higher number wasn't found, then we've reached the optimum
			else
				break;
		}

		// Once we know the best number, reset to that number
		for (ChromosomeMap c: dataSet.getChromosomeMaps())
			setTracks(optimum, c);

		System.out.println("Tracks optimised in " + (System.currentTimeMillis()-s + "ms"));
	}

	public boolean setTracks(int size, ChromosomeMap c)
	{
		Vector<Vector<Feature>> trackSet = c.getTrackSet();

		// 1: Move all features onto a temp track
		Vector<Feature> tmpTrack = new Vector<Feature>();
		for (Vector<Feature> track: trackSet)
		{
			Enumeration<Feature> features = track.elements();
			while (features.hasMoreElements())
				tmpTrack.add(features.nextElement());
		}

		// 2: Sort the features back into order
		Collections.sort(tmpTrack);

		// 3: Set up the correct number of new tracks
		trackSet.removeAllElements();
		for (int i = 0; i < size; i++)
			trackSet.add(new Vector<Feature>());

		// 4: Distribute the features across the tracks
		for (Feature f: tmpTrack)
		{
			boolean added = false;
			for (Vector<Feature> track: trackSet)
			{
				added = addToTrack(track, f);
				if (added)
					break;
			}

			if (added == false)
				trackSet.get(0).add(f);
		}

		// Return true if any QTLs are on the final track
		int count = trackSet.size();
		if (count > 0)
			return trackSet.get(count-1).size() > 0;
		else
			return false;
	}

	// Checks to see if a feature can be added to the end of this track without
	// clashing with an existing element
	private boolean addToTrack(Vector<Feature> track, Feature f)
	{
		if (track.size() == 0)
		{
			track.add(f);
			return true;
		}

		Feature prev = track.get(track.size()-1);
		if (f.getMin() > prev.getMax())
		{
			track.add(f);
			return true;
		}

		return false;
	}
}
