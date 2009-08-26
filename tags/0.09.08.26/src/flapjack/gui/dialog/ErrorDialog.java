// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;

import scri.commons.gui.*;

/**
 * An error dialog that pops up on Thread.UncaughtExceptionHandler events.
 * (Hopefully, testing would eliminate all cases where this might happen, but
 * this is the real world...)
 */
public class ErrorDialog extends JDialog
	implements ActionListener, Thread.UncaughtExceptionHandler
{
	private JButton bOK, bClipboard;

	public ErrorDialog()
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.ErrorDialog.title"),
			true
		);

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setResizable(false);
	}

	public void uncaughtException(Thread thread, Throwable throwable)
	{
		final Thread t = thread;
		final Throwable e = throwable;

		// We open the dialog using SwingUtilities because the uncaughtException
		// may have happened in a non EDT thread
		Runnable r = new Runnable() {
			public void run()
			{
				add(new TitlePanel2(), BorderLayout.NORTH);
				add(new NBErrorPanel(t, e));
				add(createButtons(), BorderLayout.SOUTH);
				getRootPane().setDefaultButton(bOK);
				pack();

				setLocationRelativeTo(Flapjack.winMain);
				setVisible(true);
			}
		};

		SwingUtilities.invokeLater(r);
	}

	private JPanel createButtons()
	{
		bOK = SwingUtils.getButton(RB.getString("gui.dialog.ErrorDialog.bOK"));
		bOK.addActionListener(this);
		bClipboard = SwingUtils.getButton(RB.getString("gui.dialog.ErrorDialog.bClipboard"));
		RB.setText(bClipboard, "gui.dialog.ErrorDialog.bClipboard");
		bClipboard.addActionListener(this);

		JPanel p1 = FlapjackUtils.getButtonPanel();
		p1.add(bClipboard);
		p1.add(bOK);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		// Full exit/shutdown
		if (e.getSource() == bOK)
			System.exit(0);

		// Copy the contends of the text area onto the system clipboard
		else if (e.getSource() == bClipboard)
		{
			StringSelection selection = new StringSelection(
				NBErrorPanel.textArea.getText());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
				selection, null);
		}
	}
}