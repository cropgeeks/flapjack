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

	public GTViewSet getViewSet()
		{ return viewSet; }

	public String toString()
	{
		return viewSet.getName();
	}

	public void setActions()
	{
		Actions.vizOverview.setEnabled(true);
		Actions.vizExportImage.setEnabled(true);
		Actions.vizColorCustomize.setEnabled(true);
		Actions.vizColorRandom.setEnabled(true);
		Actions.vizColorNucleotide.setEnabled(true);
		Actions.vizColorLineSim.setEnabled(true);
		Actions.vizColorLineSimGS.setEnabled(true);
		Actions.vizColorMarkerSim.setEnabled(true);
		Actions.vizColorMarkerSimGS.setEnabled(true);
		Actions.vizColorSimple2Color.setEnabled(true);
		Actions.vizOverlayGenotypes.setEnabled(true);
		Actions.vizNewView.setEnabled(true);
		Actions.vizRenameView.setEnabled(true);
		Actions.vizDeleteView.setEnabled(true);

		Actions.dataSortLinesBySimilarity.setEnabled(true);
		Actions.dataSortLinesByLocus.setEnabled(true);
		Actions.dataFind.setEnabled(true);
		Actions.dataStatistics.setEnabled(true);
		Actions.dataRenameDataSet.setEnabled(true);
		Actions.dataDeleteDataSet.setEnabled(true);
	}

	public JPanel getPanel()
	{
		gPanel.setViewSet(viewSet);

		return gPanel;
	}
}