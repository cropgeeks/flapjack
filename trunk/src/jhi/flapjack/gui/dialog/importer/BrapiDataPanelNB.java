// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.importer;

import java.awt.*;
import javax.swing.*;

import jhi.flapjack.gui.*;
import jhi.flapjack.io.brapi.*;

import scri.commons.gui.*;

class BrapiDataPanelNB extends JPanel implements IBrapiWizard
{
	private XmlBrapiProvider data;
	private BrapiClient client;
	private BrapiImportDialog dialog;

	private DefaultComboBoxModel<XmlCategory> catModel = new DefaultComboBoxModel<>();
	private DefaultComboBoxModel<XmlResource> resModel = new DefaultComboBoxModel<>();

	// Currently selected resource
	XmlResource res;

	public BrapiDataPanelNB(BrapiClient client, BrapiImportDialog dialog)
	{
		this.client = client;
		this.dialog = dialog;

		initComponents();

		checkCustom.setSelected(Prefs.guiBrAPIUseCustom);
		customText.setHistory(Prefs.guiBrAPICustomHistory);
		toggleCustom();

		catCombo.setModel(catModel);
		catCombo.addItemListener(e -> displayCategory());

		resCombo.setModel(resModel);
		resCombo.addItemListener(e -> displayResource());

		checkCustom.addActionListener(e -> toggleCustom());
	}

	public void onShow()
	{
		dialog.enableBack(false);

		// On first run refreshData will set enableNext appropriately
		if (catModel.getSize() == 0)
			refreshData();
		// Otherwise we have come back, so must be able to go forward
		else
			dialog.enableNext(true);
	}

	public void onNext()
	{
		Prefs.guiBrAPIUseCustom = checkCustom.isSelected();
		Prefs.guiBrAPICustomHistory = customText.getHistory();
		Prefs.guiBrAPICategoryIndex = catCombo.getSelectedIndex();

		if (checkCustom.isSelected())
		{
			XmlResource customRes = new XmlResource();
			customRes.setUrl(customText.getText());
			client.setResource(customRes);
		}
		else
			client.setResource(res);

		dialog.setScreen(dialog.getPassPanel());
		dialog.getBNext().requestFocusInWindow();
	}

	public JPanel getPanel()
		{ return this; }

	public String getCardName()
		{ return "data"; }

	void refreshData()
	{
		ProgressDialog pd = new ProgressDialog(new DataDownloader(),
			 RB.getString("gui.dialog.importer.BrapiDataPanelNB.title"),
			 RB.getString("gui.dialog.importer.BrapiDataPanelNB.message"),
			 Flapjack.winMain);

		if (pd.failed("gui.error"))
			return;

		catModel.removeAllElements();
		for (XmlCategory cat: data.getCategories())
			catModel.addElement(cat);

		int selIndex = Prefs.guiBrAPICategoryIndex;
		if (selIndex >= 0 && selIndex < catCombo.getItemCount())
			catCombo.setSelectedIndex(selIndex);
	}

	private class DataDownloader extends SimpleJob
	{
		public void runJob(int jobID)
			throws Exception
		{
			data = client.getBrapiProviders();
		}
	}

	private void displayCategory()
	{
		int index = catCombo.getSelectedIndex();

		if (index >= 0)
		{
			// Display the description text
			XmlCategory cat = catModel.getElementAt(index);
			catText.setText(cat.getDescription());
			catText.setCaretPosition(0);

			// Then fill the resources model with its possible options
			resModel.removeAllElements();
			for (XmlResource resource: cat.getResources())
				resModel.addElement(resource);

			// Finally, see if we can grab an image for use in the logo panel
			setIcon(cat.getImage(), catLogo);
		}
		else
		{
			catText.setText(null);
			setIcon(null, catLogo);
		}
	}

	private void displayResource()
	{
		int index = resCombo.getSelectedIndex();

		if (index >= 0)
		{
			// Display the description text
			res = resModel.getElementAt(index);
			resText.setText(res.getDescription());
			resText.setCaretPosition(0);

			// Finally, see if we can grab an image for use in the logo panel
			setIcon(res.getImage(), resLogo);

			client.setResource(res);
			dialog.enableNext(true);
		}
		else
		{
			resText.setText(null);
			setIcon(null, resLogo);
			dialog.enableNext(false);
		}
	}

	// Scales up/down the image to fit the window size
	private void setIcon(ImageIcon icon, JLabel label)
	{
		if (icon == null)
			label.setIcon(null);

		else
		{
			int w = icon.getIconWidth();
			int h = icon.getIconHeight();

			double scalex = (double) label.getSize().width / w;
			double scaley = (double) label.getSize().height / h;
			double scale = Math.min(scalex, scaley);

			Image i = icon.getImage().getScaledInstance(
				(int)(w*scale), (int)(h*scale), Image.SCALE_SMOOTH);

			label.setIcon(new ImageIcon(i));
		}
	}

	public void toggleCustom()
	{
		boolean enabled = checkCustom.isSelected();

		customText.setEnabled(enabled);
		catCombo.setEnabled(!enabled);
		catLogo.setEnabled(!enabled);
		catText.setEnabled(!enabled);
		resCombo.setEnabled(!enabled);
		resLogo.setEnabled(!enabled);
		resText.setEnabled(!enabled);
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

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        catCombo = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        catText = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        resCombo = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        resText = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        catLogo = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        resLogo = new javax.swing.JLabel();
        checkCustom = new javax.swing.JCheckBox();
        customText = new scri.commons.gui.matisse.HistoryComboBox();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Remote data source:"));

        jLabel2.setLabelFor(catCombo);
        jLabel2.setText("Category:");

        jScrollPane1.setHorizontalScrollBar(null);

        catText.setEditable(false);
        catText.setColumns(20);
        catText.setLineWrap(true);
        catText.setRows(5);
        catText.setWrapStyleWord(true);
        jScrollPane1.setViewportView(catText);

        jLabel3.setLabelFor(resCombo);
        jLabel3.setText("Resource to connect to:");

        resCombo.setActionCommand("");

        jScrollPane2.setHorizontalScrollBar(null);

        resText.setEditable(false);
        resText.setColumns(20);
        resText.setLineWrap(true);
        resText.setRows(5);
        resText.setWrapStyleWord(true);
        jScrollPane2.setViewportView(resText);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setOpaque(false);

        catLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(catLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(catLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setOpaque(false);

        resLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(resLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(resLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        checkCustom.setText("Provide a custom BrAPI connection URI:");

        customText.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(catCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE))
                    .addComponent(resCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(checkCustom)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(customText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(catCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkCustom)
                    .addComponent(customText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<XmlCategory> catCombo;
    private javax.swing.JLabel catLogo;
    private javax.swing.JTextArea catText;
    private javax.swing.JCheckBox checkCustom;
    private scri.commons.gui.matisse.HistoryComboBox customText;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JComboBox<XmlResource> resCombo;
    private javax.swing.JLabel resLogo;
    private javax.swing.JTextArea resText;
    // End of variables declaration//GEN-END:variables
}