package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;

import scri.commons.gui.*;

public class ImportOptionsDialog extends JDialog implements ActionListener
{
	private JButton bOK, bCancel;
	private boolean isOK = false;

	private NBImportOptionsPanel nbPanel;

	public ImportOptionsDialog()
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.ImportOptionsDialog.title"),
			true
		);

		nbPanel = new NBImportOptionsPanel(new DblClickListener());

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
		bOK = SwingUtils.getButton(RB.getString("gui.text.ok"));
		bOK.addActionListener(this);
		bCancel = SwingUtils.getButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);

		JPanel p1 = new JPanel(new GridLayout(1, 2, 5, 5));
		p1.add(bOK);
		p1.add(bCancel);

		JPanel p2 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		p2.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		p2.add(p1);

		return p2;
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

	private class DblClickListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount() != 2)
				return;

			bOK.doClick();
		}
	}
}