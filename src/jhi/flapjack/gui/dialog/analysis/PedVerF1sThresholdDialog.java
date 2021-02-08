// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;

import jhi.flapjack.gui.table.*;
import scri.commons.gui.*;

public class PedVerF1sThresholdDialog extends JDialog implements ActionListener, ChangeListener
{
	private boolean isOK = false;

	private int parentHet;
	private int f1Het;
	private int error;
	private int f1Match;
	private int het;

	private PedVerF1sThresholds thresholds;
	private LineDataTable table;

	public PedVerF1sThresholdDialog()
	{
		this(PedVerF1sThresholds.fromUserDefaults());
	}

	public PedVerF1sThresholdDialog(PedVerF1sThresholds thresholds, LineDataTable table)
	{
		this(thresholds);
		this.table = table;
	}

	public PedVerF1sThresholdDialog(PedVerF1sThresholds thresholds)
	{
		super(Flapjack.winMain, RB.getString("gui.dialog.analysis.ThresholdSettingsDialog.title"), true);
		this.thresholds = thresholds;

		parentHet = thresholds.getParentHetThreshold();
		f1Het = thresholds.getF1isHetThreshold();
		error = thresholds.getErrorThreshold();
		f1Match = thresholds.getF1Threshold();
		het = thresholds.getHetThreshold();

		initComponents();
		initComponents2();

		RB.setText(bOk, "gui.text.ok");

		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "analysis_results_tables.html#filtering-lines");

		bOk.addActionListener(this);

		getRootPane().setDefaultButton(bOk);
		SwingUtils.addCloseHandler(this, bOk);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);

		FlapjackUtils.initPanel(getContentPane(), decisionPanel, thresholdPanel);
	}

	private void initComponents2()
	{
		// Percentage heterozygosity of parents
		setupSlider(sliderPercParentHet, parentHet);
		SpinnerNumberModel parentHetSpinModel = new SpinnerNumberModel(parentHet, 0, 100, 1);
		percParentalHetSpinner.setModel(parentHetSpinModel);

		// Perfecentage heterozygosity of F1s
		setupSlider(sliderPercF1Het, f1Het);
		SpinnerNumberModel f1HetSpinModel = new SpinnerNumberModel(f1Het, 0, 100, 1);
		percF1HetSpinner.setModel(f1HetSpinModel);

		// Percentage heterozygosity of line/sample
		setupSlider(sliderPercHet, het);
		SpinnerNumberModel hetSpinModel = new SpinnerNumberModel(het, 0, 100, 1);
		percHetSpinner.setModel(hetSpinModel);

		// Percentage match of line/sample to F1
		setupSlider(sliderPercF1, f1Match);
		SpinnerNumberModel f1SpinModel = new SpinnerNumberModel(f1Match, 0, 100, 1);
		percF1Spinner.setModel(f1SpinModel);

		// Percentage error found in line
		setupSlider(sliderPercError, error);
		SpinnerNumberModel errorSpinModel = new SpinnerNumberModel(error, 0, 100, 1);
		percErrorSpinner.setModel(errorSpinModel);

		// Disable the percentage error components as we don't have this statistic yet
		lblPercError.setVisible(false);
		sliderPercError.setVisible(false);
		percErrorSpinner.setVisible(false);

		sliderPercF1Het.addChangeListener(this);
		sliderPercF1.addChangeListener(this);
		sliderPercError.addChangeListener(this);
		sliderPercHet.addChangeListener(this);
		sliderPercParentHet.addChangeListener(this);
		percErrorSpinner.addChangeListener(this);
		percF1HetSpinner.addChangeListener(this);
		percF1Spinner.addChangeListener(this);
		percHetSpinner.addChangeListener(this);
		percParentalHetSpinner.addChangeListener(this);

		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
		model.addElement("Simple");
		model.addElement("Intermediate");
		model.addElement("Detailed");
		decisionModelCombo.setModel(model);
		decisionModelCombo.setSelectedIndex(Prefs.pedVerDecisionModel);
		decisionModelCombo.addActionListener(event -> {
			Prefs.pedVerDecisionModel = decisionModelCombo.getSelectedIndex();
			if (table != null)
				table.updatePedVerDecsionModel(Prefs.pedVerDecisionModel);
		});

		chkTrueF1s.setSelected(Prefs.pedVerF1sAutoSelect);
		chkTrueF1s.addActionListener(this);
	}

	private void setupSlider(JSlider slider, int value)
	{
		DefaultBoundedRangeModel model = new DefaultBoundedRangeModel(value, 0, 0, 100);
		slider.setModel(model);
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(5);
		slider.setPaintTicks(true);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOk)
		{
			isOK = true;

			// Set the Prefs variables so that they are remembered for next time
			Prefs.pedVerF1ParentHetThreshold = parentHet;
			Prefs.pedVerF1isHetThreshold = f1Het;
			Prefs.pedVerF1ErrorThreshold = error;
			Prefs.pedVerF1F1Threshold = f1Match;
			Prefs.pedVerF1HetThreshold = het;

			Prefs.pedVerDecisionModel = decisionModelCombo.getSelectedIndex();

			setVisible(false);
		}

		else if (e.getSource() == chkTrueF1s)
		{
			Prefs.pedVerF1sAutoSelect = chkTrueF1s.isSelected();
			if (Prefs.pedVerF1sAutoSelect && table != null)
				table.autoSelectTrueF1s();
		}
	}

	private int handleSliderChange(JSlider slider, JSpinner spinner)
	{
		int threshold = slider.getValue();
		spinner.setValue(threshold);

		return threshold;
	}

	private int handleSpinnerChange(JSpinner spinner, JSlider slider)
	{
		int threshold = (int)spinner.getValue();
		slider.setValue(threshold);

		return threshold;
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() == sliderPercHet)
		{
			het = handleSliderChange(sliderPercHet, percHetSpinner);
			thresholds.setHetThreshold(het);
		}

		else if (e.getSource() == percHetSpinner)
		{
			het = handleSpinnerChange(percHetSpinner, sliderPercHet);
			thresholds.setHetThreshold(het);
		}

		else if (e.getSource() == sliderPercF1)
		{
			f1Match = handleSliderChange(sliderPercF1, percF1Spinner);
			thresholds.setF1Threshold(f1Match);
		}

		else if (e.getSource() == percF1Spinner)
		{
			f1Match = handleSpinnerChange(percF1Spinner, sliderPercF1);
			thresholds.setF1Threshold(f1Match);
		}

		else if (e.getSource() == sliderPercError)
		{
			error = handleSliderChange(sliderPercError, percErrorSpinner);
			thresholds.setErrorThreshold(error);
		}

		else if (e.getSource() == percErrorSpinner)
		{
			error = handleSpinnerChange(percErrorSpinner, sliderPercError);
			thresholds.setErrorThreshold(error);
		}

		else if (e.getSource() == sliderPercParentHet)
		{
			parentHet = handleSliderChange(sliderPercParentHet, percParentalHetSpinner);
			thresholds.setParentHetThreshold(parentHet);
		}

		else if (e.getSource() == percParentalHetSpinner)
		{
			parentHet = handleSpinnerChange(percParentalHetSpinner, sliderPercParentHet);
			thresholds.setParentHetThreshold(parentHet);
		}

		else if (e.getSource() == sliderPercF1Het)
		{
			f1Het = handleSliderChange(sliderPercF1Het, percF1HetSpinner);
			thresholds.setF1isHetThreshold(f1Het);
		}

		else if (e.getSource() == percF1HetSpinner)
		{
			f1Het = handleSpinnerChange(percF1HetSpinner, sliderPercF1Het);
			thresholds.setF1isHetThreshold(f1Het);
		}

		if (table != null)
		{
			table.getLineDataTableModel().fireTableDataChanged();
			if (isAutoSelectTrueF1s())
				table.autoSelectTrueF1s();
		}
	}

	public PedVerF1sThresholds getThresholds()
	{
		return thresholds;
	}

	public int getDecisionModelIndex()
	{
		return decisionModelCombo.getSelectedIndex();
	}

	public boolean isAutoSelectTrueF1s()
	{
		return chkTrueF1s.isSelected();
	}

	public boolean isOK()
		{ return isOK; }

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dialogPanel1 = new scri.commons.gui.matisse.DialogPanel();
        bOk = new javax.swing.JButton();
        bHelp = new javax.swing.JButton();
        thresholdPanel = new javax.swing.JPanel();
        sliderPercF1Het = new javax.swing.JSlider();
        lblPercError = new javax.swing.JLabel();
        sliderPercError = new javax.swing.JSlider();
        sliderPercParentHet = new javax.swing.JSlider();
        lblPercParentHet = new javax.swing.JLabel();
        percHetSpinner = new javax.swing.JSpinner();
        sliderPercHet = new javax.swing.JSlider();
        percF1HetSpinner = new javax.swing.JSpinner();
        percParentalHetSpinner = new javax.swing.JSpinner();
        lblPercF1 = new javax.swing.JLabel();
        lblPercHet = new javax.swing.JLabel();
        sliderPercF1 = new javax.swing.JSlider();
        lblPercF1Het = new javax.swing.JLabel();
        percF1Spinner = new javax.swing.JSpinner();
        percErrorSpinner = new javax.swing.JSpinner();
        decisionPanel = new javax.swing.JPanel();
        lblDecisionModel = new javax.swing.JLabel();
        decisionModelCombo = new javax.swing.JComboBox<>();
        chkTrueF1s = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        bOk.setText("OK");
        dialogPanel1.add(bOk);

        bHelp.setText("Help");
        dialogPanel1.add(bHelp);

        thresholdPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Threshold settings:"));

        lblPercError.setText("Percent Error Rate:");

        lblPercParentHet.setText("Percent Parental Heterozygosity (<=):");

        lblPercF1.setText("Percent Match to F1 (>=):");

        lblPercHet.setText("Percent Heterozygosity (>=):");

        lblPercF1Het.setText("Percent F1 Heterozygosity (>=):");

        javax.swing.GroupLayout thresholdPanelLayout = new javax.swing.GroupLayout(thresholdPanel);
        thresholdPanel.setLayout(thresholdPanelLayout);
        thresholdPanelLayout.setHorizontalGroup(
            thresholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(thresholdPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(thresholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPercHet)
                    .addComponent(lblPercF1)
                    .addComponent(lblPercError)
                    .addComponent(lblPercParentHet)
                    .addComponent(lblPercF1Het))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(thresholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(thresholdPanelLayout.createSequentialGroup()
                        .addGroup(thresholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sliderPercHet, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                            .addComponent(sliderPercError, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                            .addComponent(sliderPercF1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(thresholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(percHetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(percF1Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(percErrorSpinner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(thresholdPanelLayout.createSequentialGroup()
                        .addComponent(sliderPercF1Het, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(percF1HetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(thresholdPanelLayout.createSequentialGroup()
                        .addComponent(sliderPercParentHet, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(percParentalHetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        thresholdPanelLayout.setVerticalGroup(
            thresholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(thresholdPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(thresholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblPercParentHet)
                    .addComponent(sliderPercParentHet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(percParentalHetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(thresholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblPercF1Het)
                    .addComponent(sliderPercF1Het, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(percF1HetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(thresholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblPercHet)
                    .addComponent(sliderPercHet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(percHetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(thresholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblPercError)
                    .addComponent(sliderPercError, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(percErrorSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(thresholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblPercF1)
                    .addComponent(sliderPercF1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(percF1Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        decisionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Decision settings:"));

        lblDecisionModel.setText("F1 decision model:");

        chkTrueF1s.setText("Automatically select True F1s");

        javax.swing.GroupLayout decisionPanelLayout = new javax.swing.GroupLayout(decisionPanel);
        decisionPanel.setLayout(decisionPanelLayout);
        decisionPanelLayout.setHorizontalGroup(
            decisionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(decisionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(decisionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(decisionPanelLayout.createSequentialGroup()
                        .addComponent(lblDecisionModel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(decisionModelCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(decisionPanelLayout.createSequentialGroup()
                        .addComponent(chkTrueF1s)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        decisionPanelLayout.setVerticalGroup(
            decisionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(decisionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(decisionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDecisionModel)
                    .addComponent(decisionModelCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkTrueF1s)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dialogPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(decisionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(thresholdPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(thresholdPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(decisionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dialogPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bHelp;
    private javax.swing.JButton bOk;
    private javax.swing.JCheckBox chkTrueF1s;
    private javax.swing.JComboBox<String> decisionModelCombo;
    private javax.swing.JPanel decisionPanel;
    private scri.commons.gui.matisse.DialogPanel dialogPanel1;
    private javax.swing.JLabel lblDecisionModel;
    private javax.swing.JLabel lblPercError;
    private javax.swing.JLabel lblPercF1;
    private javax.swing.JLabel lblPercF1Het;
    private javax.swing.JLabel lblPercHet;
    private javax.swing.JLabel lblPercParentHet;
    private javax.swing.JSpinner percErrorSpinner;
    private javax.swing.JSpinner percF1HetSpinner;
    private javax.swing.JSpinner percF1Spinner;
    private javax.swing.JSpinner percHetSpinner;
    private javax.swing.JSpinner percParentalHetSpinner;
    javax.swing.JSlider sliderPercError;
    javax.swing.JSlider sliderPercF1;
    javax.swing.JSlider sliderPercF1Het;
    javax.swing.JSlider sliderPercHet;
    javax.swing.JSlider sliderPercParentHet;
    private javax.swing.JPanel thresholdPanel;
    // End of variables declaration//GEN-END:variables
}