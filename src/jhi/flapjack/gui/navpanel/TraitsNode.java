// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.navpanel;

import java.awt.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.traits.*;

import scri.commons.gui.*;

public class TraitsNode extends BaseNode
{
	private TabPanel tabbedPanel;

	public TraitsNode(DataSet dataSet)
	{
		super(dataSet);

		tabbedPanel = new TabPanel(dataSet);
	}

	public String toString()
	{
		return RB.getString("gui.navpanel.TraitsNode.node");
	}

	public void setActions()
	{
		Actions.viewNewView.setEnabled(true);

		// TODO: make dynamic based on inclusion of QTL data or not
		Actions.dataFilterQTLs.setEnabled(true);
		Actions.dataRenameDataSet.setEnabled(true);
		Actions.dataDeleteDataSet.setEnabled(true);
		Actions.dataFindDataSet.setEnabled(true);
	}

	public JPanel getPanel()
	{
		return tabbedPanel;
	}
}