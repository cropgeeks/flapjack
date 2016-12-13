// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.importer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jhi.flapjack.gui.*;
import jhi.flapjack.io.brapi.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class BrapiImportDialog extends JDialog implements ActionListener
{
	private JButton bNext, bBack, bCancel, bHelp;
	private boolean isOK = false;

	IBrapiWizard dataPanel;
	IBrapiWizard passPanel;
	IBrapiWizard mapsPanel;
	IBrapiWizard studiesPanel;

	private IBrapiWizard currentPanel;

	private CardLayout cards = new CardLayout();
	private JPanel panel = new JPanel();
	private int screen = 0;

	private BrapiClient client = new BrapiClient();

	public BrapiImportDialog()
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.importer.BrapiImportDialog.title"),
			true
		);

		dataPanel = new BrapiDataPanelNB(client, this);
		passPanel = new BrapiPassPanelNB(client, this);
		mapsPanel = new BrapiMapsPanelNB(client, this);
		studiesPanel = new BrapiStudiesPanelNB(client, this);

		panel.setLayout(cards);
		panel.add(dataPanel.getPanel(), dataPanel.getCardName());
		panel.add(passPanel.getPanel(), passPanel.getCardName());
		panel.add(mapsPanel.getPanel(), mapsPanel.getCardName());
		panel.add(studiesPanel.getPanel(), studiesPanel.getCardName());
//		cards.first(panel);

		add(panel);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bNext);
		SwingUtils.addCloseHandler(this, bCancel);

		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e)
			{
				setScreen(dataPanel);
			}
		});

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bNext = new JButton("Next >");
		bNext.setEnabled(false);
		bNext.addActionListener(this);
		bBack = new JButton("< Back");
		bBack.setEnabled(false);
		bBack.addActionListener(this);
		bCancel = new JButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);
		bHelp = new JButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "_-_Import_Data");

		JPanel p1 = new DialogPanel();
		p1.add(bBack);
		p1.add(bNext);
		p1.add(bCancel);
		p1.add(bHelp);

		return p1;
	}

	void enableNext(boolean enabled)
		{ bNext.setEnabled(enabled); }

	void enableBack(boolean enabled)
		{ bBack.setEnabled(enabled); }

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bNext)
			currentPanel.onNext();

		else if (e.getSource() == bBack)
			currentPanel.onBack();

		else if (e.getSource() == bCancel)
			setVisible(false);
	}

	void setScreen(IBrapiWizard screen)
	{
		cards.show(panel, screen.getCardName());
		screen.onShow();

		currentPanel = screen;
	}

//	private void setScreen(int newScreen)
//	{
//		// Displaying the DataSource screen
//		if (newScreen == 0)
//		{
//			enableBack(false);
//			enableNext(false);
//
//			// Grab the data sources
//			dataPanel.refreshData();
//
//			cards.show(panel, "data");
//			screen = 0;
//		}
//
//		// Display the Authentication screen?
//		else if (newScreen == 1)
//		{
//			enableBack(true);
//
//			// If BrAPI-> /calls doesn't validate, we can't proceed
//			if (passPanel.validateCalls() == false)
//			{
//				enableBack(false);
//				enableNext(true);
//
//				cards.show(panel, "data");
//				screen = 0;
//			}
//			else if (client.hasToken() == false)
//			{
//				//// ????????????
//			}
//			else
//			{
//				// Update the label showing the data source information
//				passPanel.updateLabels();
//
//				cards.show(panel, "pass");
//				screen = 1;
//			}
//		}
//
//		// Displaying the SelectStudies screen
//		else if (newScreen == 2)
//		{
//			enableBack(true);
//
//			// Save details entered on the previous screen (if any)
//			passPanel.saveOptions();
//
//			// Download the list of studies and their metadata
//			if (studiesPanel.refreshStudies())
//			{
//				cards.show(panel, "studies");
//				screen = 2;
//			}
//		}
//
//		// Displaying the SelectMaps screen
//		else if (newScreen == 3)
//		{
//			enableBack(true);
//
//			// Download the list of maps and their metadata
//			if (mapsPanel.refreshMaps())
//			{
//				cards.show(panel, "maps");
//				screen = 3;
//			}
//		}
//
//		// There is no screen '4' - close and continue
//		else if (newScreen == 4)
//		{
//			isOK = true;
//			setVisible(false);
//		}
//	}

	void wizardCompleted()
	{
		isOK = true;
		setVisible(false);
	}

	public boolean isOK()
		{ return isOK; }

	public BrapiClient getBrapiClient()
		{ return client; }
}