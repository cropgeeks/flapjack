// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;

import jhi.flapjack.gui.table.*;
import scri.commons.gui.*;

public class PedVerLinesThresholdDialog extends JDialog implements ActionListener, ChangeListener
{
	private boolean isOK = false;

	private int data;
	private int parentHet;
	private int simToParents;

	private PedVerLinesThresholds thresholds;
	private LineDataTable table;

	public PedVerLinesThresholdDialog()
	{
		this(PedVerLinesThresholds.fromUserDefaults());
	}

	public PedVerLinesThresholdDialog(PedVerLinesThresholds thresholds, LineDataTable table)
	{
		this(thresholds);
		this.table = table;
	}

	public PedVerLinesThresholdDialog(PedVerLinesThresholds thresholds)
	{
		super(Flapjack.winMain, RB.getString("gui.dialog.analysis.ThresholdSettingsDialog.title"), true);
		this.thresholds = thresholds;

		data = thresholds.getData();
		parentHet = thresholds.getParentHet();
		simToParents = thresholds.getSimToParents();

		initComponents();
		initComponents2();

		RB.setText(bOk, "gui.text.ok");

		RB.setText(bHelp, "gui.text.help");
//		FlapjackUtils.setHelp(bHelp, "analysis_results_tables.html#filtering-lines");

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
		setupSlider(sliderPercData, data);
		SpinnerNumberModel parentHetSpinModel = new SpinnerNumberModel(data, 0, 100, 1);
		percDataSpinner.setModel(parentHetSpinModel);

		// Perfecentage heterozygosity of F1s
		setupSlider(sliderPercParentHet, parentHet);
		SpinnerNumberModel f1HetSpinModel = new SpinnerNumberModel(parentHet, 0, 100, 1);
		percParentHetSpinner.setModel(f1HetSpinModel);

		// Percentage heterozygosity of line/sample
		setupSlider(sliderSimToP1P2, simToParents);
		SpinnerNumberModel hetSpinModel = new SpinnerNumberModel(simToParents, 0, 100, 1);
		simToParentsSpinner.setModel(hetSpinModel);

		sliderPercParentHet.addChangeListener(this);
		sliderSimToP1P2.addChangeListener(this);
		sliderPercData.addChangeListener(this);
		percParentHetSpinner.addChangeListener(this);
		simToParentsSpinner.addChangeListener(this);
		percDataSpinner.addChangeListener(this);

		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
		model.addElement("Simple");
		model.addElement("Intermediate");
		model.addElement("Detailed");

		chkVerifiedLines.setSelected(Prefs.pedVerLinesAutoSelect);
		chkVerifiedLines.addActionListener(this);
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
			Prefs.pedVerLinesDataThreshold = data;
			Prefs.pedVerF1ParentHetThreshold = parentHet;
			Prefs.pedVerLinesSimToParentsThreshold = simToParents;

			setVisible(false);
		}

		else if (e.getSource() == chkVerifiedLines)
		{
			Prefs.pedVerLinesAutoSelect = chkVerifiedLines.isSelected();
			if (Prefs.pedVerLinesAutoSelect && table != null)
				table.autoSelectVerifiedLines();
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
		if (e.getSource() == sliderPercData)
		{
			data = handleSliderChange(sliderPercData, percDataSpinner);
			thresholds.setData(data);
		}

		else if (e.getSource() == percDataSpinner)
		{
			data = handleSpinnerChange(percDataSpinner, sliderPercData);
			thresholds.setData(data);
		}

		else if (e.getSource() == sliderPercParentHet)
		{
			parentHet = handleSliderChange(sliderPercParentHet, percParentHetSpinner);
			thresholds.setParentHet(parentHet);
		}

		else if (e.getSource() == percParentHetSpinner)
		{
			parentHet = handleSpinnerChange(percParentHetSpinner, sliderPercParentHet);
			thresholds.setParentHet(parentHet);
		}

		else if (e.getSource() == sliderSimToP1P2)
		{
			simToParents = handleSliderChange(sliderSimToP1P2, simToParentsSpinner);
			thresholds.setSimToParents(simToParents);
		}

		else if (e.getSource() == simToParentsSpinner)
		{
			simToParents = handleSpinnerChange(simToParentsSpinner, sliderSimToP1P2);
			thresholds.setSimToParents(simToParents);
		}

		if (table != null)
		{
			table.getLineDataTableModel().fireTableDataChanged();
			if (Prefs.pedVerLinesAutoSelect)
				table.autoSelectVerifiedLines();
		}
	}

	public PedVerLinesThresholds getThresholds()
	{
		return thresholds;
	}

	public boolean isAutoSelectVerifiedLines()
	{
		return chkVerifiedLines.isSelected();
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
        sliderPercParentHet = new javax.swing.JSlider();
        sliderPercData = new javax.swing.JSlider();
        lblPercData = new javax.swing.JLabel();
        simToParentsSpinner = new javax.swing.JSpinner();
        sliderSimToP1P2 = new javax.swing.JSlider();
        percParentHetSpinner = new javax.swing.JSpinner();
        percDataSpinner = new javax.swing.JSpinner();
        lblSimToP1P2 = new javax.swing.JLabel();
        lblPercParentHet = new javax.swing.JLabel();
        decisionPanel = new javax.swing.JPanel();
        chkVerifiedLines = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        bOk.setText("OK");
        dialogPanel1.add(bOk);

        bHelp.setText("Help");
        dialogPanel1.add(bHelp);

        thresholdPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Threshold settings:"));

        lblPercData.setText("Percent Data (>=):");

        lblSimToP1P2.setText("Percent Similarity To P1/P2 (>=):");

        lblPercParentHet.setText("Percent Parental Heterozygosity (<=):");

        javax.swing.GroupLayout thresholdPanelLayout = new javax.swing.GroupLayout(thresholdPanel);
        thresholdPanel.setLayout(thresholdPanelLayout);
        thresholdPanelLayout.setHorizontalGroup(
            thresholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(thresholdPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(thresholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSimToP1P2)
                    .addComponent(lblPercData)
                    .addComponent(lblPercParentHet))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(thresholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(thresholdPanelLayout.createSequentialGroup()
                        .addComponent(sliderSimToP1P2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(simToParentsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(thresholdPanelLayout.createSequentialGroup()
                        .addComponent(sliderPercParentHet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(percParentHetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(thresholdPanelLayout.createSequentialGroup()
                        .addComponent(sliderPercData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(percDataSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                    .addComponent(lblPercParentHet)
                    .addComponent(sliderPercParentHet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(percParentHetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(thresholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblSimToP1P2)
                    .addComponent(sliderSimToP1P2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(simToParentsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        decisionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Decision settings:"));

        chkVerifiedLines.setText("Automatically select verified lines");

        javax.swing.GroupLayout decisionPanelLayout = new javax.swing.GroupLayout(decisionPanel);
        decisionPanel.setLayout(decisionPanelLayout);
        decisionPanelLayout.setHorizontalGroup(
            decisionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(decisionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkVerifiedLines)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        decisionPanelLayout.setVerticalGroup(
            decisionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(decisionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkVerifiedLines)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dialogPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
    private javax.swing.JCheckBox chkVerifiedLines;
    private javax.swing.JPanel decisionPanel;
    private scri.commons.gui.matisse.DialogPanel dialogPanel1;
    private javax.swing.JLabel lblPercData;
    private javax.swing.JLabel lblPercParentHet;
    private javax.swing.JLabel lblSimToP1P2;
    private javax.swing.JSpinner percDataSpinner;
    private javax.swing.JSpinner percParentHetSpinner;
    private javax.swing.JSpinner simToParentsSpinner;
    javax.swing.JSlider sliderPercData;
    javax.swing.JSlider sliderPercParentHet;
    javax.swing.JSlider sliderSimToP1P2;
    private javax.swing.JPanel thresholdPanel;
    // End of variables declaration//GEN-END:variables
}