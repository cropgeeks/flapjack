// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.importer;

import java.util.*;
import javax.swing.*;

import jhi.flapjack.gui.*;
import jhi.flapjack.io.brapi.*;

import jhi.brapi.api.allelematrices.*;

import scri.commons.gui.*;

class BrapiMatricesPanelNB extends JPanel implements IBrapiWizard
{
	private BrapiClient client;
	private List<BrapiAlleleMatrixDataset> matrices;
	private BrapiImportDialog dialog;

	private DefaultComboBoxModel<String> matricesModel;

	public BrapiMatricesPanelNB(BrapiClient client, BrapiImportDialog dialog)
	{
		this.client = client;
		this.dialog = dialog;

		initComponents();

		matricesCombo.addActionListener(e -> displayMatrices() );
	}

	private void displayMatrices()
	{
		int index = matricesCombo.getSelectedIndex();

		if (index >= 0)
		{
			BrapiAlleleMatrixDataset matrix = matrices.get(index);

			client.setMatrixID(matrix.getMatrixDbId());
			text.setText(matrix.getName() + " - " + matrix.getMatrixDbId());
		}
		else
			text.setText("");

		dialog.enableNext(index >= 0);
	}

	private void refreshMatrices()
	{
		ProgressDialog pd = new ProgressDialog(new DataDownloader(),
			 RB.getString("gui.dialog.importer.BrapiMatricesPanelNB.title"),
			 RB.getString("gui.dialog.importer.BrapiMatricesPanelNB.message"),
			 Flapjack.winMain);

		if (pd.failed("gui.error"))
			return;

		// Populate the maps combo box
		matricesModel = new DefaultComboBoxModel<String>();

		for (BrapiAlleleMatrixDataset matrix: matrices)
			matricesModel.addElement(matrix.getMatrixDbId() + " - " + matrix.getName());

		matricesCombo.setModel(matricesModel);
		displayMatrices();

		// TODO: Can we progress if no studies get loaded
		dialog.enableNext(matricesModel.getSize() > 0);
	}

	private class DataDownloader extends SimpleJob
	{
		public void runJob(int jobID)
			throws Exception
		{
			matrices = client.getMatrices();
		}
	}

	@Override
	public void onShow()
	{
		dialog.enableBack(true);
		dialog.enableNext(matricesModel != null && matricesModel.getSize() > 0);

		if (matricesModel == null || matricesModel.getSize() == 0)
			refreshMatrices();
	}

	@Override
	public void onNext()
	{
		dialog.wizardCompleted();
	}

	@Override
	public void onBack()
	{
		dialog.setScreen(dialog.getMapsPanel());
		dialog.getBBack().requestFocusInWindow();
	}

	@Override
	public JPanel getPanel()
		{ return this; }

	@Override
	public String getCardName()
		{ return "matrices"; }

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jPanel2 = new javax.swing.JPanel();
        matricesLabel = new javax.swing.JLabel();
        matricesCombo = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        text = new javax.swing.JTextArea();
        detailsLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Matrix selection:"));

        matricesLabel.setLabelFor(matricesCombo);
        matricesLabel.setText("Available matrices:");

        text.setEditable(false);
        text.setColumns(20);
        text.setRows(5);
        jScrollPane1.setViewportView(text);

        detailsLabel.setText("Details:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(matricesLabel)
                        .addGap(11, 11, 11)
                        .addComponent(matricesCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(detailsLabel)
                        .addGap(0, 405, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(matricesLabel)
                    .addComponent(matricesCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(detailsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel detailsLabel;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> matricesCombo;
    private javax.swing.JLabel matricesLabel;
    private javax.swing.JTextArea text;
    // End of variables declaration//GEN-END:variables
}