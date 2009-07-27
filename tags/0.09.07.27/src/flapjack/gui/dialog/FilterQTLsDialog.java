package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

public class FilterQTLsDialog extends JDialog implements ActionListener
{
	private JButton bFilter, bHelp;

	private NBFilterQTLsPanel nbPanel;

	public FilterQTLsDialog(DataSet dataSet)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.FilterQTLsDialog.title"),
			false
		);

		nbPanel = new NBFilterQTLsPanel(dataSet);

		add(new TitlePanel2(), BorderLayout.NORTH);
		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		addComponentListener(new ComponentAdapter()
		{
			public void componentMoved(ComponentEvent e)
			{
				Prefs.guiFilterQTLDialogX = getLocation().x;
				Prefs.guiFilterQTLDialogY = getLocation().y;
			}
		});

		getRootPane().setDefaultButton(bFilter);
		SwingUtils.addCloseHandler(this, bFilter);

		pack();
		setResizable(false);

		// Position on screen...
		if (Prefs.guiFilterQTLDialogX == -9999 && Prefs.guiFilterQTLDialogY == -9999)
			setLocationRelativeTo(Flapjack.winMain);
		else
			setLocation(Prefs.guiFilterQTLDialogX, Prefs.guiFilterQTLDialogY);

		setVisible(true);
	}

	private JPanel createButtons()
	{
		bFilter = SwingUtils.getButton(RB.getString("gui.dialog.FilterQTLsDialog.bFilter"));
		RB.setText(bFilter, "gui.dialog.FilterQTLsDialog.bFilter");
		bFilter.addActionListener(this);
		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "gui.dialog.FilterQTLsDialog");

		JPanel p1 = FlapjackUtils.getButtonPanel();
		p1.add(bFilter);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bFilter)
			nbPanel.filterQTLs();
	}
}