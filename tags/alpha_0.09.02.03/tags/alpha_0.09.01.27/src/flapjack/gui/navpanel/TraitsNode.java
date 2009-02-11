package flapjack.gui.navpanel;

import java.awt.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.gui.traits.*;

public class TraitsNode extends BaseNode
{
	private TraitsTabbedPanel tabbedPanel;

	public TraitsNode(DataSet dataSet)
	{
		super(dataSet);

		tabbedPanel = new TraitsTabbedPanel(dataSet);
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
		return tabbedPanel;
	}
}