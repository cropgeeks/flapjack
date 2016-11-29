// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.importer;

import java.util.List;
import javax.swing.*;

import jhi.flapjack.gui.*;
import jhi.flapjack.io.brapi.*;

import scri.commons.gui.*;

import jhi.brapi.resource.*;

class BrapiStudiesPanelNB extends javax.swing.JPanel
{
	private BrapiRequest request;
	private List<BrapiStudies> studies;
	private BrapiImportDialog dialog;

	private DefaultComboBoxModel<String> studiesModel;

	public BrapiStudiesPanelNB(BrapiRequest request, BrapiImportDialog dialog)
	{
		this.request = request;
		this.dialog = dialog;

		initComponents();

		studiesCombo.addActionListener(e -> displayStudies() );
	}

	private void displayStudies()
	{
		int index = studiesCombo.getSelectedIndex();

		if (index >= 0)
		{
			BrapiStudies study = studies.get(index);

			request.setStudyID(study.getStudyDbId());

//			String str = "Species: " + map.getSpecies() + "\n" +
//				"Type: " + map.getType() + "\n" +
//				"Unit: " + map.getUnit() + "\n" +
//				"Date: " + map.getPublishedDate() + "\n" +
//				"Markers: " + map.getMarkerCount() + "\n" +
//				"Chromosomes: " + map.getLinkageGroupCount();

			text.setText(study.getName() + " - " + study.getStudyDbId());
		}
		else
			text.setText("");

		dialog.enableNext(index >= 0);
	}

	boolean refreshStudies()
	{
		ProgressDialog pd = new ProgressDialog(new DataDownloader(),
			 RB.getString("gui.dialog.importer.BrapiStudiesPanelNB.title"),
			 RB.getString("gui.dialog.importer.BrapiStudiesPanelNB.message"),
			 Flapjack.winMain);

		if (pd.failed("gui.error"))
			return false;

		// Populate the maps combo box
		studiesModel = new DefaultComboBoxModel<String>();

		for (BrapiStudies study: studies)
			studiesModel.addElement(study.getStudyDbId() + " - " + study.getName());

		studiesCombo.setModel(studiesModel);
		displayStudies();

		return true;
	}

	private class DataDownloader extends SimpleJob
	{
		public void runJob(int jobID)
			throws Exception
		{
			BrapiClient.setXmlResource(
				request.getResource());

			BrapiClient.doAuthentication(
				request.getUsername(), request.getPassword());

			studies = BrapiClient.getStudies();
		}
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

        jPanel2 = new javax.swing.JPanel();
        mapsLabel = new javax.swing.JLabel();
        studiesCombo = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        text = new javax.swing.JTextArea();
        detailsLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Study selection:"));

        mapsLabel.setLabelFor(studiesCombo);
        mapsLabel.setText("Available studies:");

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
                        .addComponent(mapsLabel)
                        .addGap(11, 11, 11)
                        .addComponent(studiesCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    .addComponent(mapsLabel)
                    .addComponent(studiesCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
    private javax.swing.JLabel mapsLabel;
    private javax.swing.JComboBox<String> studiesCombo;
    private javax.swing.JTextArea text;
    // End of variables declaration//GEN-END:variables
}