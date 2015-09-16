// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.importer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import jhi.flapjack.gui.*;
import jhi.flapjack.io.DataImporter;

import scri.commons.gui.*;
import scri.commons.gui.matisse.HistoryComboBox;

class ImportGenoTabNB extends javax.swing.JPanel implements ActionListener
{
	private JDialog parent;

	ImportGenoTabNB(JDialog parent)
	{
		initComponents();

		this.parent = parent;

		setBackground((Color)UIManager.get("fjDialogBG"));
		filePanel.setBackground((Color)UIManager.get("fjDialogBG"));
		optionsPanel.setBackground((Color)UIManager.get("fjDialogBG"));

		mapButton.addActionListener(this);
		genoButton.addActionListener(this);
		hdf5Button.addActionListener(this);
		optionsButton.addActionListener(this);

		mapComboBox.setHistory(Prefs.guiMapList);
		genoComboBox.setHistory(Prefs.guiGenoList);
		hdf5ComboBox.setHistory(Prefs.guiHDF5List);
		mapComboBox.setPrototypeDisplayValue("");
		genoComboBox.setPrototypeDisplayValue("");
		hdf5ComboBox.setPrototypeDisplayValue("");


		// Apply localized text
		RB.setText(tabLabel, "gui.dialog.NBDataImportPanel.tabLabel");
		filePanel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.NBDataImportPanel.filePanel")));
		RB.setText(useText, "gui.dialog.NBDataImportPanel.useText");
		RB.setText(mapLabel, "gui.dialog.NBDataImportPanel.mapLabel");
		mapButton.setText(RB.getString("gui.text.browse"));
		RB.setText(genoLabel, "gui.dialog.NBDataImportPanel.genoLabel");
		genoButton.setText(RB.getString("gui.text.browse"));
		RB.setText(useHDF5, "gui.dialog.NBDataImportPanel.useHDF5");
		RB.setText(hdf5Label, "gui.dialog.NBDataImportPanel.hdf5Label");
		hdf5Button.setText(RB.getString("gui.text.browse"));
		optionsPanel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.NBDataImportPanel.optionsPanel")));
		RB.setText(optionsLabel, "gui.dialog.NBDataImportPanel.optionsLabel");
		RB.setText(optionsButton, "gui.dialog.NBDataImportPanel.optionsButton");

		ButtonGroup bg = new ButtonGroup();
		bg.add(useText);
		bg.add(useHDF5);
		useText.setSelected(Prefs.guiImportType == 0);
		useHDF5.setSelected(Prefs.guiImportType == 2);
		setStates();

		mapComboBox.addActionListener(this);
		genoComboBox.addActionListener(this);
		hdf5ComboBox.addActionListener(this);

		useText.addActionListener(this);
		useHDF5.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == mapButton)
			browse(mapComboBox);

		else if (e.getSource() == genoButton)
			browse(genoComboBox);

		else if (e.getSource() == hdf5Button)
			browse(hdf5ComboBox);

		else if (e.getSource() == useText || e.getSource() == useHDF5)
			setStates();

		else if (e.getSource() == optionsButton)
			new ImportGenoAdvDialog(parent);
	}

	private void setStates()
	{
		Prefs.guiImportType = useHDF5.isSelected() ? DataImporter.IMPORT_HDF5 : DataImporter.IMPORT_CLASSIC;

		mapLabel.setEnabled(useText.isSelected());
		mapComboBox.setEnabled(useText.isSelected());
		mapButton.setEnabled(useText.isSelected());
		genoLabel.setEnabled(useText.isSelected());
		genoComboBox.setEnabled(useText.isSelected());
		genoButton.setEnabled(useText.isSelected());

		hdf5Label.setEnabled(useHDF5.isSelected());
		hdf5ComboBox.setEnabled(useHDF5.isSelected());
		hdf5Button.setEnabled(useHDF5.isSelected());
	}

	private void browse(HistoryComboBox combo)
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(RB.getString("gui.dialog.NBDataImportPanel.fcTitle"));
		fc.setCurrentDirectory(new File(Prefs.guiCurrentDir));

		if (combo.getText().length() > 0)
			fc.setCurrentDirectory(new File(combo.getText()));

//		Filters.setFilters(fc, -1, FAS, PHY_S, PHY_I, ALN, MSF, NEX, NEX_B);

		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			Prefs.guiCurrentDir = fc.getCurrentDirectory().toString();

			combo.updateComboBox(file.toString());
		}
	}

	boolean isOK()
	{
		// Fail if the map/genotype boxes are empty
		if (useText.isSelected() && (mapComboBox.getText().length() == 0 || genoComboBox.getText().length() == 0))
		{
			TaskDialog.warning(
				RB.getString("gui.dialog.NBDataImportPanel.warn1"),
				RB.getString("gui.text.ok"));

			return false;
		}
		// Fail if the HDF5 box is empty
		else if (useHDF5.isSelected() && hdf5ComboBox.getText().length() == 0)
		{
			TaskDialog.warning(
				RB.getString("gui.dialog.NBDataImportPanel.warn2"),
				RB.getString("gui.text.ok"));

			return false;
		}

		Prefs.guiMapList = mapComboBox.getHistory();
		Prefs.guiGenoList = genoComboBox.getHistory();
		Prefs.guiHDF5List = hdf5ComboBox.getHistory();

		return true;
	}

	File getMapFile()
	{
		return new File(mapComboBox.getText());
	}

	File getGenotypeFile()
	{
		return new File(genoComboBox.getText());
	}

	File getHDF5File()
	{
		return new File(hdf5ComboBox.getText());
	}


	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        filePanel = new javax.swing.JPanel();
        useText = new javax.swing.JRadioButton();
        mapLabel = new javax.swing.JLabel();
        mapComboBox = new scri.commons.gui.matisse.HistoryComboBox();
        mapButton = new javax.swing.JButton();
        genoLabel = new javax.swing.JLabel();
        genoComboBox = new scri.commons.gui.matisse.HistoryComboBox();
        genoButton = new javax.swing.JButton();
        useHDF5 = new javax.swing.JRadioButton();
        hdf5ComboBox = new scri.commons.gui.matisse.HistoryComboBox();
        hdf5Label = new javax.swing.JLabel();
        hdf5Button = new javax.swing.JButton();
        tabLabel = new javax.swing.JLabel();
        optionsPanel = new javax.swing.JPanel();
        optionsButton = new javax.swing.JButton();
        optionsLabel = new javax.swing.JLabel();

        filePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Data files to import:"));

        useText.setText("Import from text files");

        mapLabel.setLabelFor(mapComboBox);
        mapLabel.setText("Map file:");

        mapButton.setText("Browse...");

        genoLabel.setLabelFor(genoComboBox);
        genoLabel.setText("Genotype file:");

        genoButton.setText("Browse...");

        useHDF5.setText("Import from an HDF5 file");

        hdf5Label.setLabelFor(hdf5ComboBox);
        hdf5Label.setText("HDF5 file:");

        hdf5Button.setText("Browse...");

        javax.swing.GroupLayout filePanelLayout = new javax.swing.GroupLayout(filePanel);
        filePanel.setLayout(filePanelLayout);
        filePanelLayout.setHorizontalGroup(
            filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(filePanelLayout.createSequentialGroup()
                        .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(useHDF5)
                            .addComponent(useText))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(filePanelLayout.createSequentialGroup()
                        .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, filePanelLayout.createSequentialGroup()
                                .addComponent(hdf5Label)
                                .addGap(25, 25, 25)
                                .addComponent(hdf5ComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, filePanelLayout.createSequentialGroup()
                                .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(genoLabel)
                                    .addComponent(mapLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(mapComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(genoComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(genoButton, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(mapButton, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(hdf5Button, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        filePanelLayout.setVerticalGroup(
            filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, filePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(useText)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(mapLabel)
                    .addComponent(mapButton)
                    .addComponent(mapComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(genoLabel)
                    .addComponent(genoButton)
                    .addComponent(genoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(useHDF5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hdf5Label)
                    .addComponent(hdf5ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hdf5Button))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabLabel.setText("Use this tab to import map and genotype data into a new or existing Flapjack project.");

        optionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Advanced options:"));

        optionsButton.setText("Advanced options...");

        optionsLabel.setText("Edit the advanced options to adjust how Flapjack will process the files being imported.");

        javax.swing.GroupLayout optionsPanelLayout = new javax.swing.GroupLayout(optionsPanel);
        optionsPanel.setLayout(optionsPanelLayout);
        optionsPanelLayout.setHorizontalGroup(
            optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(optionsLabel)
                    .addComponent(optionsButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        optionsPanelLayout.setVerticalGroup(
            optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(optionsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(optionsButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(optionsPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabLabel, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabLabel)
                .addGap(18, 18, 18)
                .addComponent(filePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(optionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel filePanel;
    private javax.swing.JButton genoButton;
    scri.commons.gui.matisse.HistoryComboBox genoComboBox;
    private javax.swing.JLabel genoLabel;
    private javax.swing.JButton hdf5Button;
    scri.commons.gui.matisse.HistoryComboBox hdf5ComboBox;
    private javax.swing.JLabel hdf5Label;
    private javax.swing.JButton mapButton;
    scri.commons.gui.matisse.HistoryComboBox mapComboBox;
    private javax.swing.JLabel mapLabel;
    private javax.swing.JButton optionsButton;
    private javax.swing.JLabel optionsLabel;
    private javax.swing.JPanel optionsPanel;
    private javax.swing.JLabel tabLabel;
    private javax.swing.JRadioButton useHDF5;
    private javax.swing.JRadioButton useText;
    // End of variables declaration//GEN-END:variables

}