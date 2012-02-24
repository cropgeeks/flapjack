// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;
import flapjack.gui.visualization.*;
import flapjack.gui.visualization.undo.*;

import scri.commons.gui.*;

public class HideLMDialog extends JDialog implements ActionListener
{
	private JButton bOK, bCancel, bHelp;
	private boolean isOK = false;

	private String i18n = "lines";

	private HideLMPanelNB nbPanel;
	private GenotypePanel gPanel;

	public HideLMDialog(GenotypePanel gPanel, boolean markers)
	{
		super(Flapjack.winMain, "", true);
		this.gPanel = gPanel;

		// Toggles the state of the i18n properties we look up
		if (markers)
			i18n = "markers";

		setTitle(RB.getString("gui.dialog.HideLMDialog." + i18n + ".title"));

		nbPanel = new HideLMPanelNB(this, gPanel.getView(), markers);

		add(new TitlePanel2(), BorderLayout.NORTH);
		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bOK);
		SwingUtils.addCloseHandler(this, bCancel);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bOK = SwingUtils.getButton(RB.getString("gui.text.ok"));
		bOK.addActionListener(this);
		bCancel = SwingUtils.getButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);
		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		if (i18n.equals("lines"))
			FlapjackUtils.setHelp(bHelp, "gui.dialog.HideLinesDialog");
		else
			FlapjackUtils.setHelp(bHelp, "gui.dialog.HideMarkersDialog");

		JPanel p1 = FlapjackUtils.getButtonPanel();
		p1.add(bOK);
		p1.add(bCancel);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == nbPanel.bRestore && !restore())
			return;

		else if (e.getSource() == bOK)
		{
			nbPanel.isOK();
			isOK = true;
		}

		setVisible(false);
	}

	public boolean isOK()
		{ return isOK; }

	private boolean restore()
	{
		if (i18n.equals("lines"))
			return restoreHiddenLines();
		else
			return restoreHiddenMarkers();
	}

	private boolean restoreHiddenLines()
	{
		String msg = RB.getString("gui.dialog.HideLMDialog.lines.restoreMsg");

		String[] options = new String[] {
				RB.getString("gui.dialog.HideLMDialog.restore"),
				RB.getString("gui.text.cancel") };

		if (TaskDialog.show(msg, TaskDialog.QST, 1, options) != 0)
			return false;

		// Create an undo state for the restore operation
		HidLinesState state = new HidLinesState(gPanel.getViewSet(),
			RB.getString("gui.visualization.HidLinesState.restoredLines"));
		state.createUndoState();

		// Do the restore
		gPanel.getView().restoreHiddenLines();
		gPanel.refreshView();

		// Create a redo state
		state.createRedoState();
		gPanel.addUndoState(state);

		return true;
	}

	private boolean restoreHiddenMarkers()
	{
		String msg = RB.getString("gui.dialog.HideLMDialog.markers.restoreMsg");

		String[] options = new String[] {
				RB.getString("gui.dialog.HideLMDialog.restore"),
				RB.getString("gui.text.cancel") };

		if (TaskDialog.show(msg, TaskDialog.QST, 1, options) != 0)
			return false;

		// Create an undo state for the restore operation
		HidMarkersState state = new HidMarkersState(gPanel.getView(),
			RB.getString("gui.visualization.HidMarkersState.restoredMarkers"));
		state.createUndoState();

		// Do the restore
		gPanel.getView().restoreHiddenMarkers();
		gPanel.refreshView();

		// Create a redo state
		state.createRedoState();
		gPanel.addUndoState(state);

		return true;
	}
}