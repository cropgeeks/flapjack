package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import scri.commons.gui.*;

import flapjack.gui.*;
import flapjack.gui.visualization.*;



public class SelectGraphDialog extends JDialog implements ActionListener
{
	private JButton bClose;
	private NBSelectGraphPanel panel;
	private GenotypePanel gPanel;

	public SelectGraphDialog(GenotypePanel gPanel)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.SelectGraphDialog.title"),
			true);

		this.gPanel = gPanel;

		add(new TitlePanel2(), BorderLayout.NORTH);
		add(panel = new NBSelectGraphPanel(gPanel));
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bClose);
		SwingUtils.addCloseHandler(this, bClose);

		panel.graphSelectCombo.addActionListener(this);
		panel.graphTypeCombo.addActionListener(this);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bClose = SwingUtils.getButton(RB.getString("gui.text.close"));
		bClose.addActionListener(this);

		JPanel p1 = FlapjackUtils.getButtonPanel();
		p1.add(bClose);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == panel.graphSelectCombo || e.getSource() == panel.graphTypeCombo)
		{
			gPanel.getView().setGraphIndex(panel.graphSelectCombo.getSelectedIndex());
			Prefs.guiGraphStyle = panel.graphTypeCombo.getSelectedIndex();
			gPanel.refreshView();
		}

		else if (e.getSource() == bClose)
		{
			setVisible(false);
		}

	}
}
