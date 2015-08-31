// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;

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

		JTabbedPane tabs = new JTabbedPane();
		tabs.add(RB.getString("gui.dialog.AboutDialog.tab1"), nbPanel);
		tabs.add(RB.getString("gui.dialog.AboutDialog.tab2"), licencePanel);
		tabs.add(RB.format("gui.dialog.AboutDialog.tab3", "\u0026"), avatars);

		add(tabs);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bClose);
		SwingUtils.addCloseHandler(this, bClose);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
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

			addMouseMotionListener(new MouseMotionAdapter()
			{
				public void mouseMoved(MouseEvent e)
				{
					int x = e.getX();

					String tooltip = "<html>";

					if (x < 100)
						tooltip += "Iain Milne";
					else if (x < 193)
						tooltip += "Gordon Stephen";
					else if (x < 287)
						tooltip += "Paul Shaw";
					else if (x < 380)
						tooltip += "Sebastian Raubach";
					else if (x < 470)
						tooltip += "Micha Bayer";
					else if (x < 570)
						tooltip += "Linda Cardle";
					else
						tooltip += "David Marshall";

					setToolTipText(tooltip);
				}
			});
		}
	}
}