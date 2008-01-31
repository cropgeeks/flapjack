package flapjack.gui.navpanel;

import java.awt.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;
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
		return "Visualization";
	}

	public void setActions()
	{
		System.out.println("setting to true");
		Actions.viewOverview.setEnabled(true);
	}

	public JPanel getPanel()
	{
		gPanel.setDataSet(dataSet);

		return gPanel;
	}
}