// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.navpanel;

import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.simmatrix.*;
import jhi.flapjack.gui.visualization.*;

public class SimMatrixNode extends VisualizationChildNode
{
	private SimMatrixPanel panel;
	private SimMatrix matrix;

	public SimMatrixNode(GenotypePanel gPanel, GTViewSet viewSet, SimMatrix matrix)
	{
		super(gPanel, viewSet, matrix.getTitle());

		this.matrix = matrix;

		panel = new SimMatrixPanel(viewSet, matrix);
	}

	public void setActions()
	{
		super.setActions();

		Actions.alysDendrogram.setEnabled(true);
		Actions.alysPCoA.setEnabled(true);
	}

	public JPanel getPanel()
	{
		mapViewSet();

		return panel;
	}

	public SimMatrix getMatrix()
	{
		return matrix;
	}

	public String getIconName()
	{
		if (matrix.getIsOrdered())
			return "SIMMATRIX_ORDERED";
		else
			return "SIMMATRIX";
	}
}