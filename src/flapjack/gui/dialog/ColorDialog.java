package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;
import flapjack.gui.visualization.*;
import flapjack.gui.visualization.colors.*;

import scri.commons.gui.*;

public class ColorDialog extends JDialog implements ActionListener
{
	private JButton bClose;
	private JButton bDefaults;

	private WinMain winMain;
	private NBColorPanel nbPanel;

	public ColorDialog(WinMain winMain, GenotypePanel gPanel)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.ColorDialog.title"),
			true
		);

		this.winMain = winMain;

		add(nbPanel = new NBColorPanel(this, gPanel));
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
		bClose = SwingUtils.getButton(RB.getString("gui.text.close"));
		bClose.addActionListener(this);
		bDefaults = SwingUtils.getButton(RB.getString("gui.dialog.ColorDialog.bDefaults"));
		RB.setMnemonic(bDefaults, "gui.dialog.ColorDialog.bDefaults");
		bDefaults.addActionListener(this);

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		p1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 5));
		p1.add(bDefaults);
		p1.add(bClose);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bClose)
			setVisible(false);

		else if (e.getSource() == nbPanel.bApply)
		{
			ColorScheme cs = (ColorScheme) nbPanel.schemeCombo.getSelectedItem();
			winMain.vizColor(cs.getModel());
		}

		else if (e.getSource() == bDefaults)
		{
			String msg = RB.getString("gui.dialog.ColorDialog.defaultsMsg");

			String[] options = new String[] {
				RB.getString("gui.dialog.ColorDialog.bDefaults"),
				RB.getString("gui.text.cancel") };

			if (TaskDialog.show(msg, MsgBox.WAR, 1, options) == 0)
				nbPanel.resetColors();
		}
	}
}