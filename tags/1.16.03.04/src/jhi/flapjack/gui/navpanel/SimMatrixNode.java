// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.navpanel;

import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.simmatrix.*;

public class SimMatrixNode extends BaseNode
{
	private SimMatrixPanel panel;
	private SimMatrix matrix;

	public SimMatrixNode(DataSet dataSet, GTViewSet viewSet, SimMatrix matrix)
	{
		super(dataSet);

		this.matrix = matrix;

		panel = new SimMatrixPanel(viewSet, matrix);
	}

	public String toString()
	{
		return matrix.getTitle();
	}

	public void setActions()
	{
		Actions.alysDendrogram.setEnabled(true);
		Actions.alysPCoA.setEnabled(true);
	}

	public JPanel getPanel()
	{
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