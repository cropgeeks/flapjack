package flapjack.gui.traits;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

public class QTLPanel extends JPanel implements ActionListener, ChangeListener
{
	private DataSet dataSet;

	private NBQTLControlPanel controls;

	public QTLPanel(DataSet dataSet)
	{
		this.dataSet = dataSet;

		controls = new NBQTLControlPanel();

		// Set the spinner to the correct number of tracks for this dataset
		int size = dataSet.getChromosomeMaps().get(0).getTrackSet().size();
		if (size > 0)
			controls.trackSpinner.setValue(size);
		else
			controls.trackSpinner.setEnabled(false);

		controls.bImport.addActionListener(this);
		controls.bRemove.addActionListener(this);
		controls.trackSpinner.addChangeListener(this);

		setLayout(new BorderLayout(0, 0));
		setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0));
		add(new JLabel("- qtl data here -", JLabel.CENTER));
		add(controls, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == controls.bImport)
		{
		}

		else if (e.getSource() == controls.bRemove)
		{
			String msg = RB.getString("gui.traits.QTLPanel.removeMsg");
			String[] options = new String[] {
					RB.getString("gui.traits.TraitsPanel.remove"),
					RB.getString("gui.text.cancel") };

			int response = TaskDialog.show(msg, MsgBox.QST, 1, options);

			if (response == 0)
				removeAllTraits();
		}
	}

	private void removeAllTraits()
	{
		Actions.projectModified();
	}

	public void stateChanged(ChangeEvent e)
	{
		int size = (Integer) controls.trackSpinner.getValue();

		// Redistribute the features across the *new* number of tracks - for
		// every chromosome in the data
		for (ChromosomeMap c: dataSet.getChromosomeMaps())
			setTracks(size, c);
	}

	private void setTracks(int size, ChromosomeMap c)
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