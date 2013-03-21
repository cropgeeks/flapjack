// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.dialog.pedigrees;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;

import scri.commons.gui.*;

public class PedigreeDialog extends JFrame
{
	private JToolBar toolbar = new JToolBar();
	private JScrollPane sp;

	public PedigreeDialog(BufferedImage image)
	{
		toolbar.add(new JButton("TODO: Add some controls"));
		toolbar.setFloatable(false);

//		SwingUtils.addCloseHandler(this, bCancel);

		PedigreePanel panel = new PedigreePanel(image);
		sp = new JScrollPane(panel);
//		sp = new JScrollPane();
		sp.getHorizontalScrollBar().setBlockIncrement(25);
		sp.getVerticalScrollBar().setBlockIncrement(25);
		panel.setScrollPane(sp);

		add(toolbar, BorderLayout.NORTH);
		add(sp);
//		add(panel);

		setSize(500, 500);
		setIconImage(Icons.getIcon("FLAPJACK").getImage());
		setTitle(RB.getString("gui.dialog.pedigrees.PedigreeDialog.title"));
		setLocationRelativeTo(Flapjack.winMain);
		setVisible(true);
	}
}