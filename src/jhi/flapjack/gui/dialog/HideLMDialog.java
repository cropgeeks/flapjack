// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog;

import java.awt.event.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.visualization.*;
import jhi.flapjack.gui.visualization.undo.*;

import scri.commons.gui.*;

public class HideLMDialog extends JDialog implements ActionListener
{
private boolean isOK = false;

	private String i18n = "lines";

	private GenotypePanel gPanel;
	private GTView view;

	public HideLMDialog(GenotypePanel gPanel, boolean markers)
	{
		super(Flapjack.winMain, "", true);
		this.gPanel = gPanel;
		view = gPanel.getView();

		// Toggles the state of the i18n properties we look up
		if (markers)
			i18n = "markers";

		setTitle(RB.getString("gui.dialog.HideLMDialog." + i18n + ".title"));

		initComponents();
		initComponents2();

		FlapjackUtils.initDialog(this, bOK, bCancel, true, getContentPane(), hidePanel, showPanel);
	}

	private void initComponents2()
	{
		RB.setText(bOK, "gui.text.ok");
		bOK.addActionListener(this);
		RB.setText(bCancel, "gui.text.cancel");
		bCancel.addActionListener(this);
		RB.setText(bHelp, "gui.text.help");
		if (i18n.equals("lines"))
			FlapjackUtils.setHelp(bHelp, "show_hide_lines.html");
		else
			FlapjackUtils.setHelp(bHelp, "show_hide_markers.html");

		int total, selected, unselected;

		if (i18n.equals("markers"))
		{
			total = view.countGenuineMarkers();
			selected = view.countSelectedMarkers();
		}
		else
		{
			total = view.lineCount();
			selected = view.countSelectedLines();
		}

		unselected = total - selected;

		rHideSelected.setText(RB.format("gui.dialog.NBHideLMPanel." + i18n + ".rHideSelected", selected, total));
		rHideUnselected.setText(RB.format("gui.dialog.NBHideLMPanel." + i18n + ".rHideUnselected", unselected, total));
		RB.setMnemonic(rHideSelected, "gui.dialog.NBHideLMPanel." + i18n + ".rHideSelected");
		RB.setMnemonic(rHideUnselected, "gui.dialog.NBHideLMPanel." + i18n + ".rHideUnselected");

		ButtonGroup bGroup = new ButtonGroup();
		bGroup.add(rHideSelected);
		bGroup.add(rHideUnselected);

		hidePanel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.NBHideLMPanel." + i18n + ".hidePanel.title")));
		RB.setText(hideLabel, "gui.dialog.NBHideLMPanel." + i18n + ".hideLabel");
		showPanel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.NBHideLMPanel." + i18n + ".showPanel.title")));
		RB.setText(showLabel, "gui.dialog.NBHideLMPanel." + i18n + ".showLabel");
		RB.setText(bRestore, "gui.dialog.NBHideLMPanel." + i18n + ".bRestore");

		if (SystemUtils.isMacOS())
			hideHint.setText(
				RB.format("gui.dialog.NBHideLMPanel." + i18n + ".hideHintOSX",
				RB.getString("gui.StatusBar.cmnd")));
		else
			RB.setText(hideHint, "gui.dialog.NBHideLMPanel." + i18n + ".hideHint");

		if (i18n.equals("markers"))
		{
			rHideSelected.setSelected(Prefs.guiHideSelectedMarkers);
			rHideUnselected.setSelected(!Prefs.guiHideSelectedMarkers);
			countLabel.setText(RB.format("gui.dialog.NBHideLMPanel.countLabel",
				view.hiddenMarkerCount()));

			if (view.hiddenMarkerCount() == 0)
				bRestore.setEnabled(false);
		}
		else
		{
			rHideSelected.setSelected(Prefs.guiHideSelectedLines);
			rHideUnselected.setSelected(!Prefs.guiHideSelectedLines);
			countLabel.setText(RB.format("gui.dialog.NBHideLMPanel.countLabel",
				view.getViewSet().hiddenLineCount()));

			if (view.getViewSet().hiddenLineCount() == 0)
				bRestore.setEnabled(false);
		}

		bRestore.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bRestore && !restore())
			return;

		else if (e.getSource() == bOK)
		{
			if (i18n.equals("markers"))
				Prefs.guiHideSelectedMarkers = rHideSelected.isSelected();
			else
				Prefs.guiHideSelectedLines = rHideSelected.isSelected();

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
		gPanel.getViewSet().restoreHiddenLines();
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

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        hidePanel = new javax.swing.JPanel();
        rHideUnselected = new javax.swing.JRadioButton();
        rHideSelected = new javax.swing.JRadioButton();
        hideLabel = new javax.swing.JLabel();
        hideHint = new javax.swing.JLabel();
        showPanel = new javax.swing.JPanel();
        showLabel = new javax.swing.JLabel();
        bRestore = new javax.swing.JButton();
        countLabel = new javax.swing.JLabel();
        dialogPanel1 = new scri.commons.gui.matisse.DialogPanel();
        bOK = new javax.swing.JButton();
        bCancel = new javax.swing.JButton();
        bHelp = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        hidePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Hide markers:"));

        rHideUnselected.setText("Hide all the markers that are NOT currently selected");

        rHideSelected.setText("Hide all the markers that ARE currently selected");

        hideLabel.setText("hideLabel");

        hideHint.setText("hideHint");

        javax.swing.GroupLayout hidePanelLayout = new javax.swing.GroupLayout(hidePanel);
        hidePanel.setLayout(hidePanelLayout);
        hidePanelLayout.setHorizontalGroup(
            hidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hidePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(hidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rHideSelected)
                    .addComponent(rHideUnselected)
                    .addComponent(hideLabel)
                    .addComponent(hideHint))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        hidePanelLayout.setVerticalGroup(
            hidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hidePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(hideLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rHideUnselected)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rHideSelected)
                .addGap(18, 18, 18)
                .addComponent(hideHint)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        showPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Show markers:"));

        showLabel.setText("Click to restore all currently hidden markers to the view:");

        bRestore.setText("Show hidden markers");

        countLabel.setText("(0 currently hidden)");

        javax.swing.GroupLayout showPanelLayout = new javax.swing.GroupLayout(showPanel);
        showPanel.setLayout(showPanelLayout);
        showPanelLayout.setHorizontalGroup(
            showPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(showPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(showPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(showLabel)
                    .addGroup(showPanelLayout.createSequentialGroup()
                        .addComponent(bRestore)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(countLabel)))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        showPanelLayout.setVerticalGroup(
            showPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(showPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(showLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(showPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bRestore)
                    .addComponent(countLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bOK.setText("OK");
        dialogPanel1.add(bOK);

        bCancel.setText("Cancel");
        dialogPanel1.add(bCancel);

        bHelp.setText("Help");
        dialogPanel1.add(bHelp);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(showPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(hidePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(dialogPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(hidePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(dialogPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bCancel;
    private javax.swing.JButton bHelp;
    private javax.swing.JButton bOK;
    javax.swing.JButton bRestore;
    private javax.swing.JLabel countLabel;
    private scri.commons.gui.matisse.DialogPanel dialogPanel1;
    private javax.swing.JLabel hideHint;
    private javax.swing.JLabel hideLabel;
    private javax.swing.JPanel hidePanel;
    private javax.swing.JRadioButton rHideSelected;
    private javax.swing.JRadioButton rHideUnselected;
    private javax.swing.JLabel showLabel;
    private javax.swing.JPanel showPanel;
    // End of variables declaration//GEN-END:variables
}