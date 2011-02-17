// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;

import flapjack.gui.*;

import scri.commons.gui.*;

class NBImportFeaturesPanel extends JPanel implements ActionListener
{
	NBImportFeaturesPanel(boolean isEnabled)
	{
		initComponents();

		setBackground((Color)UIManager.get("fjDialogBG"));
		panel.setBackground((Color)UIManager.get("fjDialogBG"));

		TitledBorder brdr = BorderFactory.createTitledBorder(RB.getString("gui.dialog.NBImportFeaturesPanel.panel"));
		panel.setBorder(brdr);
		RB.setText(label, "gui.dialog.NBImportFeaturesPanel.label");
		RB.setText(bBrowse, "gui.text.browse");
		if (isEnabled)
			RB.setText(tabLabel, "gui.dialog.NBImportFeaturesPanel.tabEnabled");
		else
		{
			RB.setText(tabLabel, "gui.dialog.NBImportFeaturesPanel.tabDisabled");
			brdr.setTitleColor((Color)UIManager.get("Label.disabledForeground"));
		}

		bBrowse.addActionListener(this);
		browseComboBox.setHistory(Prefs.guiQTLHistory);
		browseComboBox.setPrototypeDisplayValue(100);

		panel.setEnabled(isEnabled);
		label.setEnabled(isEnabled);
		browseComboBox.setEnabled(isEnabled);
		bBrowse.setEnabled(isEnabled);
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

		Prefs.guiQTLHistory = browseComboBox.getHistory();

		return true;
	}

	File getFile()
	{
		return new File(browseComboBox.getText());
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
        tabLabel = new javax.swing.JLabel();

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Features file to import:"));

        label.setLabelFor(browseComboBox);
        label.setText("Features file:");

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
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bBrowse)
                .addContainerGap())
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bBrowse))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabLabel.setText("Status message...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabLabel)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabLabel)
                .addGap(18, 18, 18)
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
    private javax.swing.JLabel tabLabel;
    // End of variables declaration//GEN-END:variables

}