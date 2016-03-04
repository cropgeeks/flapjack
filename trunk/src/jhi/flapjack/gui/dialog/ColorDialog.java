// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jhi.flapjack.gui.*;
import jhi.flapjack.gui.visualization.*;
import jhi.flapjack.gui.visualization.colors.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class ColorDialog extends JDialog implements ActionListener
{
	private JButton bClose;
	private JButton bDefaults;
	private JButton bApply;
	private JButton bHelp;

	private WinMain winMain;
	private ColorPanelNB nbPanel;

	public ColorDialog(WinMain winMain, GenotypePanel gPanel)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.ColorDialog.title"),
			true
		);

		this.winMain = winMain;

		add(new TitlePanel2(), BorderLayout.NORTH);
		add(nbPanel = new ColorPanelNB(gPanel));
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
		bDefaults = new JButton(RB.getString("gui.dialog.ColorDialog.bDefaults"));
		RB.setMnemonic(bDefaults, "gui.dialog.ColorDialog.bDefaults");
		bDefaults.addActionListener(this);
		bApply = new JButton(RB.getString("gui.dialog.ColorDialog.bApply"));
		RB.setMnemonic(bApply, "gui.dialog.ColorDialog.bApply");
		bApply.addActionListener(this);
		bHelp = new JButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "_-_Customize_Colours");

		JPanel p1 = new DialogPanel();
		p1.add(bApply);
		p1.add(bDefaults);
		p1.add(bClose);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bClose)
			setVisible(false);

		else if (e.getSource() == bApply)
		{
			ColorScheme cs = (ColorScheme) nbPanel.schemeCombo.getSelectedItem();
			winMain.mViz.vizColor(cs.getModel());

			// Sillyness...if a shift/click was detected
			if (((e.getModifiers() & ActionEvent.SHIFT_MASK) != 0) && cs instanceof RandomColorScheme)
				new RandomDemo().start();
		}

		else if (e.getSource() == bDefaults)
		{
			String msg = RB.getString("gui.dialog.ColorDialog.defaultsMsg");

			String[] options = new String[] {
				RB.getString("gui.dialog.ColorDialog.bDefaults"),
				RB.getString("gui.text.cancel") };

			if (TaskDialog.show(msg, TaskDialog.WAR, 1, options) == 0)
				nbPanel.resetColors();
		}
	}

	private class RandomDemo extends Thread
	{
		// Sillyness - on a Shift/Click of the button if the random scheme has
		// been selected, we loop 50 times, selecting a new scheme each time
		public void run()
		{
			Runnable r = () -> { winMain.mViz.vizColor(ColorScheme.RANDOM); };

			for (int i = 0; i < 50; i++)
			{
				try
				{
					SwingUtilities.invokeAndWait(r);
					Thread.sleep(100);
				}
				catch (Exception e) {}
			}
		}
	}
}