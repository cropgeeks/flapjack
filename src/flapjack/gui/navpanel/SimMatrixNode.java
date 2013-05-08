// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.navpanel;

import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.simmatrix.*;

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
		return "Sim Matrix Panel";
	}

	public void setActions()
	{

	}

	public JPanel getPanel()
	{
		return panel;
	}

	public SimMatrix getMatrix()
	{
		return matrix;
	}
}