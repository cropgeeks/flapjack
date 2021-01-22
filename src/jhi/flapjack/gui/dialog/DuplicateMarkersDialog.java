// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import jhi.flapjack.gui.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

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

		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		FlapjackUtils.initDialog(this, bClose, bClose, true, getContentPane());
	}

	private JPanel createButtons()
	{
		bClose = new JButton(RB.getString("gui.text.close"));
		bClose.addActionListener(this);

		bClipboard = new JButton(RB.getString("gui.dialog.DuplicateMarkersDialog.clipboard"));
		bClipboard.addActionListener(this);
		RB.setMnemonic(bClipboard, "gui.dialog.DuplicateMarkersDialog.clipboard");

		bHelp = new JButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "duplicate_markers.html");

		JPanel p1 = new DialogPanel();
		p1.add(bClose);
		p1.add(bClipboard);
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