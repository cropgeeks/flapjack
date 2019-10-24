// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;

import jhi.flapjack.gui.table.*;
import scri.commons.gui.*;

public class ThresholdDialog extends JDialog implements ActionListener, ChangeListener
{
	private boolean isOK = false;

	private int parentHet;
	private int f1Het;
	private int error;
	private int f1Match;
	private int het;

	private PedVerF1sThresholds thresholds;
	private LineDataTable table;

	public ThresholdDialog()
	{
		this(PedVerF1sThresholds.fromUserDefaults());
	}

	public ThresholdDialog(PedVerF1sThresholds thresholds, LineDataTable table)
	{
		this(thresholds);
		this.table = table;
	}

	public ThresholdDialog(PedVerF1sThresholds thresholds)
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

		RB.setText(bCancel, "gui.text.cancel");
		RB.setText(bOk, "gui.text.ok");

		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "analysis_results_tables.html#filtering-lines");

		getContentPane().setBackground((Color)UIManager.get("fjDialogBG"));
		bOk.addActionListener(this);
		bCancel.addActionListener(this);

		getRootPane().setDefaultButton(bOk);
		SwingUtils.addCloseHandler(this, bCancel);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
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

			setVisible(false);
		}

		else if (e.getSource() == bCancel)
			setVisible(false);
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
			table.getLineDataTableModel().fireTableDataChanged();
	}

	public PedVerF1sThresholds getThresholds()
	{
		return thresholds;
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
    private void initComponents()
    {

        dialogPanel1 = new scri.commons.gui.matisse.DialogPanel();
        bOk = new javax.swing.JButton();
        bCancel = new javax.swing.JButton();
        bHelp = new javax.swing.JButton();
        lblPercParentHet = new javax.swing.JLabel();
        sliderPercParentHet = new javax.swing.JSlider();
        percParentalHetSpinner = new javax.swing.JSpinner();
        percF1HetSpinner = new javax.swing.JSpinner();
        sliderPercF1Het = new javax.swing.JSlider();
        lblPercF1Het = new javax.swing.JLabel();
        lblPercHet = new javax.swing.JLabel();
        sliderPercHet = new javax.swing.JSlider();
        percHetSpinner = new javax.swing.JSpinner();
        percErrorSpinner = new javax.swing.JSpinner();
        lblPercError = new javax.swing.JLabel();
        lblPercF1 = new javax.swing.JLabel();
        sliderPercError = new javax.swing.JSlider();
        sliderPercF1 = new javax.swing.JSlider();
        percF1Spinner = new javax.swing.JSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        bOk.setText("OK");
        dialogPanel1.add(bOk);

        bCancel.setText("Cancel");
        dialogPanel1.add(bCancel);

        bHelp.setText("Help");
        dialogPanel1.add(bHelp);

        lblPercParentHet.setText("Percent Parental Heterozygosity:");

        lblPercF1Het.setText("Percent F1 Heterozygosity:");

        lblPercHet.setText("Percent Heterozygosity:");

        lblPercError.setText("Percent Error Rate:");

        lblPercF1.setText("Percent Match to F1:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dialogPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPercHet)
                    .addComponent(lblPercF1)
                    .addComponent(lblPercError)
                    .addComponent(lblPercParentHet)
                    .addComponent(lblPercF1Het))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sliderPercHet, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                            .addComponent(sliderPercError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(sliderPercF1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(percHetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(percF1Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(percErrorSpinner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(sliderPercF1Het, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(percF1HetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(sliderPercParentHet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(percParentalHetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblPercParentHet)
                    .addComponent(sliderPercParentHet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(percParentalHetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblPercF1Het)
                    .addComponent(sliderPercF1Het, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(percF1HetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblPercHet)
                    .addComponent(sliderPercHet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(percHetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblPercError)
                    .addComponent(sliderPercError, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(percErrorSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblPercF1)
                    .addComponent(sliderPercF1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(percF1Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(dialogPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bCancel;
    private javax.swing.JButton bHelp;
    private javax.swing.JButton bOk;
    private scri.commons.gui.matisse.DialogPanel dialogPanel1;
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
    // End of variables declaration//GEN-END:variables
}