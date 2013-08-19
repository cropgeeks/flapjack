package flapjack.gui.navpanel;

import java.awt.image.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.dendrogram.*;

public class DendrogramNode extends BaseNode
{
	private DendrogramPanel panel;

	public DendrogramNode(DataSet dataSet, Dendrogram dendrogram)
	{
		super(dataSet);

		panel = new DendrogramPanel(dendrogram);
	}

	public String toString()
	{
		return "Dendogram Panel";
	}

	public void setActions()
	{

	}

	public JPanel getPanel()
	{
		return panel;
	}
}
