// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;

import scri.commons.gui.*;

class ImportGenoAdvPanelNB extends JPanel implements ActionListener
{
	public ImportGenoAdvPanelNB()
	{
		initComponents();

		setBackground((Color)UIManager.get("fjDialogBG"));
		panel.setBackground((Color)UIManager.get("fjDialogBG"));

		checkUseHetSep.addActionListener(this);

		missingText.setText(Prefs.ioMissingData);
		heteroText.setText(Prefs.ioHeteroSeparator);
		checkHetero.setSelected(Prefs.ioHeteroCollapse);
		checkUseHetSep.setSelected(Prefs.ioUseHetSep);
		checkMarkers.setSelected(Prefs.ioMakeAllChromosome);
		checkTransposed.setSelected(Prefs.ioTransposed);

		// Apply localized text
		panel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.NBAdvancedDataImportPanel.panel")));
		RB.setText(checkMarkers, "gui.dialog.NBAdvancedDataImportPanel.checkMarkers");
		RB.setText(markersLabel, "gui.dialog.NBAdvancedDataImportPanel.markersLabel");
		RB.setText(checkUseHetSep, "gui.dialog.NBAdvancedDataImportPanel.checkUseHetSep");
		RB.setText(missingLabel, "gui.dialog.NBAdvancedDataImportPanel.missingLabel");
		RB.setText(heteroLabel, "gui.dialog.NBAdvancedDataImportPanel.heteroLabel");
		RB.setText(checkHetero, "gui.dialog.NBAdvancedDataImportPanel.checkHetero");
		RB.setText(checkTransposed, "gui.dialog.NBAdvancedDataImportPanel.checkTransposed");

		setLabelStates();
	}

	void applySettings()
	{
		Prefs.ioMissingData = missingText.getText();
		Prefs.ioHeteroSeparator = heteroText.getText();
		Prefs.ioHeteroCollapse = checkHetero.isSelected();
		Prefs.ioUseHetSep = checkUseHetSep.isSelected();
		Prefs.ioMakeAllChromosome = checkMarkers.isSelected();
		Prefs.ioTransposed = checkTransposed.isSelected();
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == checkUseHetSep)
			setLabelStates();
	}

	private void setLabelStates()
	{
		heteroLabel.setEnabled(checkUseHetSep.isSelected());
		heteroText.setEnabled(checkUseHetSep.isSelected());
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
        heteroLabel = new javax.swing.JLabel();
        heteroText = new javax.swing.JTextField();
        checkHetero = new javax.swing.JCheckBox();
        missingLabel = new javax.swing.JLabel();
        missingText = new javax.swing.JTextField();
        checkUseHetSep = new javax.swing.JCheckBox();
        checkMarkers = new javax.swing.JCheckBox();
        markersLabel = new javax.swing.JLabel();
        checkTransposed = new javax.swing.JCheckBox();

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Advanced options:"));

        heteroLabel.setText("Heterozygous separator string:");

        heteroText.setColumns(4);

        checkHetero.setText("Don't distinguish between heterozyous alleles (treats A/T the same as T/A)");

        missingLabel.setText("Missing data string:");

        missingText.setColumns(4);

        checkUseHetSep.setText("Expect heterozygotes to be separated by a string (A/T rather than AT)");

        checkMarkers.setText("Duplicate all markers onto a single \"All Chromosomes\" chromosome for side-by-side viewing");

        markersLabel.setText("(not recommended if you have a large number of markers)");

        checkTransposed.setText("Genotype data has been transposed (markers are now rows)");

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(markersLabel))
                    .addComponent(checkMarkers)
                    .addComponent(checkUseHetSep)
                    .addComponent(checkHetero)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(heteroLabel)
                            .addComponent(missingLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(missingText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(heteroText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(checkTransposed))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkMarkers)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(markersLabel)
                .addGap(18, 18, 18)
                .addComponent(checkHetero)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkUseHetSep)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(heteroLabel)
                    .addComponent(heteroText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(missingText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(missingLabel))
                .addGap(18, 18, 18)
                .addComponent(checkTransposed)
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
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkHetero;
    private javax.swing.JCheckBox checkMarkers;
    private javax.swing.JCheckBox checkTransposed;
    private javax.swing.JCheckBox checkUseHetSep;
    private javax.swing.JLabel heteroLabel;
    private javax.swing.JTextField heteroText;
    private javax.swing.JLabel markersLabel;
    private javax.swing.JLabel missingLabel;
    private javax.swing.JTextField missingText;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables

}