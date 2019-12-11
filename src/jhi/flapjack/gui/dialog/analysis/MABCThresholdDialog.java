// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;

import jhi.flapjack.gui.table.*;
import scri.commons.gui.*;

public class MABCThresholdDialog extends JDialog implements ActionListener, ChangeListener
{
	private boolean isOK = false;

	private int percData;
	private int rppTotal;
	private int qtlAlleleCount;

	private MABCThresholds thresholds;
	private LineDataTable table;

	private int maxQtlAlleleCount;

	public MABCThresholdDialog(int maxQtlAlleleCount)
	{
		this(MABCThresholds.fromUserDefaults(), maxQtlAlleleCount);
	}

	public MABCThresholdDialog(MABCThresholds thresholds, int maxQtlAlleleCount, LineDataTable table)
	{
		this(thresholds, maxQtlAlleleCount);
		this.table = table;
	}

	public MABCThresholdDialog(MABCThresholds thresholds, int maxQtlAlleleCount)
	{
		super(Flapjack.winMain, RB.getString("gui.dialog.analysis.ThresholdSettingsDialog.title"), true);
		this.thresholds = thresholds;
		this.maxQtlAlleleCount = maxQtlAlleleCount;

		percData = thresholds.getPercData();
		rppTotal = thresholds.getRppTotal();
		qtlAlleleCount = thresholds.getQtlAlleleCount();

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
		// Percentage data of each line
		DefaultBoundedRangeModel sliderPercModel = new DefaultBoundedRangeModel(percData, 0, 0, 100);
		sliderPercData.setModel(sliderPercModel);
		SpinnerNumberModel parentHetSpinModel = new SpinnerNumberModel(percData, 0, 100, 1);
		percDataSpinner.setModel(parentHetSpinModel);

		// Recurrent parent proportion in each line
		DefaultBoundedRangeModel sliderRppModel = new DefaultBoundedRangeModel(rppTotal, 0, 0, 100);
		sliderRPPTotal.setModel(sliderRppModel);
		SpinnerNumberModel f1HetSpinModel = new SpinnerNumberModel(rppTotal/100d, 0, 1, 0.01);
		percRPPTotalSpinner.setModel(f1HetSpinModel);

		qtlAlleleCount = Math.min(qtlAlleleCount, maxQtlAlleleCount);
		// QTL Allele count for each line
		DefaultBoundedRangeModel qtlModel = new DefaultBoundedRangeModel(qtlAlleleCount, 0, 0, maxQtlAlleleCount);
		sliderQtlAlleleCount.setModel(qtlModel);
		SpinnerNumberModel hetSpinModel = new SpinnerNumberModel(qtlAlleleCount, 0, maxQtlAlleleCount, 1);
		qtlAlleleCountSpinner.setModel(hetSpinModel);

		sliderPercData.addChangeListener(this);
		sliderRPPTotal.addChangeListener(this);
		sliderQtlAlleleCount.addChangeListener(this);

		percDataSpinner.addChangeListener(this);
		percRPPTotalSpinner.addChangeListener(this);
		qtlAlleleCountSpinner.addChangeListener(this);

		chkAutoSelect.setSelected(Prefs.mabcAutoSelect);
		chkAutoSelect.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOk)
		{
			isOK = true;

			// Set the Prefs variables so that they are remembered for next time
			Prefs.mabcDataThreshold = percData;
			Prefs.mabcRPPTotalThreshold = rppTotal;
			Prefs.mabcQTLAlleleCountThreshold = qtlAlleleCount;

			setVisible(false);
		}

		else if (e.getSource() == chkAutoSelect)
		{
			Prefs.mabcAutoSelect = chkAutoSelect.isSelected();
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
		if (e.getSource() == sliderQtlAlleleCount)
		{
			qtlAlleleCount = handleSliderChange(sliderQtlAlleleCount, qtlAlleleCountSpinner);
			thresholds.setQtlAlleleCount(qtlAlleleCount);
		}

		else if (e.getSource() == qtlAlleleCountSpinner)
		{
			qtlAlleleCount = handleSpinnerChange(qtlAlleleCountSpinner, sliderQtlAlleleCount);
			thresholds.setQtlAlleleCount(qtlAlleleCount);
		}

		else if (e.getSource() == sliderPercData)
		{
			percData = handleSliderChange(sliderPercData, percDataSpinner);
			thresholds.setPercData(percData);
		}

		else if (e.getSource() == percDataSpinner)
		{
			percData = handleSpinnerChange(percDataSpinner, sliderPercData);
			thresholds.setPercData(percData);
		}

		else if (e.getSource() == sliderRPPTotal)
		{
			rppTotal = sliderRPPTotal.getValue();
			percRPPTotalSpinner.setValue(rppTotal/100d);
			thresholds.setRppTotal(rppTotal);
		}

		else if (e.getSource() == percRPPTotalSpinner)
		{
			rppTotal = (int)((double)percRPPTotalSpinner.getValue() * 100);
			sliderRPPTotal.setValue(rppTotal);
			thresholds.setRppTotal(rppTotal);
		}

		if (table != null)
			table.getLineDataTableModel().fireTableDataChanged();
	}

	public MABCThresholds getThresholds()
	{
		return thresholds;
	}

	public boolean isAutoSelect()
	{
		return chkAutoSelect.isSelected();
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
        sliderRPPTotal = new javax.swing.JSlider();
        sliderPercData = new javax.swing.JSlider();
        lblPercData = new javax.swing.JLabel();
        qtlAlleleCountSpinner = new javax.swing.JSpinner();
        sliderQtlAlleleCount = new javax.swing.JSlider();
        percRPPTotalSpinner = new javax.swing.JSpinner();
        percDataSpinner = new javax.swing.JSpinner();
        lblQtlAlleleCount = new javax.swing.JLabel();
        lblRPPTotal = new javax.swing.JLabel();
        decisionPanel = new javax.swing.JPanel();
        chkAutoSelect = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        bOk.setText("OK");
        dialogPanel1.add(bOk);

        bHelp.setText("Help");
        dialogPanel1.add(bHelp);

        thresholdPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Threshold settings:"));

        lblPercData.setText("Percent data (>=):");

        lblQtlAlleleCount.setText("QTL allele count (>=):");

        lblRPPTotal.setText("RPP total (>=):");

        javax.swing.GroupLayout thresholdPanelLayout = new javax.swing.GroupLayout(thresholdPanel);
        thresholdPanel.setLayout(thresholdPanelLayout);
        thresholdPanelLayout.setHorizontalGroup(
            thresholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(thresholdPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(thresholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblQtlAlleleCount)
                    .addComponent(lblPercData)
                    .addComponent(lblRPPTotal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(thresholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sliderQtlAlleleCount, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sliderRPPTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sliderPercData, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(thresholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(percRPPTotalSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(percDataSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(qtlAlleleCountSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        thresholdPanelLayout.setVerticalGroup(
            thresholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(thresholdPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(thresholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblPercData)
                    .addComponent(sliderPercData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(percDataSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(thresholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblRPPTotal)
                    .addComponent(sliderRPPTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(percRPPTotalSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(thresholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblQtlAlleleCount)
                    .addComponent(sliderQtlAlleleCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(qtlAlleleCountSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        decisionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Decision settings:"));

        chkAutoSelect.setText("Automatically select lines");

        javax.swing.GroupLayout decisionPanelLayout = new javax.swing.GroupLayout(decisionPanel);
        decisionPanel.setLayout(decisionPanelLayout);
        decisionPanelLayout.setHorizontalGroup(
            decisionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(decisionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkAutoSelect)
                .addContainerGap(297, Short.MAX_VALUE))
        );
        decisionPanelLayout.setVerticalGroup(
            decisionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(decisionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkAutoSelect)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dialogPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(thresholdPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(decisionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
    private javax.swing.JCheckBox chkAutoSelect;
    private javax.swing.JPanel decisionPanel;
    private scri.commons.gui.matisse.DialogPanel dialogPanel1;
    private javax.swing.JLabel lblPercData;
    private javax.swing.JLabel lblQtlAlleleCount;
    private javax.swing.JLabel lblRPPTotal;
    private javax.swing.JSpinner percDataSpinner;
    private javax.swing.JSpinner percRPPTotalSpinner;
    private javax.swing.JSpinner qtlAlleleCountSpinner;
    javax.swing.JSlider sliderPercData;
    javax.swing.JSlider sliderQtlAlleleCount;
    javax.swing.JSlider sliderRPPTotal;
    private javax.swing.JPanel thresholdPanel;
    // End of variables declaration//GEN-END:variables
}