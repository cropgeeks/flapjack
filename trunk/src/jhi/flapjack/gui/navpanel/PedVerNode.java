package jhi.flapjack.gui.navpanel;

import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.pedver.*;

import scri.commons.gui.*;

/**
 * Created by gs40939 on 06/05/2016.
 */
public class PedVerNode extends BaseNode
{
	private PedVerPanel panel;

	public PedVerNode(DataSet dataSet, GTViewSet viewSet)
	{
		super(dataSet);

		panel = new PedVerPanel(viewSet);
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
