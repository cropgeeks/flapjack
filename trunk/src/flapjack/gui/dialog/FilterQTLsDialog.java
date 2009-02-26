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
	private JButton bFilter, bCancel, bHelp;
	private boolean isOK = false;

	private NBFilterQTLsPanel nbPanel;

	public FilterQTLsDialog(DataSet dataSet)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.FilterQTLsDialog.title"),
			true
		);

		nbPanel = new NBFilterQTLsPanel(dataSet);

		add(new TitlePanel2(), BorderLayout.NORTH);
		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bFilter);
		SwingUtils.addCloseHandler(this, bCancel);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bFilter = SwingUtils.getButton(RB.getString("gui.dialog.FilterQTLsDialog.bFilter"));
		bFilter.addActionListener(this);
		bCancel = SwingUtils.getButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);
		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
//		FlapjackUtils.setHelp(bHelp, "gui.dialog.DataImportDialog");

		JPanel p1 = FlapjackUtils.getButtonPanel();
		p1.add(bFilter);
		p1.add(bCancel);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bFilter)
		{
			nbPanel.filterQTLs();

			isOK = true;
			setVisible(false);
		}

		else if (e.getSource() == bCancel)
			setVisible(false);
	}

	public boolean isOK() {
		return isOK;
	}
}