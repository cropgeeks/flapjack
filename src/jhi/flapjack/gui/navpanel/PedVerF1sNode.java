// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.navpanel;

import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.pedver.*;
import jhi.flapjack.gui.visualization.*;

public class PedVerF1sNode extends VisualizationChildNode
{
	private PedVerF1sPanel panel;

	public PedVerF1sNode(GenotypePanel gPanel, GTViewSet viewSet)
	{
		super(gPanel, viewSet, viewSet.getLines().get(0).getResults().getName());

		panel = new PedVerF1sPanel(viewSet);
	}

	public void setActions()
	{
		super.setActions();

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