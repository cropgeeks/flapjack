// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import flapjack.gui.*;

import scri.commons.gui.*;

class BrowsePanelNB extends JPanel implements ActionListener
{
	public BrowsePanelNB(String rbLabel, String fileHistory)
	{
		initComponents();

		setBackground((Color)UIManager.get("fjDialogBG"));
		panel.setBackground((Color)UIManager.get("fjDialogBG"));

		panel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.NBBrowsePanel.panel.title")));
		RB.setText(label, rbLabel);
		RB.setText(bBrowse, "gui.text.browse");

		bBrowse.addActionListener(this);
		browseComboBox.setHistory(fileHistory);
	}

	boolean isOK()
	{
		if (browseComboBox.getText().length() == 0)
		{
			TaskDialog.warning(
				RB.getString("gui.dialog.NBBrowsePanel.warning"),
				RB.getString("gui.text.ok"));
			return false;
		}

		return true;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bBrowse)
		{
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle(RB.getString("gui.dialog.NBDataImportPanel.fcTitle"));
			fc.setCurrentDirectory(new File(Prefs.guiCurrentDir));

			if (browseComboBox.getText().length() > 0)
				fc.setCurrentDirectory(new File(browseComboBox.getText()));

			if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
			{
				File file = fc.getSelectedFile();
				Prefs.guiCurrentDir = fc.getCurrentDirectory().toString();

				browseComboBox.updateComboBox(file.toString());
			}
		}
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        label = new javax.swing.JLabel();
        bBrowse = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        browseComboBox = new scri.commons.gui.matisse.HistoryComboBox();

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Browse for file:"));

        label.setLabelFor(browseComboBox);
        label.setText("File to import:");

        bBrowse.setText("Browse...");

        jPanel1.setLayout(new java.awt.BorderLayout());
        jPanel1.add(browseComboBox, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 296, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addComponent(bBrowse)
                .addContainerGap())
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label)
                    .addComponent(bBrowse)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bBrowse;
    scri.commons.gui.matisse.HistoryComboBox browseComboBox;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables

}