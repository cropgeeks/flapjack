// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jhi.flapjack.gui.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class AboutDialog extends JDialog implements ActionListener
{
	private JButton bClose;

	private AboutPanelNB nbPanel;

	public AboutDialog()
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.AboutDialog.title"),
			true
		);

		nbPanel = new AboutPanelNB();

		AvatarPanel avatars = new AvatarPanel();
		AboutLicencePanelNB licencePanel = new AboutLicencePanelNB();
		AboutFundingPanelNB fundingPanel = new AboutFundingPanelNB();

		JTabbedPane tabs = new JTabbedPane();
		tabs.add(RB.getString("gui.dialog.AboutDialog.tab1"), nbPanel);
		tabs.add(RB.getString("gui.dialog.AboutDialog.tab2"), licencePanel);
		tabs.add(RB.getString("gui.dialog.AboutDialog.tab3"), fundingPanel);
		tabs.add(RB.format("gui.dialog.AboutDialog.tab4", "\u0026"), avatars);

		add(tabs);
		add(createButtons(), BorderLayout.SOUTH);

		FlapjackUtils.initDialog(this, bClose, bClose, true, getContentPane());
	}

	private JPanel createButtons()
	{
		bClose = new JButton(RB.getString("gui.text.close"));
		bClose.addActionListener(this);

		JPanel p1 = new DialogPanel();
		p1.add(bClose);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		setVisible(false);
	}

	private class AvatarPanel extends JPanel
	{
		AvatarPanel()
		{
			setBackground(Color.white);
			add(new JLabel(Icons.getIcon("AVATARS")));
		}
	}
}