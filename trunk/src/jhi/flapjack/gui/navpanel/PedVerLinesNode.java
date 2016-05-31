package jhi.flapjack.gui.navpanel;

import jhi.flapjack.data.DataSet;
import jhi.flapjack.data.GTViewSet;
import jhi.flapjack.gui.Actions;
import jhi.flapjack.gui.pedver.PedVerLinesPanel;
import jhi.flapjack.gui.pedver.PedVerPanel;
import scri.commons.gui.RB;

import javax.swing.*;

/**
 * Created by gs40939 on 06/05/2016.
 */
public class PedVerLinesNode extends BaseNode
{
	private PedVerLinesPanel panel;

	public PedVerLinesNode(DataSet dataSet, GTViewSet viewSet)
	{
		super(dataSet);

		panel = new PedVerLinesPanel(viewSet);
	}

	public String toString()
	{
		return RB.getString("gui.navpanel.MabcNode.node");
	}

	public void setActions()
	{
		Actions.viewNewView.setEnabled(true);

		// TODO: make dynamic based on inclusion of QTL data or not
		Actions.dataFilterQTLs.setEnabled(true);
		Actions.dataRenameDataSet.setEnabled(true);
		Actions.dataDeleteDataSet.setEnabled(true);
	}

	public JPanel getPanel()
	{
		return panel;
	}
}
