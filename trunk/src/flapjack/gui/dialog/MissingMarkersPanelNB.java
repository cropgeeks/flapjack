// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.gui.*;

import scri.commons.gui.*;

class MissingMarkersPanelNB extends JPanel implements ChangeListener
{
	private int value = Prefs.guiMissingMarkerPcnt;

	MissingMarkersPanelNB()
	{
		initComponents();

		setBackground((Color)UIManager.get("fjDialogBG"));
		panel.setBackground((Color)UIManager.get("fjDialogBG"));

		RB.setText(comboLabel, "gui.dialog.MissingMarkersPanelNB.comboLabel");
		combo.addItem(RB.getString("gui.dialog.MissingMarkersPanelNB.comboCurrent"));
		combo.addItem(RB.getString("gui.dialog.MissingMarkersPanelNB.comboAll"));
		if (Prefs.guiMissingMarkerAllChromsomes)
			combo.setSelectedIndex(1);

		slider.addChangeListener(this);
		slider.setValue(value);
	}

	private void formatLabel()
	{
		percentLabel.setText(RB.format(
			"gui.dialog.MissingMarkersPanelNB.percentLabel", value));
	}

	public void stateChanged(ChangeEvent e)
	{
		value = slider.getValue();
		formatLabel();
	}

	void applySettings()
	{
		Prefs.guiMissingMarkerPcnt = value;
		Prefs.guiMissingMarkerAllChromsomes = combo.getSelectedIndex() == 1;
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        percentLabel = new javax.swing.JLabel();
        comboLabel = new javax.swing.JLabel();
        combo = new javax.swing.JComboBox();
        slider = new javax.swing.JSlider();

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Settings:"));

        percentLabel.setLabelFor(slider);
        percentLabel.setText("Only hide markers with greater than 95% missing data:");

        comboLabel.setLabelFor(combo);
        comboLabel.setText("Hide markers in:");

        slider.setMajorTickSpacing(20);
        slider.setMinorTickSpacing(10);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        slider.setValue(95);
        slider.setOpaque(false);

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addComponent(comboLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(combo, 0, 237, Short.MAX_VALUE))
                    .addComponent(percentLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                    .addComponent(slider, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboLabel)
                    .addComponent(combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(percentLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(slider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox combo;
    private javax.swing.JLabel comboLabel;
    private javax.swing.JPanel panel;
    private javax.swing.JLabel percentLabel;
    private javax.swing.JSlider slider;
    // End of variables declaration//GEN-END:variables
}