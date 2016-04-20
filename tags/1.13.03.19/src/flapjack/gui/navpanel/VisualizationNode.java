// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

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
		Actions.editFilterMissingMarkers.setEnabled(true);
		Actions.editSelectLinesAll.setEnabled(true);
		Actions.editSelectLinesNone.setEnabled(true);
		Actions.editSelectLinesInvert.setEnabled(true);
		Actions.editHideLines.setEnabled(true);
		Actions.editInsertLine.setEnabled(true);
		Actions.editDeleteLine.setEnabled(true);
		Actions.editDuplicateLine.setEnabled(true);
		Actions.editDuplicateLineRemove.setEnabled(true);
		Actions.editInsertSplitter.setEnabled(true);
		Actions.editDeleteSplitter.setEnabled(true);

		Actions.viewNewView.setEnabled(true);
		Actions.viewRenameView.setEnabled(true);
		Actions.viewDeleteView.setEnabled(true);
		Actions.viewToggleCanvas.setEnabled(true);
		Actions.viewOverview.setEnabled(true);
		Actions.viewBookmark.setEnabled(true);
		Actions.viewPageLeft.setEnabled(true);
		Actions.viewPageRight.setEnabled(true);

		Actions.vizExportImage.setEnabled(true);
		Actions.vizExportData.setEnabled(true);
		Actions.vizCreatePedigree.setEnabled(true);
		Actions.vizColorCustomize.setEnabled(true);
		Actions.vizColorRandom.setEnabled(true);
		Actions.vizColorRandomWSP.setEnabled(true);
		Actions.vizColorNucleotide.setEnabled(true);
//		Actions.vizColorABHData.setEnabled(true);
		Actions.vizColorLineSim.setEnabled(true);
		Actions.vizColorLineSimGS.setEnabled(true);
		Actions.vizColorMarkerSim.setEnabled(true);
		Actions.vizColorMarkerSimGS.setEnabled(true);
		Actions.vizColorSimple2Color.setEnabled(true);
		Actions.vizColorAlleleFreq.setEnabled(true);
		Actions.vizScalingLocal.setEnabled(true);
		Actions.vizScalingGlobal.setEnabled(true);
		Actions.vizScalingClassic.setEnabled(true);
		Actions.vizOverlayGenotypes.setEnabled(true);
		Actions.vizHighlightHZ.setEnabled(true);
		Actions.vizHighlightGaps.setEnabled(true);

		Actions.dataSortLinesBySimilarity.setEnabled(true);
		Actions.dataSortLinesByTrait.setEnabled(true);
		Actions.dataSortLinesByExternal.setEnabled(true);
		Actions.dataSortLinesAlphabetically.setEnabled(true);
		// TODO: make dynamic based on inclusion of QTL data or not
		Actions.dataFilterQTLs.setEnabled(true);
		Actions.dataFind.setEnabled(true);
		Actions.dataSimMatrix.setEnabled(true);
		Actions.dataStatistics.setEnabled(true);
		Actions.dataDBSettings.setEnabled(true);
		Actions.dataRenameDataSet.setEnabled(true);
		Actions.dataDeleteDataSet.setEnabled(true);
		Actions.dataSelectTraits.setEnabled(true);
		Actions.dataSelectGraph.setEnabled(true);

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