// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.navpanel;

import java.awt.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.gui.simmatrix.*;

public class SimMatrixNode extends BaseNode
{
	private SimMatrixPanel panel;

	public SimMatrixNode(DataSet dataSet, GTViewSet viewSet)
	{
		super(dataSet);

		panel = new SimMatrixPanel(viewSet);
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
}