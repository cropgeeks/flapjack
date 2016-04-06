// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.navpanel;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.mabc.*;

import scri.commons.gui.*;

public class MabcNode extends BaseNode
{
	private MabcPanel panel;

	public MabcNode(DataSet dataSet, GTViewSet viewSet, ArrayList<MABCLineStats> lineStats)
	{
		super(dataSet);

		panel = new MabcPanel(viewSet, lineStats);
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