// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.importer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import jhi.flapjack.gui.*;
import jhi.flapjack.io.brapi.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class BrapiImportDialog extends JDialog implements ActionListener
{
	private JButton bNext, bBack, bCancel, bHelp;
	private boolean isOK = false;

	private IBrapiWizard dataPanel;
	private IBrapiWizard passPanel;
	private IBrapiWizard mapsPanel;
	private IBrapiWizard studiesPanel;
	private IBrapiWizard matricesPanel;

	private IBrapiWizard currentPanel;
	private Stack<IBrapiWizard> stack = new Stack<>();

	private CardLayout cards = new CardLayout();
	private JPanel panel = new JPanel();

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
		matricesPanel = new BrapiMatricesPanelNB(client, this);

		panel.setLayout(cards);
		panel.add(dataPanel.getPanel(), dataPanel.getCardName());
		panel.add(passPanel.getPanel(), passPanel.getCardName());
		panel.add(mapsPanel.getPanel(), mapsPanel.getCardName());
		panel.add(studiesPanel.getPanel(), studiesPanel.getCardName());
		panel.add(matricesPanel.getPanel(), matricesPanel.getCardName());

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
		FlapjackUtils.setHelp(bHelp, "import_data.html");

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
		{
//			currentPanel.onBack();
			stack.pop();
			setScreen(stack.pop());
			bBack.requestFocusInWindow();
		}

		else if (e.getSource() == bCancel)
			setVisible(false);
	}

	void setScreen(IBrapiWizard screen)
	{
		cards.show(panel, screen.getCardName());
		stack.push(screen);
		screen.onShow();

		currentPanel = screen;
	}

	void wizardCompleted()
	{
		isOK = true;
		setVisible(false);
	}

	public boolean isOK()
		{ return isOK; }

	public BrapiClient getBrapiClient()
		{ return client; }

	IBrapiWizard getDataPanel()
		{ return dataPanel; }

	IBrapiWizard getPassPanel()
		{ return passPanel; }

	IBrapiWizard getMapsPanel()
		{ return mapsPanel; }

	IBrapiWizard getStudiesPanel()
		{ return studiesPanel; }

	IBrapiWizard getMatricesPanel()
		{ return matricesPanel; }

	public JButton getBNext()
		{ return bNext; }
}