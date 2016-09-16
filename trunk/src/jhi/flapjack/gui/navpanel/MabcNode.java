// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.navpanel;

import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.mabc.*;
import jhi.flapjack.gui.visualization.*;

import scri.commons.gui.*;

public class MabcNode extends VisualizationChildNode
{
	private MabcPanel panel;

	public MabcNode(GenotypePanel gPanel, GTViewSet viewSet)
	{
		super(gPanel, viewSet);

		panel = new MabcPanel(viewSet);
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
		mapViewSet();

		return panel;
	}
}