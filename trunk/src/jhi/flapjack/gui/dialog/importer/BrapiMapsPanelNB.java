// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.importer;

import java.util.*;
import javax.swing.*;

import jhi.flapjack.gui.*;
import jhi.flapjack.io.brapi.*;

import scri.commons.gui.*;

import jhi.brapi.api.genomemaps.*;

class BrapiMapsPanelNB extends JPanel implements IBrapiWizard
{
	private BrapiClient client;
	private List<BrapiGenomeMap> maps;

	private BrapiImportDialog dialog;

	private DefaultComboBoxModel<String> mapModel;

	public BrapiMapsPanelNB(BrapiClient client, BrapiImportDialog dialog)
	{
		this.client = client;
		this.dialog = dialog;

		initComponents();

		checkSkipMap.setSelected(Prefs.guiBrapiSkipMap);
		checkSkipMap.addActionListener(e -> {
			Prefs.guiBrapiSkipMap = checkSkipMap.isSelected();
			displayMap();
		});

		mapsCombo.addActionListener(e -> displayMap());
	}

	private void displayMap()
	{
		int index = mapsCombo.getSelectedIndex();

		if (index >= 0)
		{
			BrapiGenomeMap map = maps.get(index);

			client.setMapID("" + map.getMapDbId());

			String str = "Species: " + map.getSpecies() + "\n" +
				"Type: " + map.getType() + "\n" +
				"Unit: " + map.getUnit() + "\n" +
				"Date: " + map.getPublishedDate() + "\n" +
				"Markers: " + map.getMarkerCount() + "\n" +
				"Chromosomes: " + map.getLinkageGroupCount();

			text.setText(str);
		}
		else
			text.setText("");

		dialog.enableNext(index >= 0 || checkSkipMap.isSelected());
	}

	private void refreshMaps()
	{
		ProgressDialog pd = new ProgressDialog(new DataDownloader(),
			 RB.getString("gui.dialog.importer.BrapiMapsPanelNB.title"),
			 RB.getString("gui.dialog.importer.BrapiMapsPanelNB.message"),
			 Flapjack.winMain);

		if (pd.failed("gui.error"))
			return;

		// Populate the maps combo box
		mapModel = new DefaultComboBoxModel<String>();

		for (BrapiGenomeMap map: maps)
			mapModel.addElement(map.getMapDbId() + " - " + map.getName());

		mapsCombo.setModel(mapModel);
		displayMap();
	}

	private class DataDownloader extends SimpleJob
	{
		public void runJob(int jobID)
			throws Exception
		{
			maps = client.getMaps();
		}
	}

	@Override
	public void onShow()
	{
		dialog.enableBack(true);

		if (mapModel == null || mapModel.getSize() == 0)
			refreshMaps();
	}

	@Override
	public void onNext()
	{
		if (client.hasAlleleMatrices())
		{
			dialog.setScreen(dialog.getMatricesPanel());
			dialog.getBNext().requestFocusInWindow();
		}
		else
			dialog.wizardCompleted();
	}

	@Override
	public void onBack()
	{
		dialog.setScreen(dialog.getStudiesPanel());
		dialog.getBBack().requestFocusInWindow();
	}

	@Override
	public JPanel getPanel()
		{ return this; }

	@Override
	public String getCardName()
		{ return "maps"; }

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
        mapsCombo = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        text = new javax.swing.JTextArea();
        detailsLabel = new javax.swing.JLabel();
        checkSkipMap = new javax.swing.JCheckBox();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Map selection:"));

        mapsLabel.setLabelFor(mapsCombo);
        mapsLabel.setText("Available maps:");

        text.setEditable(false);
        text.setColumns(20);
        text.setRows(5);
        jScrollPane1.setViewportView(text);

        detailsLabel.setText("Details:");

        checkSkipMap.setText("Skip using a map (not recommended) - a 'dummy' chromosome will hold markers instead");

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
                        .addComponent(mapsCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(checkSkipMap)
                            .addComponent(detailsLabel))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mapsLabel)
                    .addComponent(mapsCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(detailsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkSkipMap)
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
    private javax.swing.JCheckBox checkSkipMap;
    private javax.swing.JLabel detailsLabel;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> mapsCombo;
    private javax.swing.JLabel mapsLabel;
    private javax.swing.JTextArea text;
    // End of variables declaration//GEN-END:variables
}