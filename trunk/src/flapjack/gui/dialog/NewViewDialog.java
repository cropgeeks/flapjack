// Copyright 2007-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

public class NewViewDialog extends JDialog implements ActionListener
{
	private DataSet dataSet;

	private JButton bOK, bCancel, bHelp;
	private boolean isOK = false;

	private NBNewViewPanel nbPanel;

	public NewViewDialog(DataSet dataSet, GTViewSet currentViewSet)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.NewViewDialog.title"),
			true
		);

		this.dataSet = dataSet;
		nbPanel = new NBNewViewPanel(dataSet, currentViewSet);

		add(new TitlePanel2(), BorderLayout.NORTH);
		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bOK);
		SwingUtils.addCloseHandler(this, bCancel);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bOK = SwingUtils.getButton(RB.getString("gui.dialog.NewViewDialog.createButton"));
		bOK.addActionListener(this);
		bCancel = SwingUtils.getButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);
		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "gui.dialog.NewViewDialog");

		JPanel p1 = FlapjackUtils.getButtonPanel();
		p1.add(bOK);
		p1.add(bCancel);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOK)
		{
			nbPanel.isOK();
			isOK = true;
		}

		setVisible(false);
	}

	public boolean isOK()
		{ return isOK; }

	public GTViewSet getNewViewSet()
	{
		GTViewSet viewSet = null;

		String name = nbPanel.nameText.getText();

		// Create a standard new view...
		if (nbPanel.createNewView())
		{
			viewSet = new GTViewSet(dataSet, name);
		}
		// Or create one that is cloned from another view
		else
		{
			GTViewSet cloneFrom = (GTViewSet) nbPanel.cloneCombo.getSelectedItem();
			viewSet = cloneFrom.createClone(name, Prefs.guiCloneHidden);
		}

		return viewSet;
	}
}