// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.importer;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.gui.*;
import jhi.flapjack.io.brapi.*;

import scri.commons.gui.*;
import uk.ac.hutton.ics.brapi.resource.core.study.Study;

class BrapiStudiesPanelNB extends JPanel implements IBrapiWizard
{
	private BrapiClient client;
	private List<Study> studies;
	private BrapiImportDialog dialog;

	private DefaultComboBoxModel<String> studiesModel;

	public BrapiStudiesPanelNB(BrapiClient client, BrapiImportDialog dialog)
	{
		this.client = client;
		this.dialog = dialog;

		initComponents();

		studiesCombo.addActionListener(e -> displayStudies() );
	}

	private void displayStudies()
	{
		int index = studiesCombo.getSelectedIndex();

		if (index >= 0)
		{
			Study study = studies.get(index);

			client.setStudyID(study.getStudyDbId());

			String[] columnNames = { "Field", "Description" };
			Object[][] data = {
				{ RB.getString("gui.dialog.importer.BrapiStudiesPanelNB.studyId"),
				  study.getStudyDbId() },
				{ RB.getString("gui.dialog.importer.BrapiStudiesPanelNB.studyName"),
				  study.getStudyName() },
				{ RB.getString("gui.dialog.importer.BrapiStudiesPanelNB.studyActive"),
				  study.getActive() },
				{ RB.getString("gui.dialog.importer.BrapiStudiesPanelNB.studyDescription"),
				  study.getStudyDescription() },
				{ RB.getString("gui.dialog.importer.BrapiStudiesPanelNB.studyType"),
				  study.getStudyType() },
				{ RB.getString("gui.dialog.importer.BrapiStudiesPanelNB.studyStart"),
				  study.getStartDate() },
				{ RB.getString("gui.dialog.importer.BrapiStudiesPanelNB.studyEnd"),
				  study.getEndDate() }
			};

			table.setModel(new DefaultTableModel(data, columnNames));
			table.getColumnModel().getColumn(0).setPreferredWidth(25);
		}
		else
			table.setModel(new DefaultTableModel());

		dialog.enableNext(index >= 0);
	}

	public boolean refreshData()
	{
		ProgressDialog pd = new ProgressDialog(new DataDownloader(),
			 RB.getString("gui.dialog.importer.BrapiStudiesPanelNB.title"),
			 RB.getString("gui.dialog.importer.BrapiStudiesPanelNB.message"),
			 Flapjack.winMain);

		if (pd.failed("gui.error"))
			return false;

		// Populate the maps combo box
		studiesModel = new DefaultComboBoxModel<String>();

		for (Study study: studies)
			studiesModel.addElement(study.getStudyDbId() + ": " + study.getStudyName());

		studiesCombo.setModel(studiesModel);
		displayStudies();

		// TODO: Can we progress if no studies get loaded
		dialog.enableNext(studiesModel.getSize() > 0);

		return true;
	}

	private class DataDownloader extends SimpleJob
	{
		public void runJob(int jobID)
			throws Exception
		{
			if (client.hasStudiesSearch())
				studies = client.getStudies();

			// TODO: for now assume that if we don't have the get, we use the POST as the validation should catch
			// the case where neither are supported
			else
				studies = client.getStudiesByPost();
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
		if (Prefs.guiBrAPIUseMaps)
		{
			if (dialog.getMapsPanel().refreshData())
				dialog.setScreen(dialog.getMapsPanel());
		}

		else if (dialog.getVariantSetsPanel().refreshData())
			dialog.setScreen(dialog.getVariantSetsPanel());

		else
			dialog.wizardCompleted();

		dialog.getBNext().requestFocusInWindow();
	}

	@Override
	public void onBack()
	{
		client.setStudyID(null);
	}

	@Override
	public JPanel getPanel()
		{ return this; }

	@Override
	public String getCardName()
		{ return "studies"; }

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
        detailsLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(499, 276));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Study selection:"));

        mapsLabel.setLabelFor(studiesCombo);
        mapsLabel.setText("Available studies:");

        detailsLabel.setText("Details:");

        table.setModel(new DefaultTableModel());
        jScrollPane2.setViewportView(table);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(mapsLabel)
                        .addGap(11, 11, 11)
                        .addComponent(studiesCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(detailsLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2))
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
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
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
    private javax.swing.JLabel mapsLabel;
    private javax.swing.JComboBox<String> studiesCombo;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}