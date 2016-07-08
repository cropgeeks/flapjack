// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.navpanel;

import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.pedver.*;

import scri.commons.gui.*;

public class PedVerF1sNode extends BaseNode
{
	private PedVerF1sPanel panel;

	public PedVerF1sNode(DataSet dataSet, GTViewSet viewSet)
	{
		super(dataSet);

		panel = new PedVerF1sPanel(viewSet);
	}

	public String toString()
	{
		return "PedVerF1s Results";
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
