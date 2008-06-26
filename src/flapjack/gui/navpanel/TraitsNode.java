package flapjack.gui.navpanel;

import java.awt.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.gui.traits.*;

public class TraitsNode extends BaseNode
{
	private TraitsPanel traitsPanel;

	public TraitsNode(DataSet dataSet)
	{
		super(dataSet);

		traitsPanel = new TraitsPanel(dataSet);
	}

	public String toString()
	{
		return RB.getString("gui.navpanel.TraitsNode.node");
	}

	public void setActions()
	{
		Actions.vizNewView.setEnabled(true);

		Actions.dataRenameDataSet.setEnabled(true);
		Actions.dataDeleteDataSet.setEnabled(true);
	}

	public JPanel getPanel()
	{
		return traitsPanel;
	}
}