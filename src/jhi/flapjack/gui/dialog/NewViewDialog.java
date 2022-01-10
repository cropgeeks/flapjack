// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class NewViewDialog extends JDialog implements ActionListener
{
	private DataSet dataSet;

	private JButton bOK, bCancel, bHelp;
	private boolean isOK = false;

	private NewViewPanelNB nbPanel;

	public NewViewDialog(DataSet dataSet, GTViewSet currentViewSet)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.NewViewDialog.title"),
			true
		);

		this.dataSet = dataSet;
		nbPanel = new NewViewPanelNB(dataSet, currentViewSet);

		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		FlapjackUtils.initDialog(this, bOK, bCancel, true, getContentPane());
	}

	private JPanel createButtons()
	{
		bOK = new JButton(RB.getString("gui.dialog.NewViewDialog.createButton"));
		bOK.addActionListener(this);
		bCancel = new JButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);
		bHelp = new JButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "create_new_view.html");

		JPanel p1 = new DialogPanel();
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
			viewSet = cloneFrom.createClone(name, false);
		}

		return viewSet;
	}
}