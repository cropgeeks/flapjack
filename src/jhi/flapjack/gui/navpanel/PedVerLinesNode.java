// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.navpanel;

import jhi.flapjack.gui.pedver.lines.PedVerLinesPanel;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.visualization.*;

public class PedVerLinesNode extends VisualizationChildNode
{
	private PedVerLinesPanel panel;

	public PedVerLinesNode(GenotypePanel gPanel, GTViewSet viewSet)
	{
		super(gPanel, viewSet, viewSet.getLines().get(0).getLineResults().getName());

		panel = new PedVerLinesPanel(viewSet);
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