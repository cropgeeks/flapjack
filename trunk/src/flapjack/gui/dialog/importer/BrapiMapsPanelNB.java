/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flapjack.gui.dialog.importer;

import javax.swing.*;

import flapjack.io.brapi.*;

import scri.commons.gui.*;

import uk.ac.hutton.brapi.resource.*;

class BrapiMapsPanelNB extends javax.swing.JPanel
{
	private BrapiRequest request;
	private MapList maps;

	private DefaultComboBoxModel<String> model;

	public BrapiMapsPanelNB(BrapiRequest request)
	{
		this.request = request;

		initComponents();

		mapsCombo.addActionListener(e -> displayMap() );
		bRefresh.addActionListener(e -> refreshMaps() );
	}

	private void displayMap()
	{
		int index = mapsCombo.getSelectedIndex();

		if (index >= 0)
		{
			Map map = maps.getMaps().get(index);

			request.setMapIndex(map.getId());

			String str = "Species: " + map.getSpecies() + "\n" +
				"Type: " + map.getType() + "\n" +
				"Unit: " + map.getUnit() + "\n" +
				"Date: " + map.getDate() + "\n" +
				"Markers: " + map.getMarkerCount() + "\n" +
				"Chromosomes: " + map.getChromosomeCount();

			text.setText(str);
		}
		else
			text.setText("");
	}

	void refreshMaps()
	{
		Runnable r = () -> getMaps();
		new Thread(r).start();
	}

	private void getMaps()
	{
		try
		{
			maps = BrapiClient.getMaps();
			System.out.println(maps);
		}
		catch (Exception e)
		{
			TaskDialog.error("BRAPI error: " + e, RB.getString("gui.text.close"));
			return;
		}

		model = new DefaultComboBoxModel<String>();

		for (Map map: maps.getMaps())
			model.addElement(map.getId() + " - " + map.getName());

		mapsCombo.setModel(model);
		displayMap();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        mapsLabel = new javax.swing.JLabel();
        mapsCombo = new javax.swing.JComboBox<String>();
        bRefresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        text = new javax.swing.JTextArea();
        detailsLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Map selection:"));

        mapsLabel.setLabelFor(mapsCombo);
        mapsLabel.setText("Available maps:");

        bRefresh.setText("Refresh");

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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mapsCombo, 0, 274, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bRefresh))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(detailsLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mapsLabel)
                    .addComponent(mapsCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bRefresh))
                .addGap(18, 18, 18)
                .addComponent(detailsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
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
    private javax.swing.JButton bRefresh;
    private javax.swing.JLabel detailsLabel;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> mapsCombo;
    private javax.swing.JLabel mapsLabel;
    private javax.swing.JTextArea text;
    // End of variables declaration//GEN-END:variables
}
