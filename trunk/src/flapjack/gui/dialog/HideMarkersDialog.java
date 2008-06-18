package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;
import flapjack.gui.visualization.*;

import scri.commons.gui.*;

public class HideMarkersDialog extends JDialog implements ActionListener
{
	private JButton bOK, bCancel, bHelp;
	private boolean isOK = false;

	private NBHideMarkersPanel nbPanel;
	private GenotypePanel gPanel;

	public HideMarkersDialog(GenotypePanel gPanel)
	{
		super(Flapjack.winMain, "", true);
		this.gPanel = gPanel;

		setTitle(RB.getString("gui.dialog.HideMarkersDialog.title"));

		nbPanel = new NBHideMarkersPanel(this, gPanel.getView());

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
		FlapjackUtils.setHelp(bHelp, "gui.dialog.HideMarkersDialog");

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		p1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 5));
		p1.add(bOK);
		p1.add(bCancel);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == nbPanel.bRestore && !restoreHiddenMarkers())
			return;

		else if (e.getSource() == bOK)
		{
			nbPanel.isOK();
			isOK = true;
		}

		setVisible(false);
	}

	public boolean isOK()
		{ return isOK; }

	private boolean restoreHiddenMarkers()
	{
		String msg = RB.getString("gui.dialog.HideMarkersDialog.restoreMsg");

		String[] options = new String[] {
				RB.getString("gui.dialog.HideMarkersDialog.restore"),
				RB.getString("gui.text.cancel") };

		if (TaskDialog.show(msg, MsgBox.QST, 1, options) != 0)
			return false;

		// Create an undo state for the restore operation
		HidMarkersState state = new HidMarkersState(gPanel.getView(),
			RB.getString("gui.visualization.HidMarkersState.restoredMarkers"));
		state.createUndoState();

		// Do the restore
		gPanel.getView().restoreHiddenMarkers();
		gPanel.refreshView();

		// Create a redo state
		state.createRedoState();
		gPanel.addUndoState(state);

		return true;
	}
}