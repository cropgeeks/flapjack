// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import java.awt.event.*;
import javax.swing.*;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;
import jhi.flapjack.data.pedigree.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class MABCStatsSinglePanelNB extends JPanel implements ActionListener
{
	private GTViewSet viewSet;
	private ChromosomeSelectionDialog csd;
	private MABCThresholdDialog thresholdDialog;

	private AnalysisSet as;

	private DefaultComboBoxModel<LineInfo> rpModel;
	private DefaultComboBoxModel<LineInfo> dpModel;

	MABCStatsSinglePanelNB(GTViewSet viewSet)
	{
		this.viewSet = viewSet;

		// This analysis will run on selected lines/markers only
		as = new AnalysisSet(this.viewSet)
			.withViews(null)
			.withSelectedLines()
			.withSelectedMarkers();

		initComponents();
		initComponents2();
	}

	private void initComponents2()
	{
		maxMrkrCoverage.setValue(Prefs.mabcMaxMrkrCoverage);

		RB.setText(chkExcludeParents, "gui.dialog.analysis.MABCStatsDialog.chkExlcudeParents");
		chkExcludeParents.addActionListener(this);
		chkExcludeParents.setSelected(Prefs.guiMabcExcludeParents);

		csd = new ChromosomeSelectionDialog(viewSet, true, true);
		csdLabel.addActionListener(e -> csd.setVisible(true));

		// Calculates the highest possible values for the QTL allele count statistic and passes this
		// to the threshold dialog to initialize components
		int maxQtlAlleleCount = 0;
		for (GTView view : viewSet.getViews())
		{
			for (QTLInfo qtl : view.getQTLs())
				if (qtl.getQTL().isVisible())
					maxQtlAlleleCount += 2;
		}

		thresholdDialog = new MABCThresholdDialog(maxQtlAlleleCount);
		lblThreshold.addActionListener(e -> thresholdDialog.setVisible(true));

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

		FlapjackUtils.initPanel(settingsPanel, dataPanel);
	}

	private void setupComboBoxes(AnalysisSet as)
	{
		createRPComboModelFrom(as);
		recurrentCombo.setModel(rpModel);

		createDPComboModelFrom(as);
		donorCombo.setModel(dpModel);


		// Warn if we *have* ped info, and have ended up with multiple RP or DP lines
		PedManager pm = viewSet.getDataSet().getPedManager();

		if (pm.getPedigrees().size() > 0 && (rpModel.getSize() > 1 || dpModel.getSize() > 1))
		{
			ParentSelector selector = new ParentSelector();
			ProgressDialog dialog = new ProgressDialog(selector,
				RB.getString("gui.dialog.analysis.MABCStatsDialog.parentSelectorProgress.title"),
				RB.getString("gui.dialog.analysis.MABCStatsDialog.parentSelectorProgress.label"),
				Flapjack.winMain);
		}


		// Preselect lines 0 and 1 for cases with no pedigree information
		if (pm.getPedigrees().size() == 0)
		{
			if (rpModel.getSize() >= 1)
				recurrentCombo.setSelectedIndex(0);

			if (dpModel.getSize() >= 2)
				donorCombo.setSelectedIndex(1);
		}
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
		LineInfo rpInfo = (LineInfo)recurrentCombo.getSelectedItem();
		return as.getLines().indexOf(rpInfo);
	}

	public int getDonorParent()
	{
		LineInfo dpInfo = (LineInfo)donorCombo.getSelectedItem();
		return as.getLines().indexOf(dpInfo);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bWeighted)
		{
			jLabel3.setEnabled(true);
			maxMrkrCoverage.setEnabled(true);
		}

		else if (e.getSource() == bUnweighted)
		{
			jLabel3.setEnabled(false);
			maxMrkrCoverage.setEnabled(false);
		}

		else if (e.getSource() == chkExcludeParents)
			Prefs.guiMabcExcludeParents = chkExcludeParents.isSelected();
	}

	class ParentSelector extends SimpleJob
	{
		@Override
		public void runJob(int jobIndex)
			throws Exception
		{
			selectParents(rpModel);
			selectParents(dpModel);
		}

		void selectParents(DefaultComboBoxModel<LineInfo> model)
		{
			int maxMarkerCount = 0;
			LineInfo found = null;

			for (int i=0; i < model.getSize(); i++)
			{
				LineInfo lineInfo = model.getElementAt(i);

				int lineIndex = as.getLines().indexOf(lineInfo);
				int viewCount = as.viewCount();

				int markerCount = 0;

				for (int viewIndex = 0; viewIndex < viewCount; viewIndex++)
					for (int markerIndex = 0; markerIndex < as.markerCount(viewIndex); markerIndex++)
						if (as.getState(viewIndex, lineIndex, markerIndex) > 0)
							markerCount++;

				if (markerCount > maxMarkerCount)
				{
					maxMarkerCount = markerCount;
					found = lineInfo;
				}
			}

			if(found != null)
				model.setSelectedItem(found);
		}
	}

	boolean isOK()
	{
		Prefs.guiUseSimpleMabcStats = bUnweighted.isSelected();
		Prefs.mabcMaxMrkrCoverage = (Double) maxMrkrCoverage.getValue();

		// If fewer than 2 lines are selected, disable the OK button.
		if (viewSet.getView(0).countSelectedLines() < 2)
			return false;
//			bOK.setEnabled(false);

		return true;
	}

	public MABCThresholds getThresholds()
		{ return thresholdDialog.getThresholds(); }


	public boolean isAutoSelect()
	{
		return thresholdDialog.isAutoSelect();
	}


	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        settingsPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        maxMrkrCoverage = new javax.swing.JSpinner();
        lblParent1 = new javax.swing.JLabel();
        recurrentCombo = new javax.swing.JComboBox<>();
        lblParent2 = new javax.swing.JLabel();
        donorCombo = new javax.swing.JComboBox<>();
        bWeighted = new javax.swing.JRadioButton();
        bUnweighted = new javax.swing.JRadioButton();
        chkExcludeParents = new javax.swing.JCheckBox();
        dataPanel = new javax.swing.JPanel();
        csdLabel = new scri.commons.gui.matisse.HyperLinkLabel();
        lblThreshold = new scri.commons.gui.matisse.HyperLinkLabel();

        settingsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("General settings:"));

        jLabel3.setText("Maximum coverage per marker (cM): ");

        maxMrkrCoverage.setModel(new javax.swing.SpinnerNumberModel(10.0d, 0.0d, null, 0.25d));

        lblParent1.setText("Select recurrent parent line:");
        lblParent1.setToolTipText("");

        lblParent2.setText("Select donor parent line:");
        lblParent2.setToolTipText("");

        bWeighted.setText("Weighted model");

        bUnweighted.setText("Unweighted model");

        chkExcludeParents.setText("Exclude other parental lines from analysis and view");

        javax.swing.GroupLayout settingsPanelLayout = new javax.swing.GroupLayout(settingsPanel);
        settingsPanel.setLayout(settingsPanelLayout);
        settingsPanelLayout.setHorizontalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(settingsPanelLayout.createSequentialGroup()
                        .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblParent1)
                            .addComponent(lblParent2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(donorCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(recurrentCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(settingsPanelLayout.createSequentialGroup()
                        .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bWeighted)
                            .addComponent(bUnweighted)
                            .addGroup(settingsPanelLayout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(maxMrkrCoverage, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chkExcludeParents))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(14, 14, 14))
        );
        settingsPanelLayout.setVerticalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblParent1)
                    .addComponent(recurrentCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblParent2)
                    .addComponent(donorCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(chkExcludeParents)
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

        lblThreshold.setText("Select threshold settings");

        javax.swing.GroupLayout dataPanelLayout = new javax.swing.GroupLayout(dataPanel);
        dataPanel.setLayout(dataPanelLayout);
        dataPanelLayout.setHorizontalGroup(
            dataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(csdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblThreshold, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        dataPanelLayout.setVerticalGroup(
            dataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataPanelLayout.createSequentialGroup()
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
                .addComponent(dataPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton bUnweighted;
    private javax.swing.JRadioButton bWeighted;
    private javax.swing.JCheckBox chkExcludeParents;
    private scri.commons.gui.matisse.HyperLinkLabel csdLabel;
    private javax.swing.JPanel dataPanel;
    private javax.swing.JComboBox<LineInfo> donorCombo;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lblParent1;
    private javax.swing.JLabel lblParent2;
    private scri.commons.gui.matisse.HyperLinkLabel lblThreshold;
    private javax.swing.JSpinner maxMrkrCoverage;
    private javax.swing.JComboBox<LineInfo> recurrentCombo;
    private javax.swing.JPanel settingsPanel;
    // End of variables declaration//GEN-END:variables
}