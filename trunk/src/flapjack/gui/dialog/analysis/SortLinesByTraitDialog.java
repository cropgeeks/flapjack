package flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;
import flapjack.gui.visualization.*;

import scri.commons.gui.*;

public class SortLinesByTraitDialog extends JDialog implements ActionListener
{
	JButton bOK, bCancel;
	private boolean isOK = false;

	private GenotypePanel gPanel;
	private NBSortLinesByTraitPanel nbPanel;

	public SortLinesByTraitDialog(GenotypePanel gPanel)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.analysis.SortLinesByTraitDialog.title"),
			true
		);

		this.gPanel = gPanel;
		nbPanel = new NBSortLinesByTraitPanel(this, gPanel);

		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		if (nbPanel.combo1.getItemCount() == 0)
			bOK.setEnabled(false);

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

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		p1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 5));
		p1.add(bOK);
		p1.add(bCancel);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOK)
			isOK = true;

		setVisible(false);
	}

	public boolean isOK()
		{ return isOK; }

	// Returns an array containing the selected index of the first combo box and
	// the selected index of the second combo box (minus 1 as its first element
	// can be left blank; this will return -1 meaning don't use)
	public int[] getTraitIndices()
	{
		return new int[] {
			nbPanel.combo1.getSelectedIndex(),
			nbPanel.combo2.getSelectedIndex()-1,
			nbPanel.combo3.getSelectedIndex()-1
		};
	}

	public boolean[] getAscendingIndices()
	{
		return new boolean[] {
			nbPanel.rAsc1.isSelected(),
			nbPanel.rAsc2.isSelected(),
			nbPanel.rAsc3.isSelected()
		};
	}
}