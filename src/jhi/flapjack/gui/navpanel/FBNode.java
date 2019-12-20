// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.navpanel;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.forwardbreeding.*;
import jhi.flapjack.gui.visualization.*;

import javax.swing.*;

public class FBNode extends VisualizationChildNode
{
	private FBPanel panel;

	public FBNode(GenotypePanel gPanel, GTViewSet viewSet)
	{
		super(gPanel, viewSet, viewSet.getLines().get(0).getLineResults().getName());

		panel = new FBPanel(viewSet);
	}

	public void setActions()
	{
		super.setActions();
		Actions.viewNewView.setEnabled(true);

		// TODO: make dynamic based on inclusion of QTL data or not
		Actions.dataFilterQTLs.setEnabled(true);
		Actions.dataRenameDataSet.setEnabled(true);
		Actions.dataDeleteDataSet.setEnabled(true);
		Actions.dataFindDataSet.setEnabled(true);
	}

	public JPanel getPanel()
	{
		mapViewSet();

		return panel;
	}
}