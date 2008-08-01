package flapjack.gui.navpanel;

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
		Actions.editModeNavigation.setEnabled(true);
		Actions.editModeMarker.setEnabled(true);
		Actions.editModeLine.setEnabled(true);
		Actions.editSelectMarkersAll.setEnabled(true);
		Actions.editSelectMarkersNone.setEnabled(true);
		Actions.editSelectMarkersInvert.setEnabled(true);
		Actions.editHideMarkers.setEnabled(true);
		Actions.editSelectLinesAll.setEnabled(true);
		Actions.editSelectLinesNone.setEnabled(true);
		Actions.editSelectLinesInvert.setEnabled(true);
		Actions.editHideLines.setEnabled(true);

		Actions.vizExportImage.setEnabled(true);
		Actions.vizColorCustomize.setEnabled(true);
		Actions.vizColorRandom.setEnabled(true);
		Actions.vizColorNucleotide.setEnabled(true);
		Actions.vizColorLineSim.setEnabled(true);
		Actions.vizColorLineSimGS.setEnabled(true);
		Actions.vizColorMarkerSim.setEnabled(true);
		Actions.vizColorMarkerSimGS.setEnabled(true);
		Actions.vizColorSimple2Color.setEnabled(true);
		Actions.vizColorAlleleFreq.setEnabled(true);
		Actions.vizOverlayGenotypes.setEnabled(true);
		Actions.vizHighlightHZ.setEnabled(true);
		Actions.vizSelectTraits.setEnabled(true);
		Actions.vizNewView.setEnabled(true);
		Actions.vizRenameView.setEnabled(true);
		Actions.vizDeleteView.setEnabled(true);
		Actions.vizToggleCanvas.setEnabled(true);
		Actions.vizOverview.setEnabled(true);

		Actions.dataSortLinesBySimilarity.setEnabled(true);
		Actions.dataSortLinesByTrait.setEnabled(true);
		Actions.dataFind.setEnabled(true);
		Actions.dataStatistics.setEnabled(true);
		Actions.dataDBSettings.setEnabled(true);
		Actions.dataRenameDataSet.setEnabled(true);
		Actions.dataDeleteDataSet.setEnabled(true);

		DBAssociation db = viewSet.getDataSet().getDbAssociation();
		Actions.dataDBLineName.setEnabled(db.isLineSearchEnabled());
		Actions.dataDBMarkerName.setEnabled(db.isMarkerSearchEnabled());
	}

	public JPanel getPanel()
	{
		gPanel.setViewSet(viewSet);

		return gPanel;
	}
}