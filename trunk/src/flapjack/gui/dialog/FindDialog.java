package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;
import flapjack.gui.visualization.*;

import scri.commons.gui.*;

public class FindDialog extends JDialog implements ActionListener
{
	private JButton bFindNext, bFindPrev, bClose;

	private NBFindPanel nbPanel = new NBFindPanel();
	private GenotypePanel gPanel;

	public FindDialog(GenotypePanel gPanel)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.FindDialog.title"),
			false
		);

		this.gPanel = gPanel;

		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);
		addListeners();

		getRootPane().setDefaultButton(bFindNext);
		SwingUtils.addCloseHandler(this, bClose);

		pack();
		setResizable(false);

		// Work out the current screen's width and height
		int scrnW = SwingUtils.getVirtualScreenDimension().width;
		int scrnH = SwingUtils.getVirtualScreenDimension().height;

		// Determine where on screen to display
		if (Prefs.guiFindDialogShown == false ||
			Prefs.guiFindDialogX > (scrnW-50) || Prefs.guiFindDialogY > (scrnH-50))
			setLocationRelativeTo(Flapjack.winMain);
		else
			setLocation(Prefs.guiFindDialogX, Prefs.guiFindDialogY);
	}

	private void addListeners()
	{
		addComponentListener(new ComponentAdapter()
		{
			public void componentMoved(ComponentEvent e)
			{
				Prefs.guiFindDialogX = getLocation().x;
				Prefs.guiFindDialogY = getLocation().y;
			}
		});
	}

	private JPanel createButtons()
	{
		bFindNext = SwingUtils.getButton(RB.getString("gui.dialog.FindDialog.findNext"));
		bFindNext.addActionListener(this);
		bFindNext.setMnemonic(KeyEvent.VK_N);
		bFindNext.setDisplayedMnemonicIndex(5);
		bFindPrev = SwingUtils.getButton(RB.getString("gui.dialog.FindDialog.findPrev"));
		bFindPrev.addActionListener(this);
		bFindPrev.setMnemonic(KeyEvent.VK_P);
		bClose = SwingUtils.getButton(RB.getString("gui.text.close"));
		bClose.addActionListener(this);

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		p1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 5));
		p1.add(bFindNext);
		p1.add(bFindPrev);
		p1.add(bClose);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bFindNext)
		{
		}

		else if (e.getSource() == bClose)
		{
			nbPanel.isOK();
			setVisible(false);
		}
	}
}