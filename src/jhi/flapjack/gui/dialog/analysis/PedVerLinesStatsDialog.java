/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jhi.flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jhi.flapjack.analysis.*;
import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

/**
 *
 * @author gs40939
 */
public class PedVerLinesStatsDialog extends JDialog implements ActionListener
{
	private boolean isOK;

	private DefaultComboBoxModel<LineInfo> referenceModel;
	private DefaultComboBoxModel<LineInfo> testModel;

	/**
	 * Creates new form PedVerStatsDialogNew
	 */
	public PedVerLinesStatsDialog(AnalysisSet as)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.analysis.PedVerLinesStatsDialog.title"),
			true
		);

		isOK = false;

        initComponents();
		initComponents2();

		setupComboBoxes(as);

		parentsPanel.setBackground(Color.WHITE);

		getContentPane().setBackground(Color.WHITE);

		getRootPane().setDefaultButton(bOK);
		SwingUtils.addCloseHandler(this, bOK);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
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
		RB.setText(bOK, "gui.text.ok");
		bOK.addActionListener(this);

		RB.setText(bCancel, "gui.text.cancel");
		bCancel.addActionListener(this);
	}

	public boolean isOK()
		{ return isOK; }

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOK)
		{
			isOK = true;
			setVisible(false);
		}

		else if (e.getSource() == bCancel)
			setVisible(false);
	}

	public LineInfo getReferenceLine()
	{
		return (LineInfo)referenceCombo.getSelectedItem();
	}

	public LineInfo getTestLine()
	{
		return (LineInfo)testCombo.getSelectedItem();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        parentsPanel = new javax.swing.JPanel();
        lblParent1 = new javax.swing.JLabel();
        referenceCombo = new javax.swing.JComboBox<>();
        lblParent2 = new javax.swing.JLabel();
        testCombo = new javax.swing.JComboBox<>();
        dialogPanel1 = new scri.commons.gui.matisse.DialogPanel();
        bOK = new javax.swing.JButton();
        bCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        parentsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Select lines:"));

        lblParent1.setText("Select reference line:");
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
                    .addComponent(testCombo, 0, 348, Short.MAX_VALUE)
                    .addGroup(parentsPanelLayout.createSequentialGroup()
                        .addGroup(parentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblParent1)
                            .addComponent(lblParent2))
                        .addGap(0, 246, Short.MAX_VALUE)))
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

        bOK.setText("OK");
        dialogPanel1.add(bOK);

        bCancel.setText("Cancel");
        dialogPanel1.add(bCancel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dialogPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(parentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(parentsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dialogPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bCancel;
    private javax.swing.JButton bOK;
    private scri.commons.gui.matisse.DialogPanel dialogPanel1;
    private javax.swing.JLabel lblParent1;
    private javax.swing.JLabel lblParent2;
    private javax.swing.JPanel parentsPanel;
    private javax.swing.JComboBox<LineInfo> referenceCombo;
    private javax.swing.JComboBox<LineInfo> testCombo;
    // End of variables declaration//GEN-END:variables
}