package flapjack.gui.navpanel;

import java.awt.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.visualization.*;

public class VisualizationNode extends BaseNode
{
	private GenotypePanel gPanel;

	public VisualizationNode(DataSet dataSet, GenotypePanel gPanel)
	{
		super(dataSet);

		this.gPanel = gPanel;
	}

	public String toString()
	{
		return "visualization";
	}

	public void setActions()
	{
	}

	public JPanel getPanel()
	{
		gPanel.setDataSet(dataSet);

		return gPanel;
	}
}