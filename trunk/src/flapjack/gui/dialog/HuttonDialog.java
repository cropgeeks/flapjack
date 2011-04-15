// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import javax.swing.*;

import flapjack.gui.*;
import scri.commons.gui.SwingUtils;

public class HuttonDialog extends JDialog
{
	private HuttonDialogNB nbPanel;

	public HuttonDialog()
	{
		super(
			Flapjack.winMain,
			"Important News",
			true
		);

		add(new HuttonDialogNB());
		SwingUtils.addCloseHandler(this, new JButton());

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}
}