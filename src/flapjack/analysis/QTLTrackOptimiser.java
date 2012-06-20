// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.analysis;

import flapjack.data.*;
import java.util.*;

public class QTLTrackOptimiser
{
	public QTLTrackOptimiser()
	{
	}

	// Attempt to work out the optimum number of active tracks for a new set
/*	public void optimizeTrackUsage()
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
*/

	public ArrayList<ArrayList<FeatureGroup>> getTracks(int size, GTView view)
	{
		ArrayList<ArrayList<FeatureGroup>> tracks = new	ArrayList<>();

		ArrayList<QTLInfo> QTLInfos = view.getQTLs();

		// Set up the correct number of new tracks
		for (int i = 0; i < size; i++)
			tracks.add(new ArrayList<FeatureGroup>());

		// Distribute the features across the tracks
		for (QTLInfo qtlInfo: QTLInfos)
		{
			QTL qtl = qtlInfo.getQTL();
			// Just ignore features that are invisible/disabled
			if (qtl.isVisible() == false || qtl.isAllowed() == false)
				continue;

			for (int trackNum = size-1; trackNum >= 0; trackNum--)
			{
				ArrayList<FeatureGroup> track = tracks.get(trackNum);

				if (addToTrack(track, qtlInfo, trackNum == 0))
					break;
			}
		}

		return tracks;
	}

	// Checks to see if a feature can be added to the end of this track without
	// clashing with an existing element
	// @param group true if the feature should be grouped with any that clash
	private boolean addToTrack(ArrayList<FeatureGroup> track, QTLInfo qtlInfo, boolean group)
	{
		if (track.size() == 0)
		{
			track.add(new FeatureGroup(qtlInfo));
			return true;
		}

		FeatureGroup prev = track.get(track.size()-1);

		if (qtlInfo.min() > prev.getMax())
		{
			track.add(new FeatureGroup(qtlInfo));
			return true;
		}
		else if (group)
		{
			prev.addFeature(qtlInfo);
			return true;
		}
		else
			return false;
	}
}