package flapjack.gui.dialog;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;

import scri.commons.gui.*;

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
	}

	public void uncaughtException(Thread t, Throwable e)
	{
		add(new NBErrorPanel(t, e));
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bOK);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bOK = SwingUtils.getButton(RB.getString("gui.dialog.ErrorDialog.bOK"));
		bOK.addActionListener(this);
		bClipboard = SwingUtils.getButton(RB.getString("gui.dialog.ErrorDialog.bClipboard"));
		RB.setText(bClipboard, "gui.dialog.ErrorDialog.bClipboard");
		bClipboard.addActionListener(this);

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		p1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 5));
		p1.add(bClipboard);
		p1.add(bOK);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOK)
			System.exit(0);

		else if (e.getSource() == bClipboard)
		{
			StringSelection selection = new StringSelection(
				NBErrorPanel.textArea.getText());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
				selection, null);
		}
	}
}