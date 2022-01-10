// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.importer;

import java.awt.*;
import javax.swing.*;

import jhi.flapjack.gui.*;

import scri.commons.gui.*;

class ImportGenoAdvPanelNB extends JPanel
{
	public ImportGenoAdvPanelNB()
	{
		initComponents();

		setBackground((Color)UIManager.get("fjDialogBG"));
		panel.setBackground((Color)UIManager.get("fjDialogBG"));
		fjPanel.setBackground((Color)UIManager.get("fjDialogBG"));

		missingText.setText(Prefs.ioMissingData);
		heteroText.setText(Prefs.ioHeteroSeparator);
		checkHetero.setSelected(Prefs.ioHeteroCollapse);
		checkMarkers.setSelected(Prefs.ioMakeAllChromosome);
		checkTransposed.setSelected(Prefs.ioTransposed);
		checkAllowDupLines.setSelected(Prefs.ioAllowDupLines);

		// Apply localized text
		panel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.NBAdvancedDataImportPanel.panel")));
		fjPanel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.NBAdvancedDataImportPanel.fjPanel")));
		RB.setText(checkMarkers, "gui.dialog.NBAdvancedDataImportPanel.checkMarkers");
		RB.setText(markersLabel, "gui.dialog.NBAdvancedDataImportPanel.markersLabel");
		RB.setText(missingLabel, "gui.dialog.NBAdvancedDataImportPanel.missingLabel");
		RB.setText(heteroLabel, "gui.dialog.NBAdvancedDataImportPanel.heteroLabel");
		RB.setText(checkHetero, "gui.dialog.NBAdvancedDataImportPanel.checkHetero");
		RB.setText(checkTransposed, "gui.dialog.NBAdvancedDataImportPanel.checkTransposed");
		RB.setText(checkAllowDupLines, "gui.dialog.NBAdvancedDataImportPanel.checkDupLines");
	}

	void applySettings()
	{
		Prefs.ioMissingData = missingText.getText();
		Prefs.ioHeteroSeparator = heteroText.getText();
		Prefs.ioHeteroCollapse = checkHetero.isSelected();
		Prefs.ioMakeAllChromosome = checkMarkers.isSelected();
		Prefs.ioTransposed = checkTransposed.isSelected();
		Prefs.ioAllowDupLines = checkAllowDupLines.isSelected();
	}


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        panel = new javax.swing.JPanel();
        checkHetero = new javax.swing.JCheckBox();
        checkMarkers = new javax.swing.JCheckBox();
        markersLabel = new javax.swing.JLabel();
        checkAllowDupLines = new javax.swing.JCheckBox();
        fjPanel = new javax.swing.JPanel();
        heteroText = new javax.swing.JTextField();
        checkTransposed = new javax.swing.JCheckBox();
        heteroLabel = new javax.swing.JLabel();
        missingText = new javax.swing.JTextField();
        missingLabel = new javax.swing.JLabel();

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Advanced options:"));

        checkHetero.setText("Don't distinguish between heterozyous alleles (treats A/T the same as T/A)");

        checkMarkers.setText("Duplicate all markers onto a single \"All Chromosomes\" chromosome for side-by-side viewing");

        markersLabel.setText("(not recommended if you have a large number of markers)");

        checkAllowDupLines.setText("Allow data with duplicate line names to be imported (experimental)");

        fjPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Flapjack format specific:"));

        heteroText.setColumns(4);

        checkTransposed.setText("Genotype data has been transposed (markers are now rows)");

        heteroLabel.setText("Heterozygous separator string:");

        missingText.setColumns(4);

        missingLabel.setText("Missing data string:");

        javax.swing.GroupLayout fjPanelLayout = new javax.swing.GroupLayout(fjPanel);
        fjPanel.setLayout(fjPanelLayout);
        fjPanelLayout.setHorizontalGroup(
            fjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fjPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkTransposed)
                    .addGroup(fjPanelLayout.createSequentialGroup()
                        .addGroup(fjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(heteroLabel)
                            .addComponent(missingLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(fjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(missingText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(heteroText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        fjPanelLayout.setVerticalGroup(
            fjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fjPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(heteroLabel)
                    .addComponent(heteroText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(missingText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(missingLabel))
                .addGap(18, 18, 18)
                .addComponent(checkTransposed)
                .addContainerGap())
        );

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fjPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(checkMarkers)
                            .addComponent(checkHetero)
                            .addGroup(panelLayout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(markersLabel))
                            .addComponent(checkAllowDupLines))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
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
                .addComponent(checkAllowDupLines)
                .addGap(18, 18, 18)
                .addComponent(fjPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
    private javax.swing.JCheckBox checkAllowDupLines;
    private javax.swing.JCheckBox checkHetero;
    private javax.swing.JCheckBox checkMarkers;
    private javax.swing.JCheckBox checkTransposed;
    private javax.swing.JPanel fjPanel;
    private javax.swing.JLabel heteroLabel;
    private javax.swing.JTextField heteroText;
    private javax.swing.JLabel markersLabel;
    private javax.swing.JLabel missingLabel;
    private javax.swing.JTextField missingText;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables

}