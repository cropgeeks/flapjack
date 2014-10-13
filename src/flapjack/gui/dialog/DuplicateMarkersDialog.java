// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import flapjack.gui.*;

import scri.commons.gui.*;

public class DuplicateMarkersDialog extends JDialog implements ActionListener
{
	private JButton bClose;
	private JButton bClipboard;
	private JButton bHelp;

	private DuplicateMarkersPanelNB nbPanel;
	private LinkedList<String> duplicates;

	public DuplicateMarkersDialog(LinkedList<String> duplicates)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.DuplicateMarkersDialog.title"),
			true
		);

		this.duplicates = duplicates;
		nbPanel = new DuplicateMarkersPanelNB(duplicates);

		add(new TitlePanel2(), BorderLayout.NORTH);
		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bClose);
		SwingUtils.addCloseHandler(this, bClose);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);

		bClose.requestFocusInWindow();
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bClose = SwingUtils.getButton(RB.getString("gui.text.close"));
		bClose.addActionListener(this);

		bClipboard = SwingUtils.getButton(RB.getString("gui.dialog.DuplicateMarkersDialog.clipboard"));
		bClipboard.addActionListener(this);
		RB.setMnemonic(bClipboard, "gui.dialog.DuplicateMarkersDialog.clipboard");

		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "_-_Duplicate_Markers");

		JPanel p1 = FlapjackUtils.getButtonPanel();
		p1.add(bClipboard);
		p1.add(bClose);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bClose)
		{
			Prefs.warnDuplicateMarkers = !nbPanel.isCheckboxSelected();
			setVisible(false);
		}

		else if (e.getSource() == bClipboard)
		{
			StringBuilder sb = new StringBuilder();

			for (String str: duplicates)
				sb.append(str + System.getProperty("line.separator"));

			StringSelection selection = new StringSelection(sb.toString());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
				selection, null);
		}
	}
}