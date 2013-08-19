package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.gui.visualization.*;
import flapjack.gui.visualization.colors.*;

import scri.commons.gui.*;

public class AlleleFrequencyDialog extends JDialog implements ActionListener, ChangeListener
{
	private GenotypePanel gPanel;
	private GTViewSet viewSet;

	public AlleleFrequencyDialog(GenotypePanel gPanel)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.AlleleFrequencyDialog.title"),
			true
		);

		this.gPanel = gPanel;
		viewSet = gPanel.getViewSet();

		initComponents();
		initComponents2();
		getContentPane().setBackground(Color.WHITE);

		getRootPane().setDefaultButton(bOK);
		SwingUtils.addCloseHandler(this, bOK);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private void initComponents2()
	{
		RB.setText(bOK, "gui.text.ok");
		bOK.addActionListener(this);

		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "gui.dialog.AlleleFrequencyDialog");

		RB.setText(thresholdLabel, "gui.dialog.NBAlleleFrequencyPanel.thresholdLabel");
		RB.setText(hintLabel, "gui.dialog.NBAlleleFrequencyPanel.hintLabel");

		slider.addChangeListener(this);
		int value = (int) (viewSet.getAlleleFrequencyThreshold() * 200);
		slider.setValue(value);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOK)
			setVisible(false);
	}

	public void stateChanged(ChangeEvent e)
	{
		int value = slider.getValue();
		float threshold = value / 200f;

		float percent = value / 2f;
		percentLabel.setText(percent + "%");

		viewSet.setAlleleFrequencyThreshold(threshold);
		viewSet.setColorScheme(ColorScheme.ALLELE_FREQUENCY);
		gPanel.refreshView();

		Actions.projectModified();
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dialogPanel1 = new scri.commons.gui.matisse.DialogPanel();
        bOK = new javax.swing.JButton();
        bHelp = new javax.swing.JButton();
        hintLabel = new javax.swing.JLabel();
        thresholdLabel = new javax.swing.JLabel();
        percentLabel = new javax.swing.JLabel();
        slider = new javax.swing.JSlider();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        bOK.setText("OK");
        dialogPanel1.add(bOK);

        bHelp.setText("Help");
        dialogPanel1.add(bHelp);

        hintLabel.setText("To adjust this threshold later on, simply reapply the colour scheme");

        thresholdLabel.setText("Low/high cutoff threshold:");

        percentLabel.setText("50%");

        slider.setMajorTickSpacing(50);
        slider.setMaximum(200);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dialogPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(slider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(thresholdLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(percentLabel))
                    .addComponent(hintLabel))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(thresholdLabel)
                    .addComponent(percentLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(slider, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(hintLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(dialogPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bHelp;
    private javax.swing.JButton bOK;
    private scri.commons.gui.matisse.DialogPanel dialogPanel1;
    private javax.swing.JLabel hintLabel;
    private javax.swing.JLabel percentLabel;
    private javax.swing.JSlider slider;
    private javax.swing.JLabel thresholdLabel;
    // End of variables declaration//GEN-END:variables

}