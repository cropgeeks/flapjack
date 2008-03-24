package flapjack.gui.dialog;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import flapjack.gui.*;

import scri.commons.gui.*;

public class DuplicateMarkersDialog extends JDialog implements ActionListener
{
	private JButton bClose;
	private JButton bClipboard;

	private NBDuplicateMarkersPanel nbPanel;
	private LinkedList<String> duplicates;

	public DuplicateMarkersDialog(LinkedList<String> duplicates)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.DuplicateMarkersDialog.title"),
			true
		);

		this.duplicates = duplicates;
		nbPanel = new NBDuplicateMarkersPanel(duplicates);

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

		JPanel p2 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		p2.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 5));
		p2.add(bClipboard);
		p2.add(bClose);

		return p2;
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
			StringBuffer sb = new StringBuffer();

			for (String str: duplicates)
				sb.append(str + System.getProperty("line.separator"));

			StringSelection selection = new StringSelection(sb.toString());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
				selection, null);
		}
	}
}