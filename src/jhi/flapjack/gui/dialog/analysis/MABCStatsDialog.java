// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import java.awt.event.*;
import javax.swing.*;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;
import jhi.flapjack.data.pedigree.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class MABCStatsDialog extends JDialog implements ActionListener
{
	private GTViewSet viewSet;
	private ChromosomeSelectionDialog csd;

	private AnalysisSet as;

	private DefaultComboBoxModel<LineInfo> rpModel;
	private DefaultComboBoxModel<LineInfo> dpModel;

	private boolean isOK;

	public MABCStatsDialog(GTViewSet viewSet)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.analysis.MABCStatsDialog.title"),
			true
		);

		this.viewSet = viewSet;
		isOK = false;

		// This analysis will run on selected lines/markers only
		as = new AnalysisSet(this.viewSet)
			.withViews(null)
			.withSelectedLines()
			.withSelectedMarkers();

		initComponents();
		initComponents2();

		FlapjackUtils.initDialog(this, bOK, bCancel, true,
			getContentPane(), settingsPanel, dataPanel);
	}

	private void initComponents2()
	{
		maxMrkrCoverage.setValue(Prefs.mabcMaxMrkrCoverage);

//		RB.setText(bOK, "gui.text.ok");
		bOK.addActionListener(this);

		RB.setText(bCancel, "gui.text.cancel");
		bCancel.addActionListener(this);

		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "mabc.html");

		csd = new ChromosomeSelectionDialog(viewSet, true);
		csdLabel.addActionListener(e -> { csd.setVisible(true); } );

		setupComboBoxes(as);

		ButtonGroup bGroup = new ButtonGroup();
		bGroup.add(bWeighted);
		bGroup.add(bUnweighted);

		bWeighted.addActionListener(this);
		bUnweighted.addActionListener(this);

		// Set the selection state for the weighted / unweighted radio buttons
		// based on the last choice the user made (assumes Weighted by default)
		bWeighted.setSelected(!Prefs.guiUseSimpleMabcStats);
		bUnweighted.setSelected(Prefs.guiUseSimpleMabcStats);

		// Set the enabled state of the max marker coverage spinner and its
		// label based on whether or not we are using the weighted calculation
		jLabel3.setEnabled(!Prefs.guiUseSimpleMabcStats);
		maxMrkrCoverage.setEnabled(!Prefs.guiUseSimpleMabcStats);

		// If fewer than 2 lines are selected, disable the OK button.
		if (viewSet.getView(0).countSelectedLines() < 2)
			bOK.setEnabled(false);
	}

	private void setupComboBoxes(AnalysisSet as)
	{
		createRPComboModelFrom(as);
		recurrentCombo.setModel(rpModel);
		if (rpModel.getSize() >= 1)
			recurrentCombo.setSelectedIndex(0);

		createDPComboModelFrom(as);
		donorCombo.setModel(dpModel);
		if (dpModel.getSize() >= 1)
			donorCombo.setSelectedIndex(0);
	}

	private void createRPComboModelFrom(AnalysisSet as)
	{
		PedManager pm = viewSet.getDataSet().getPedManager();

		rpModel = new DefaultComboBoxModel<>();
		for (int i = 0; i < as.lineCount(); i++)
			if (pm.isRP(as.getLine(i)))
				rpModel.addElement(as.getLine(i));

		// If no RP lines are found, add all of them anyway
		if (rpModel.getSize() == 0)
			for (int i = 0; i < as.lineCount(); i++)
				rpModel.addElement(as.getLine(i));
	}

	private void createDPComboModelFrom(AnalysisSet as)
	{
		PedManager pm = viewSet.getDataSet().getPedManager();

		dpModel = new DefaultComboBoxModel<>();
		for (int i = 0; i < as.lineCount(); i++)
			if (pm.isDP(as.getLine(i)))
				dpModel.addElement(as.getLine(i));

		// If no DP lines are found, add all of them anyway
		if (dpModel.getSize() == 0)
			for (int i = 0; i < as.lineCount(); i++)
				dpModel.addElement(as.getLine(i));
	}

	// Generates a boolean array with a true/false selected state for each of
	// the possible chromosomes that could be used in the sort
	public boolean[] getSelectedChromosomes()
	{
		return csd.getSelectedChromosomes();
	}

	public int getRecurrentParent()
	{
		return recurrentCombo.getSelectedIndex();
	}

	public int getDonorParent()
	{
		return donorCombo.getSelectedIndex();
	}

	public boolean isOK()
		{ return isOK; }

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOK)
		{
			Prefs.mabcMaxMrkrCoverage = (Double) maxMrkrCoverage.getValue();

			isOK = true;
			setVisible(false);
		}

		else if (e.getSource() == bCancel)
			setVisible(false);

		else if (e.getSource() == bWeighted)
		{
			jLabel3.setEnabled(true);
			maxMrkrCoverage.setEnabled(true);
			Prefs.guiUseSimpleMabcStats = bUnweighted.isSelected();
		}

		else if (e.getSource() == bUnweighted)
		{
			jLabel3.setEnabled(false);
			maxMrkrCoverage.setEnabled(false);
			Prefs.guiUseSimpleMabcStats = bUnweighted.isSelected();
		}
	}

	public boolean isSimpleStats()
	{
		return Prefs.guiUseSimpleMabcStats;
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

        dialogPanel1 = new scri.commons.gui.matisse.DialogPanel();
        bOK = new javax.swing.JButton();
        bCancel = new javax.swing.JButton();
        bHelp = new javax.swing.JButton();
        settingsPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        maxMrkrCoverage = new javax.swing.JSpinner();
        lblParent1 = new javax.swing.JLabel();
        recurrentCombo = new javax.swing.JComboBox<>();
        lblParent2 = new javax.swing.JLabel();
        donorCombo = new javax.swing.JComboBox<>();
        bWeighted = new javax.swing.JRadioButton();
        bUnweighted = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        dataPanel = new javax.swing.JPanel();
        csdLabel = new scri.commons.gui.matisse.HyperLinkLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        bOK.setText("Run");
        dialogPanel1.add(bOK);

        bCancel.setText("Cancel");
        dialogPanel1.add(bCancel);

        bHelp.setText("Help");
        dialogPanel1.add(bHelp);

        settingsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("General settings:"));

        jLabel3.setText("Maximum coverage per marker (cM): ");

        maxMrkrCoverage.setModel(new javax.swing.SpinnerNumberModel(10.0d, 0.0d, null, 0.25d));

        lblParent1.setText("Select recurrent parent line:");
        lblParent1.setToolTipText("");

        lblParent2.setText("Select donor parent line:");
        lblParent2.setToolTipText("");

        bWeighted.setText("Weighted model");

        bUnweighted.setText("Unweighted model");

        jLabel1.setText("<html>Marker Assisted Back Crossing statistics will calculate Recurrent Parent Percentages<br>for each line across each chromosome, and will also display linkage drag and QTL<br>status information if appropriate.");

        javax.swing.GroupLayout settingsPanelLayout = new javax.swing.GroupLayout(settingsPanel);
        settingsPanel.setLayout(settingsPanelLayout);
        settingsPanelLayout.setHorizontalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(settingsPanelLayout.createSequentialGroup()
                        .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bWeighted)
                            .addComponent(bUnweighted)
                            .addGroup(settingsPanelLayout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(maxMrkrCoverage, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(settingsPanelLayout.createSequentialGroup()
                        .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblParent1)
                            .addComponent(lblParent2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(donorCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(recurrentCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        settingsPanelLayout.setVerticalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, settingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblParent1)
                    .addComponent(recurrentCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblParent2)
                    .addComponent(donorCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(bWeighted)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(maxMrkrCoverage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bUnweighted)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dataPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Data selection settings:"));

        csdLabel.setText("Select chromosomes to analyse");

        javax.swing.GroupLayout dataPanelLayout = new javax.swing.GroupLayout(dataPanel);
        dataPanel.setLayout(dataPanelLayout);
        dataPanelLayout.setHorizontalGroup(
            dataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(csdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        dataPanelLayout.setVerticalGroup(
            dataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(csdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dialogPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(settingsPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dataPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(settingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dataPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dialogPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bCancel;
    private javax.swing.JButton bHelp;
    private javax.swing.JButton bOK;
    private javax.swing.JRadioButton bUnweighted;
    private javax.swing.JRadioButton bWeighted;
    private scri.commons.gui.matisse.HyperLinkLabel csdLabel;
    private javax.swing.JPanel dataPanel;
    private scri.commons.gui.matisse.DialogPanel dialogPanel1;
    private javax.swing.JComboBox<LineInfo> donorCombo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lblParent1;
    private javax.swing.JLabel lblParent2;
    private javax.swing.JSpinner maxMrkrCoverage;
    private javax.swing.JComboBox<LineInfo> recurrentCombo;
    private javax.swing.JPanel settingsPanel;
    // End of variables declaration//GEN-END:variables

}