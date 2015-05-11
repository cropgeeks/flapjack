package flapjack.gui.dialog.importer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;
import flapjack.io.brapi.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class BrapiImportDialog extends JDialog implements ActionListener
{
	private JButton bNext, bBack, bCancel, bHelp;
	private boolean isOK = false;

	private BrapiDataPanelNB dataPanel;
	private BrapiMapsPanelNB mapsPanel;

	private CardLayout cards = new CardLayout();
	Panel panel = new Panel();
	private int screen = 0;

	private BrapiRequest request = new BrapiRequest();

	public BrapiImportDialog()
	{
		super(
			Flapjack.winMain,
			"BRAPI Data Import Wizard",
			true
		);

		dataPanel = new BrapiDataPanelNB();
		mapsPanel = new BrapiMapsPanelNB(request);

		panel.setLayout(cards);
		panel.add(dataPanel, "data");
		panel.add(mapsPanel, "maps");
//		cards.first(panel);

		add(panel);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bNext);
		SwingUtils.addCloseHandler(this, bCancel);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bNext = new JButton("Next >");
		bNext.addActionListener(this);
		bBack = new JButton("< Back");
		bBack.setVisible(false);
		bBack.addActionListener(this);
		bCancel = new JButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);
		bHelp = new JButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "_-_Import_Data");

		JPanel p1 = new DialogPanel();
		p1.add(bBack);
		p1.add(bNext);
		p1.add(bCancel);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bNext)
		{
			if (screen == 0)
			{
				mapsPanel.refreshMaps();
				cards.next(panel);
				bBack.setVisible(true);

				screen = 1;
			}
			else if (screen == 1)
			{
				isOK = true;
				setVisible(false);
			}
		}

		else if (e.getSource() == bBack)
		{
			if (screen == 1)
			{
				bBack.setVisible(false);
				cards.previous(panel);

				screen = 0;
			}
		}

		if (e.getSource() == bCancel)
			setVisible(false);
	}

	public boolean isOK()
		{ return isOK; }

	public BrapiRequest getBrapiRequest()
		{ return request; }
}