// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.importer;

import java.text.*;
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
		NumberFormat nf = DecimalFormat.getNumberInstance();
		int index = matricesCombo.getSelectedIndex();

		if (index >= 0)
		{
			BrapiAlleleMatrixDataset matrix = matrices.get(index);

			client.setMatrixID(matrix.getMatrixDbId());
			int sampleCount = matrix.getSampleCount();
			int markerCount = matrix.getMarkerCount();

			String str = RB.format("gui.dialog.importer.BrapiMatricesPanelNB.matrixName", matrix.getName()) + "\n"
				+ RB.format("gui.dialog.importer.BrapiMatricesPanelNB.matrixId", matrix.getMatrixDbId()) + "\n"
				+ RB.format("gui.dialog.importer.BrapiMatricesPanelNB.sampleCount", nf.format(sampleCount)) + "\n"
				+ RB.format("gui.dialog.importer.BrapiMatricesPanelNB.markerCount", nf.format(markerCount));

			text.setText(str);

			// Calculate and display a file size label which converts the rough size in bytes to kB, MB or GB as
			// appropriate
			long sizeInBytes = sampleCount * markerCount;
			String size = FlapjackUtils.getSizeString(sizeInBytes);

			lblSize.setText(RB.format("gui.dialog.importer.BrapiMatricesPanelNB.lblSize", size));
		}
		else
		{
			text.setText("");
			lblSize.setText("");
		}

		dialog.enableNext(index >= 0);
	}

	public boolean refreshData()
	{
		ProgressDialog pd = new ProgressDialog(new DataDownloader(),
			 RB.getString("gui.dialog.importer.BrapiMatricesPanelNB.title"),
			 RB.getString("gui.dialog.importer.BrapiMatricesPanelNB.message"),
			 Flapjack.winMain);

		if (pd.failed("gui.error"))
			return false;

		// Populate the maps combo box
		matricesModel = new DefaultComboBoxModel<String>();

		for (BrapiAlleleMatrixDataset matrix: matrices)
			matricesModel.addElement(matrix.getName());

		matricesCombo.setModel(matricesModel);
		displayMatrices();

		// TODO: Can we progress if no studies get loaded
		dialog.enableNext(matricesModel.getSize() > 0);

		return true;
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
	}

	@Override
	public void onNext()
	{
		dialog.wizardCompleted();
	}

	@Override
	public void onBack()
	{
		client.setMatrixID(null);
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
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        matricesLabel = new javax.swing.JLabel();
        matricesCombo = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        text = new javax.swing.JTextArea();
        detailsLabel = new javax.swing.JLabel();
        lblSize = new javax.swing.JLabel();

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

        lblSize.setText("Flapjack estimates the file will be at least %1 in size");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(matricesLabel)
                        .addGap(11, 11, 11)
                        .addComponent(matricesCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(detailsLabel)
                            .addComponent(lblSize))
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSize)
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
    private javax.swing.JLabel lblSize;
    private javax.swing.JComboBox<String> matricesCombo;
    private javax.swing.JLabel matricesLabel;
    private javax.swing.JTextArea text;
    // End of variables declaration//GEN-END:variables
}