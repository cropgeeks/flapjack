// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.importer;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.gui.*;
import jhi.flapjack.io.brapi.*;

import scri.commons.gui.*;
import uk.ac.hutton.ics.brapi.resource.genotyping.variant.VariantSet;

class BrapiVariantSetsPanelNB extends JPanel implements IBrapiWizard
{
	private BrapiClient client;
	private List<VariantSet> variantSets;
	private BrapiImportDialog dialog;

	private DefaultComboBoxModel<String> variantsModel;

	public BrapiVariantSetsPanelNB(BrapiClient client, BrapiImportDialog dialog)
	{
		this.client = client;
		this.dialog = dialog;

		initComponents();

		variantsCombo.addActionListener(e -> displayVariantSets() );
	}

	private void displayVariantSets()
	{
		NumberFormat nf = DecimalFormat.getNumberInstance();
		int index = variantsCombo.getSelectedIndex();

		if (index >= 0)
		{
			VariantSet variantSet = variantSets.get(index);

			long sampleCount = variantSet.getCallSetCount();
			long markerCount = variantSet.getVariantCount();

			client.setVariantSetID("" + variantSet.getVariantSetDbId());
			client.setVariantSetName(variantSet.getVariantSetName());
			client.setTotalLines(sampleCount);
			client.setTotalMarkers(markerCount);

			dialog.enableNext(sampleCount > 0 || markerCount > 0);

			String[] columnNames = { "Field", "Description" };
			Object[][] data = {
				{ RB.getString("gui.dialog.importer.BrapiVariantsSetsPanelNB.matrixId"),
				  variantSet.getVariantSetDbId() },
				{ RB.getString("gui.dialog.importer.BrapiVariantsSetsPanelNB.matrixName"),
				  variantSet.getVariantSetName() },
				{ RB.getString("gui.dialog.importer.BrapiVariantsSetsPanelNB.sampleCount"),
				  nf.format(sampleCount) },
				{ RB.getString("gui.dialog.importer.BrapiVariantsSetsPanelNB.markerCount"),
				  nf.format(markerCount) }
			};

			table.setModel(new DefaultTableModel(data, columnNames));
			table.getColumnModel().getColumn(0).setPreferredWidth(25);

			// Calculate and display a file size label which converts the rough size in bytes to kB, MB or GB as
			// appropriate
			long sizeInBytes = sampleCount * markerCount;
			String size = FlapjackUtils.getSizeString(sizeInBytes);

			lblSize.setText(RB.format("gui.dialog.importer.BrapiVariantsSetsPanelNB.lblSize", size));
		}
		else
		{
			table.setModel(new DefaultTableModel());
			lblSize.setText("");
		}

//		dialog.enableNext(index >= 0);
	}

	public boolean refreshData()
	{
		ProgressDialog pd = new ProgressDialog(new DataDownloader(),
			 RB.getString("gui.dialog.importer.BrapiVariantsSetsPanelNB.title"),
			 RB.getString("gui.dialog.importer.BrapiVariantsSetsPanelNB.message"),
			 Flapjack.winMain);

		if (pd.failed("gui.error"))
			return false;

		// Populate the maps combo box
		variantsModel = new DefaultComboBoxModel<String>();

		for (VariantSet variantSet: variantSets)
			variantsModel.addElement(variantSet.getVariantSetDbId() + ": " + variantSet.getVariantSetName());

		variantsCombo.setModel(variantsModel);
		displayVariantSets();

		// TODO: Can we progress if no studies get loaded
		dialog.enableNext(variantsModel.getSize() > 0);

		return true;
	}

	private class DataDownloader extends SimpleJob
	{
		public void runJob(int jobID)
			throws Exception
		{
			variantSets = client.getVariantSets();
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
		client.setVariantSetID(null);
	}

	@Override
	public JPanel getPanel()
		{ return this; }

	@Override
	public String getCardName()
		{ return "variantSets"; }

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
        variantsCombo = new javax.swing.JComboBox<>();
        detailsLabel = new javax.swing.JLabel();
        lblSize = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Variant set selection:"));

        matricesLabel.setLabelFor(variantsCombo);
        matricesLabel.setText("Available variant sets:");

        detailsLabel.setText("Details:");

        lblSize.setText("Flapjack estimates the file will be at least %1 in size");

        table.setModel(new DefaultTableModel());
        jScrollPane2.setViewportView(table);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(matricesLabel)
                        .addGap(11, 11, 11)
                        .addComponent(variantsCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(detailsLabel)
                            .addComponent(lblSize))
                        .addGap(0, 120, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(matricesLabel)
                    .addComponent(variantsCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(detailsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblSize;
    private javax.swing.JLabel matricesLabel;
    private javax.swing.JTable table;
    private javax.swing.JComboBox<String> variantsCombo;
    // End of variables declaration//GEN-END:variables
}