package flapjack.gui.navpanel;

import java.awt.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.gui.visualization.*;

public class VisualizationNode extends BaseNode
{
	private GenotypePanel gPanel;
	private GTViewSet viewSet;

	public VisualizationNode(DataSet dataSet, GTViewSet viewSet, GenotypePanel gPanel)
	{
		super(dataSet);

		this.viewSet = viewSet;
		this.gPanel = gPanel;

		// Cache the data within the views ready for rendering
		for (int i = 0; i < viewSet.getChromosomeCount(); i++)
			viewSet.getView(i).cacheLines();
	}

	public String toString()
	{
		return viewSet.getName();
	}

	public void setActions()
	{
		Actions.viewOverview.setEnabled(true);
		Actions.dataSortLines.setEnabled(true);
	}

	public JPanel getPanel()
	{
		gPanel.setViewSet(viewSet);

		return gPanel;
	}
}