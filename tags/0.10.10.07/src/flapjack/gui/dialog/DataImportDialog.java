// Copyright 2007-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.gui.*;
import flapjack.io.*;

import scri.commons.gui.*;

public class DataImportDialog extends JDialog implements ActionListener, ChangeListener
{
	private JButton bImport, bCancel, bHelp;
	private boolean isOK = false;

	private JTabbedPane tabs;
	private boolean secondaryOptions;

	private NBImportDataPanel dataPanel;
	private NBImportTraitsPanel traitsPanel;
	private NBImportFeaturesPanel featuresPanel;
	private NBImportSamplePanel samplePanel;

	public DataImportDialog(int tabIndex, boolean secondaryOptions)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.DataImportDialog.title"),
			true
		);

		this.secondaryOptions = secondaryOptions;

		add(createButtons(), BorderLayout.SOUTH);

		// Create and add the panels/tabs
		tabs = new JTabbedPane();

		dataPanel = new NBImportDataPanel(this);
		tabs.addTab(RB.getString("gui.dialog.DataImportDialog.dataTab"),
			Icons.getIcon("DATATAB"), dataPanel);

		traitsPanel = new NBImportTraitsPanel(secondaryOptions);
		tabs.addTab(RB.getString("gui.dialog.DataImportDialog.phenotypesTab"),
			Icons.getIcon("PHENOTYPETAB"), traitsPanel);

		featuresPanel = new NBImportFeaturesPanel(secondaryOptions);
		tabs.addTab(RB.getString("gui.dialog.DataImportDialog.featuresTab"),
			Icons.getIcon("QTLTAB"), featuresPanel);

		samplePanel = new NBImportSamplePanel(bImport);
		tabs.addTab(RB.getString("gui.dialog.DataImportDialog.sampleTab"),
			Icons.getIcon("HELPTAB"), samplePanel);

		tabs.addChangeListener(this);
		tabs.setSelectedIndex(tabIndex);
		add(tabs);

		getRootPane().setDefaultButton(bImport);
		SwingUtils.addCloseHandler(this, bCancel);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bImport = SwingUtils.getButton(RB.getString("gui.dialog.DataImportDialog.import"));
		bImport.addActionListener(this);
		bCancel = SwingUtils.getButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);
		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "gui.dialog.DataImportDialog");

		JPanel p1 = FlapjackUtils.getButtonPanel();
		p1.add(bImport);
		p1.add(bCancel);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bImport)
		{
			switch (tabs.getSelectedIndex())
			{
				case 0: if (dataPanel.isOK() == false)
							return;
				break;

				case 1: if (traitsPanel.isOK() == false)
							return;
				break;

				case 2: if (featuresPanel.isOK() == false)
							return;
				break;
			}

			isOK = true;
			setVisible(false);
		}

		else if (e.getSource() == bCancel)
			setVisible(false);
	}

	public void stateChanged(ChangeEvent e)
	{
		// Change the Import button's text based on the tab we're viewing
		switch (tabs.getSelectedIndex())
		{
			case 0 :
				RB.setText(bImport, "gui.dialog.DataImportDialog.import");
				bImport.setEnabled(true);
				break;

			case 1 :
				RB.setText(bImport, "gui.dialog.DataImportDialog.importPhenotypes");
				bImport.setEnabled(secondaryOptions);
				break;

			case 2 :
				RB.setText(bImport, "gui.dialog.DataImportDialog.importFeatures");
				bImport.setEnabled(secondaryOptions);
				break;

			case 3 :
				RB.setText(bImport, "gui.dialog.DataImportDialog.importSample");
				bImport.setEnabled(samplePanel.isOK());
				break;
		}
	}

	public boolean isOK()
		{ return isOK; }

	public int getSelectedAction()
		{ return tabs.getSelectedIndex(); }


	public File getMapFile()
		{ return dataPanel.getMapFile(); }

	public File getGenotypeFile()
		{ return dataPanel.getGenotypeFile(); }

	public File getTraitsFile()
		{ return traitsPanel.getFile(); }

	public File getFeaturesFile()
		{ return featuresPanel.getFile(); }

	public FlapjackFile getSampleProject()
		{ return samplePanel.getProject(); }
}