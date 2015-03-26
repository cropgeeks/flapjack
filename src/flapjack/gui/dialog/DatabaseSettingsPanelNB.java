// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

class DatabaseSettingsPanelNB extends JPanel
{
	private DBAssociation dbAssociation;

	public DatabaseSettingsPanelNB(DataSet dataSet)
	{
		initComponents();

		setBackground((Color)UIManager.get("fjDialogBG"));
		panel.setBackground((Color)UIManager.get("fjDialogBG"));

		this.dbAssociation = dataSet.getDbAssociation();

		panel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.NBDatabaseSettingsPanel.panel.title")));
		RB.setText(label, "gui.dialog.NBDatabaseSettingsPanel.label");
		RB.setText(lineLabel, "gui.dialog.NBDatabaseSettingsPanel.lineLabel");
		RB.setText(lineHint, "gui.dialog.NBDatabaseSettingsPanel.lineHint");
		RB.setText(markerLabel, "gui.dialog.NBDatabaseSettingsPanel.markerLabel");
		RB.setText(markerHint, "gui.dialog.NBDatabaseSettingsPanel.markerHint");
		RB.setText(hintLabel, "gui.dialog.NBDatabaseSettingsPanel.hintLabel");

		label.setIcon(Icons.getIcon("DATABASE_32", Prefs.uiScale));
		lineText.setText(dbAssociation.getLineSearch());
		lineText.setCaretPosition(0);
		markerText.setText(dbAssociation.getMarkerSearch());
		markerText.setCaretPosition(0);
	}

	void isOK()
	{
		dbAssociation.setLineSearch(lineText.getText());
		dbAssociation.setMarkerSearch(markerText.getText());

		Actions.dataDBLineName.setEnabled(dbAssociation.isLineSearchEnabled());
		Actions.dataDBMarkerName.setEnabled(dbAssociation.isMarkerSearchEnabled());
		Actions.projectModified();
	}

	/** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        label = new javax.swing.JLabel();
        panel = new javax.swing.JPanel();
        lineLabel = new javax.swing.JLabel();
        lineText = new javax.swing.JTextField();
        markerLabel = new javax.swing.JLabel();
        markerText = new javax.swing.JTextField();
        hintLabel = new javax.swing.JLabel();
        lineHint = new javax.swing.JLabel();
        markerHint = new javax.swing.JLabel();

        label.setText("Flapjack needs to know how to link up with a remote database for performing queries.");

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Options:"));

        lineLabel.setLabelFor(lineText);
        lineLabel.setText("Line searches:");

        markerLabel.setLabelFor(markerText);
        markerLabel.setText("Marker searches:");

        hintLabel.setText("$LINE and $MARKER will be replaced by the actual line or marker name during submission.");

        lineHint.setText("eg: http://mydatabase.com/search?line=$LINE");

        markerHint.setText("eg: http://mydatabase.com/search?marker=$MARKER");

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lineLabel)
                            .addComponent(markerLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(markerHint)
                            .addComponent(markerText, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                            .addComponent(lineHint)
                            .addComponent(lineText, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)))
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(hintLabel)))
                .addContainerGap())
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lineLabel)
                    .addComponent(lineText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lineHint)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(markerLabel)
                    .addComponent(markerText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(markerHint)
                .addGap(18, 18, 18)
                .addComponent(hintLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel hintLabel;
    private javax.swing.JLabel label;
    private javax.swing.JLabel lineHint;
    private javax.swing.JLabel lineLabel;
    private javax.swing.JTextField lineText;
    private javax.swing.JLabel markerHint;
    private javax.swing.JLabel markerLabel;
    private javax.swing.JTextField markerText;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables
}