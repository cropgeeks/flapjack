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
	}

	public String toString()
	{
		return viewSet.getName();
	}

	public void setActions()
	{
		Actions.vizOverview.setEnabled(true);
		Actions.vizExportImage.setEnabled(true);

		Actions.dataSortLinesBySimilarity.setEnabled(true);
		Actions.dataSortLinesByLocus.setEnabled(true);
		Actions.dataColorRandom.setEnabled(true);
		Actions.dataColorNucleotide.setEnabled(true);
		Actions.dataColorNucleotideSim.setEnabled(true);
		Actions.dataColorNucleotideSimGS.setEnabled(true);
	}

	public JPanel getPanel()
	{
		gPanel.setViewSet(viewSet);

		return gPanel;
	}
}