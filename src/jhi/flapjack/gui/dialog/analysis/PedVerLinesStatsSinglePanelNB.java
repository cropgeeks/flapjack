// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import javax.swing.*;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class PedVerLinesStatsSinglePanelNB extends JPanel
{
	private ChromosomeSelectionDialog csd;
	private PedVerLinesThresholdDialog thresholdDialog;

	private DefaultComboBoxModel<LineInfo> referenceModel;
	private DefaultComboBoxModel<LineInfo> testModel;

	public PedVerLinesStatsSinglePanelNB(GTViewSet viewSet)
	{
		initComponents();
		initComponents2();

		AnalysisSet as = new AnalysisSet(viewSet)
			.withViews(null)
			.withSelectedLines()
			.withSelectedMarkers();

		csd = new ChromosomeSelectionDialog(viewSet, true, true);
		csdLabel.addActionListener(e -> csd.setVisible(true));

		thresholdDialog = new PedVerLinesThresholdDialog();
		lblThreshold.addActionListener(e -> thresholdDialog.setVisible(true));

		setupComboBoxes(as);

		FlapjackUtils.initPanel(jPanel1, parentsPanel);
	}

	private void setupComboBoxes(AnalysisSet as)
	{
		referenceModel = createComboModelFrom(as);
		referenceCombo.setModel(referenceModel);
		if (as.lineCount() >= 1)
			referenceCombo.setSelectedIndex(0);

		testModel = createComboModelFrom(as);
		testCombo.setModel(testModel);
		if (as.lineCount() >= 2)
			testCombo.setSelectedIndex(1);
	}

	private DefaultComboBoxModel<LineInfo> createComboModelFrom(AnalysisSet as)
	{
		DefaultComboBoxModel<LineInfo> model = new DefaultComboBoxModel<>();
		for (int i = 0; i < as.lineCount(); i++)
			model.addElement(as.getLine(i));

		return model;
	}

	private void initComponents2()
	{
		parentsPanel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.analysis.PedVerLinesStatsDialog.parentsPanel.title")));
		RB.setText(lblParent1, "gui.dialog.analysis.PedVerLinesStatsDialog.parentsPanel.lblParent1");
		RB.setText(lblParent2, "gui.dialog.analysis.PedVerLinesStatsDialog.parentsPanel.lblParent2");
		jPanel1.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.analysis.PedVerLinesStatsDialog.csd.title")));
		RB.setText(csdLabel, "gui.dialog.analysis.PedVerLinesStatsDialog.csd.csdLabel");
	}

	public boolean isOK()
	{
		return true;
	}

	public int getReferenceLine()
	{
		return referenceCombo.getSelectedIndex();
	}

	public int getTestLine()
	{
		return testCombo.getSelectedIndex();
	}

	// Generates a boolean array with a true/false selected state for each of
	// the possible chromosomes that could be used in the sort
	public boolean[] getSelectedChromosomes()
	{
		return csd.getSelectedChromosomes();
	}

	public PedVerLinesThresholds getThresholds()
	{
		return thresholdDialog.getThresholds();
	}

	public boolean isAutoSelectVerifiedLines()
	{
		return thresholdDialog.isAutoSelectVerifiedLines();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        parentsPanel = new javax.swing.JPanel();
        lblParent1 = new javax.swing.JLabel();
        referenceCombo = new javax.swing.JComboBox<>();
        lblParent2 = new javax.swing.JLabel();
        testCombo = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        csdLabel = new scri.commons.gui.matisse.HyperLinkLabel();
        lblThreshold = new scri.commons.gui.matisse.HyperLinkLabel();

        parentsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("General settings:"));

        lblParent1.setText("Select parent1:");
        lblParent1.setToolTipText("");

        lblParent2.setText("Select test line:");
        lblParent2.setToolTipText("");

        javax.swing.GroupLayout parentsPanelLayout = new javax.swing.GroupLayout(parentsPanel);
        parentsPanel.setLayout(parentsPanelLayout);
        parentsPanelLayout.setHorizontalGroup(
            parentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parentsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(parentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(referenceCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(testCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(parentsPanelLayout.createSequentialGroup()
                        .addGroup(parentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblParent1)
                            .addComponent(lblParent2))
                        .addGap(0, 236, Short.MAX_VALUE)))
                .addContainerGap())
        );
        parentsPanelLayout.setVerticalGroup(
            parentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parentsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblParent1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(referenceCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblParent2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(testCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Data selection settings:"));

        csdLabel.setText("Select chromosomes to analyse");

        lblThreshold.setText("Select threshold settings");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(csdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblThreshold, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(csdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblThreshold, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(parentsPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(parentsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private scri.commons.gui.matisse.HyperLinkLabel csdLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblParent1;
    private javax.swing.JLabel lblParent2;
    private scri.commons.gui.matisse.HyperLinkLabel lblThreshold;
    private javax.swing.JPanel parentsPanel;
    private javax.swing.JComboBox<LineInfo> referenceCombo;
    private javax.swing.JComboBox<LineInfo> testCombo;
    // End of variables declaration//GEN-END:variables
}