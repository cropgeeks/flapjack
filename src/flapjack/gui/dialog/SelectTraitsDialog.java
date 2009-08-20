package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

public class SelectTraitsDialog extends JDialog implements ActionListener
{
	private JButton bOK, bCancel, bHelp;
	private NBSelectTraitsPanel nbPanel;

	public SelectTraitsDialog(GTViewSet viewSet)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.SelectTraitsDialog.title"),
			true
		);

		nbPanel = new NBSelectTraitsPanel(viewSet);

		add(new TitlePanel2(), BorderLayout.NORTH);
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
		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "gui.dialog.SelectTraitsDialog");

		JPanel p1 = FlapjackUtils.getButtonPanel();
		p1.add(bOK);
		p1.add(bCancel);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOK)
		{
			nbPanel.isOK();
			Actions.projectModified();
		}

		setVisible(false);
	}
}