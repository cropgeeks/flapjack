package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;

import scri.commons.gui.*;

public class SCRIDialog extends JDialog implements ActionListener
{
	private JButton bOK, bCancel;

	public SCRIDialog()
	{
		super(Flapjack.winMain, "SCRI Flapjack User Agreement", true);

		add(new TitlePanel2(), BorderLayout.NORTH);
		add(new NBSCRIPanel());
		add(createButtons(), BorderLayout.SOUTH);

		pack();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);

		if (Prefs.querySCRI && SystemUtils.isSCRIUser())
			setVisible(true);
		else
			Prefs.querySCRI = false;
	}

	private JPanel createButtons()
	{
		bOK = SwingUtils.getButton("I agree");
		bOK.addActionListener(this);
		bCancel = SwingUtils.getButton("Cancel");
		bCancel.addActionListener(this);

		JPanel p1 = FlapjackUtils.getButtonPanel();
		p1.add(bOK);
		p1.add(bCancel);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOK)
		{
			setVisible(false);
			Prefs.querySCRI = false;
		}

		else
		{
			String message = "Declining this agreement will mean you will not "
				+ "be able to use Flapjack. Are you sure?";
			String[] options = { "Yes", "No" };

			int response = TaskDialog.show(message, TaskDialog.WAR, 1, options);
			if (response == 0)
				System.exit(0);
		}
	}
}