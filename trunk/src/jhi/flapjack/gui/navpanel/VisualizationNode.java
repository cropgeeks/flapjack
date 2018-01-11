// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.navpanel;

import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.visualization.*;

public class VisualizationNode extends BaseNode
{
	private GenotypePanel gPanel;
	private ChromosomePanel cPanel;
	private GTViewSet viewSet;

	public VisualizationNode(DataSet dataSet, GTViewSet viewSet, GenotypePanel gPanel, ChromosomePanel cPanel)
	{
		super(dataSet);

		this.viewSet = viewSet;
		this.gPanel = gPanel;
		this.cPanel = cPanel;
	}

	public GTViewSet getViewSet()
		{ return viewSet; }

	public String toString()
	{
		return viewSet.getName();
	}

	public void setActions()
	{
		// These are the options we're enabling with the classic genotypes view
		if (Prefs.visShowChromosomes == false)
		{
			Actions.editModeNavigation.setEnabled(true);
			Actions.editModeMarker.setEnabled(true);
			Actions.editModeLine.setEnabled(true);
			Actions.editSelectMarkersAll.setEnabled(true);
			Actions.editSelectMarkersNone.setEnabled(true);
			Actions.editSelectMarkersInvert.setEnabled(true);
			Actions.editSelectMarkersImport.setEnabled(true);
			Actions.editSelectMarkersMonomorphic.setEnabled(true);
			Actions.editHideMarkers.setEnabled(true);
			Actions.editFilterMissingMarkers.setEnabled(true);
			Actions.editFilterMissingMarkersByLine.setEnabled(true);
			Actions.editFilterHeterozygousMarkers.setEnabled(true);
			Actions.editFilterHeterozygousMarkersByLine.setEnabled(true);
			Actions.editFilterMonomorphicMarkers.setEnabled(true);
			Actions.editSelectLinesAll.setEnabled(true);
			Actions.editSelectLinesNone.setEnabled(true);
			Actions.editSelectLinesInvert.setEnabled(true);
			Actions.editSelectLinesImport.setEnabled(true);
			Actions.editHideLines.setEnabled(true);
			Actions.editInsertLine.setEnabled(true);
			Actions.editDeleteLine.setEnabled(true);
			Actions.editDuplicateLine.setEnabled(true);
			Actions.editDuplicateLineRemove.setEnabled(true);
			Actions.editInsertSplitter.setEnabled(true);
			Actions.editDeleteSplitter.setEnabled(true);
			Actions.editCustomMap.setEnabled(true);

			Actions.viewNewView.setEnabled(true);
			Actions.viewRenameView.setEnabled(true);
			Actions.viewDeleteView.setEnabled(true);
			Actions.viewToggleCanvas.setEnabled(true);
			Actions.viewOverview.setEnabled(true);
			Actions.viewBookmark.setEnabled(true);
			Actions.viewPageLeft.setEnabled(true);
			Actions.viewPageRight.setEnabled(true);
			Actions.viewGenotypes.setEnabled(true);
			Actions.viewChromosomes.setEnabled(true);

			Actions.vizExportImage.setEnabled(true);
			Actions.vizExportData.setEnabled(true);
			Actions.vizCreatePedigree.setEnabled(true);
			Actions.vizColorCustomize.setEnabled(true);
			Actions.vizColorRandom.setEnabled(true);
			Actions.vizColorRandomWSP.setEnabled(true);
			Actions.vizColorNucleotide.setEnabled(true);
			Actions.vizColorNucleotide01.setEnabled(true);
//			Actions.vizColorABHData.setEnabled(true);
			Actions.vizColorLineSim.setEnabled(true);
			Actions.vizColorLineSimExact.setEnabled(true);
			Actions.vizColorMarkerSim.setEnabled(true);
			Actions.vizColorLineSimAny.setEnabled(true);
			Actions.vizColorParentDual.setEnabled(true);
			Actions.vizColorParentTotal.setEnabled(true);
			Actions.vizColorSimple2Color.setEnabled(true);
			Actions.vizColorAlleleFreq.setEnabled(true);
			Actions.vizColorBinned.setEnabled(true);
			Actions.vizColorMagic.setEnabled(true);
			Actions.vizScalingLocal.setEnabled(true);
			Actions.vizScalingGlobal.setEnabled(true);
			Actions.vizScalingClassic.setEnabled(true);
			Actions.vizOverlayGenotypes.setEnabled(true);
			Actions.vizDisableGradients.setEnabled(true);
			Actions.vizHighlightHtZ.setEnabled(true);
			Actions.vizHighlightHoZ.setEnabled(true);
			Actions.vizHighlightGaps.setEnabled(true);

			Actions.alysSortLinesBySimilarity.setEnabled(true);
			Actions.alysSortLinesByTrait.setEnabled(true);
			Actions.alysSortLinesByExternal.setEnabled(true);
			Actions.alysSortLinesAlphabetically.setEnabled(true);
			// Only enable sim matrix analysis with non-binned data
			if (viewSet.getDataSet().getBinnedData().containsBins() == false)
				Actions.alysSimMatrix.setEnabled(true);
			Actions.alysMABC.setEnabled(true);
			Actions.alysPedVer.setEnabled(true);
			Actions.alysPedVerLines.setEnabled(true);

			// TODO: make dynamic based on inclusion of QTL data or not
			Actions.dataFilterQTLs.setEnabled(true);
			Actions.dataFind.setEnabled(true);
			Actions.dataStatistics.setEnabled(true);
			Actions.dataDBSettings.setEnabled(true);
			Actions.dataRenameDataSet.setEnabled(true);
			Actions.dataDeleteDataSet.setEnabled(true);
			Actions.dataSelectTraits.setEnabled(true);
			Actions.dataSelectTextTraits.setEnabled(true);
			Actions.dataSelectGraph.setEnabled(true);
		}
		else
		{
			Actions.viewNewView.setEnabled(true);
			Actions.viewRenameView.setEnabled(true);
			Actions.viewDeleteView.setEnabled(true);
			Actions.viewOverview.setEnabled(true);
			Actions.viewGenotypes.setEnabled(true);
			Actions.viewChromosomes.setEnabled(true);

			Actions.vizExportData.setEnabled(true);

			Actions.dataStatistics.setEnabled(true);
			Actions.dataDBSettings.setEnabled(true);
			Actions.dataRenameDataSet.setEnabled(true);
			Actions.dataDeleteDataSet.setEnabled(true);
		}

		DBAssociation db = viewSet.getDataSet().getDbAssociation();
		Actions.dataDBLineName.setEnabled(db.isLineSearchEnabled());
		Actions.dataDBMarkerName.setEnabled(db.isMarkerSearchEnabled());
	}

	public JPanel getPanel()
	{
		gPanel.setViewSet(viewSet);
		cPanel.setViewSet(viewSet);

		if (Prefs.visShowChromosomes)
			return cPanel;
		else
			return gPanel;
	}
}