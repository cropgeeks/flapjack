// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import com.sun.imageio.spi.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class CanvasMenu
{
	private GenotypeCanvas canvas;
	private WinMain winMain;

	private int menuShortcut;

	private JPopupMenu menu = new JPopupMenu();

	private JCheckBoxMenuItem mLock;
	private JMenuItem mBookmark;
	private JMenu mSplitLines;
	private JMenuItem mInsertLine;
	private JMenuItem mDeleteLine;
	private JMenuItem mInsertSplitter;
	private JMenuItem mRemoveSplitter;
	private JMenuItem mDuplicateLine;
	private JMenuItem mDuplicateLineRemove;
	private JCheckBoxMenuItem mShowGenotypes;
	private JMenu mHighlight;
	private JCheckBoxMenuItem mHighlightHtZ;
	private JCheckBoxMenuItem mHighlightHoZ;
	private JCheckBoxMenuItem mHighlightGaps;
	private JCheckBoxMenuItem mDisableGradients;
	private JMenuItem mSelectTraits;
	private JMenu mColor;
	private JMenuItem mColorCustomize;
	private JRadioButtonMenuItem mColorRandom;
	private JRadioButtonMenuItem mColorRandomWSP;
	private JRadioButtonMenuItem mColorNucleotide;
	private JRadioButtonMenuItem mColorNucleotide01;
	private JRadioButtonMenuItem mColorLineSim;
	private JRadioButtonMenuItem mColorLineSimExact;
	private JRadioButtonMenuItem mColorMarkerSim;
	private JRadioButtonMenuItem mColorSimple2Color;
	private JRadioButtonMenuItem mColorABHData;
	private JRadioButtonMenuItem mColorAlleleFreq;
	private JRadioButtonMenuItem mColorBinned;
	private JMenuItem mColorMagic;
	private JMenuItem mToggleCanvas;
	private JMenu mDataDB;
	private JMenuItem mDBLineName;
	private JMenuItem mDBMarkerName;
	private JMenuItem mDBSettings;
	private JMenu mSortLines;
	private JMenuItem mSortLinesBySimilarity;
	private JMenuItem mSortLinesByTrait;
	private JMenuItem mSortLinesByExternal;
	private JMenuItem mSortLinesAlphabetically;
	private JMenuItem mFind;
	private JMenu mFilter;
	private JMenuItem mFilterMissingMarkers;
	private JMenuItem mFilterMissingMarkersByLine;
	private JMenuItem mFilterMonomorphicMarkers;


	CanvasMenu(GenotypeCanvas canvas, WinMain winMain)
	{
		this.canvas = canvas;
		this.winMain = winMain;

		// Returns value for "CTRL" under most OSs, and the "apple" key for OS X
		menuShortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		createItems();
	}

	boolean isShowingMenu()
		{ return menu.isVisible(); }

	private void createItems()
	{
		mBookmark = WinMainMenuBar.getItem(Actions.viewBookmark, "gui.Actions.viewBookmark", 0, 0);
		mInsertLine = WinMainMenuBar.getItem(Actions.editInsertLine, "gui.Actions.editInsertLine", 0, 0);
		mDeleteLine = WinMainMenuBar.getItem(Actions.editDeleteLine, "gui.Actions.editDeleteLine", 0, 0);
		mDuplicateLine = WinMainMenuBar.getItem(Actions.editDuplicateLine, "gui.Actions.editDuplicateLine", 0, 0);
		mDuplicateLineRemove = WinMainMenuBar.getItem(Actions.editDuplicateLineRemove, "gui.Actions.editDuplicateLineRemove", 0, 0);
		mInsertSplitter = WinMainMenuBar.getItem(Actions.editInsertSplitter, "gui.Actions.editInsertLine", 0, 0);
		mRemoveSplitter = WinMainMenuBar.getItem(Actions.editDeleteSplitter, "gui.Actions.editDeleteLine", 0, 0);
		mShowGenotypes = WinMainMenuBar.getCheckedItem(Actions.vizOverlayGenotypes, "gui.Actions.vizOverlayGenotypes", KeyEvent.VK_G, menuShortcut);
		mDisableGradients = WinMainMenuBar.getCheckedItem(Actions.vizDisableGradients, "gui.Actions.vizDisableGradients", 0, 0);
		mHighlightHtZ = WinMainMenuBar.getCheckedItem(Actions.vizHighlightHtZ, "gui.Actions.vizHighlightHtZ", 0, 0);
		mHighlightHoZ = WinMainMenuBar.getCheckedItem(Actions.vizHighlightHoZ, "gui.Actions.vizHighlightHoZ", 0, 0);
		mHighlightGaps = WinMainMenuBar.getCheckedItem(Actions.vizHighlightGaps, "gui.Actions.vizHighlightGaps", 0, 0);
		mSelectTraits = WinMainMenuBar.getItem(Actions.dataSelectTraits, "gui.Actions.dataSelectTraits", 0, 0);
		mColorCustomize = WinMainMenuBar.getItem(Actions.vizColorCustomize, "gui.Actions.vizColorCustomize", 0, 0);
		mColorRandom = WinMainMenuBar.getRadioItem(Actions.vizColorRandom, "gui.Actions.vizColorRandom", 0, 0);
		mColorRandomWSP = WinMainMenuBar.getRadioItem(Actions.vizColorRandomWSP, "gui.Actions.vizColorRandomWSP", 0, 0);
		mColorNucleotide = WinMainMenuBar.getRadioItem(Actions.vizColorNucleotide, "gui.Actions.vizColorNucleotide", 0, 0);
		mColorNucleotide01 = WinMainMenuBar.getRadioItem(Actions.vizColorNucleotide01, "gui.Actions.vizColorNucleotide01", 0, 0);
		mColorSimple2Color = WinMainMenuBar.getRadioItem(Actions.vizColorSimple2Color, "gui.Actions.vizColorSimple2Color", 0, 0);
		mColorABHData = WinMainMenuBar.getRadioItem(Actions.vizColorABHData, "gui.Actions.vizColorABHData", 0, 0);
		mColorLineSim = WinMainMenuBar.getRadioItem(Actions.vizColorLineSim, "gui.Actions.vizColorLineSim", 0, 0);
		mColorLineSimExact = WinMainMenuBar.getRadioItem(Actions.vizColorLineSimExact, "gui.Actions.vizColorLineSimExact", 0, 0);
		mColorMarkerSim = WinMainMenuBar.getRadioItem(Actions.vizColorMarkerSim, "gui.Actions.vizColorMarkerSim", 0, 0);
		mColorAlleleFreq = WinMainMenuBar.getRadioItem(Actions.vizColorAlleleFreq, "gui.Actions.vizColorAlleleFreq", 0, 0);
		mColorBinned = WinMainMenuBar.getRadioItem(Actions.vizColorBinned, "gui.Actions.vizColorBinned", 0, 0);
		mColorMagic = WinMainMenuBar.getRadioItem(Actions.vizColorMagic, "gui.Actions.vizColorMagic", 0, 0);
		mToggleCanvas = WinMainMenuBar.getItem(Actions.viewToggleCanvas, "gui.Actions.viewToggleCanvas", 0, 0);
		mFind = WinMainMenuBar.getItem(Actions.dataFind, "gui.Actions.dataFind", KeyEvent.VK_F, menuShortcut);
		mDBLineName = WinMainMenuBar.getItem(Actions.dataDBLineName, "gui.Actions.dataDBLineName", 0, 0);
		mDBMarkerName = WinMainMenuBar.getItem(Actions.dataDBMarkerName, "gui.Actions.dataDBMarkerName", 0, 0);
		mDBSettings = WinMainMenuBar.getItem(Actions.dataDBSettings, "gui.Actions.dataDBSettings", 0, 0);

		mSplitLines = new JMenu(RB.getString("gui.CanvasMenu.mSplitLines"));
		mSplitLines.add(mInsertLine);
		mSplitLines.add(mDeleteLine);
		mSplitLines.addSeparator();
		mSplitLines.add(mDuplicateLine);
		mSplitLines.add(mDuplicateLineRemove);
		mSplitLines.addSeparator();
		mSplitLines.add(mInsertSplitter);
		mSplitLines.add(mRemoveSplitter);

		mColor = new JMenu(RB.getString("gui.WinMainMenuBar.mVizColor"));
		mColor.setIcon(Actions.getIcon("COLORS"));
		RB.setMnemonic(mColor, "gui.WinMainMenuBar.mVizColor");
		winMain.mViz.handleColorMenu(mColor);

		mColor.add(mColorNucleotide);
		mColor.add(mColorNucleotide01);
//		mColor.add(mColorABHData);
		mColor.add(mColorSimple2Color);
		mColor.add(mColorLineSim);
		mColor.add(mColorLineSimExact);
		mColor.add(mColorMarkerSim);
		mColor.add(mColorAlleleFreq);
		mColor.add(mColorMagic);
		mColor.add(mColorBinned);
		mColor.addSeparator();
		mColor.add(mColorRandom);
		mColor.add(mColorRandomWSP);
		mColor.addSeparator();
		mColor.add(mColorCustomize);

		mHighlight = new JMenu(RB.getString("gui.WinMainMenuBar.mVizHighlight"));
		RB.setMnemonic(mHighlight, "gui.WinMainMenuBar.mVizHighlight");
		mHighlight.add(mHighlightHtZ);
		mHighlight.add(mHighlightHoZ);
		mHighlight.add(mHighlightGaps);

		mDataDB = new JMenu(RB.getString("gui.WinMainMenuBar.mDataDB"));
		mDataDB.setIcon(Actions.getIcon("DATABASE"));
		RB.setMnemonic(mDataDB, "gui.WinMainMenuBar.mDataDB");
		mDataDB.add(mDBLineName);
		mDataDB.add(mDBMarkerName);
		mDataDB.addSeparator();
		mDataDB.add(mDBSettings);

		mSortLinesBySimilarity = WinMainMenuBar.getItem(Actions.alysSortLinesBySimilarity, "gui.Actions.alysSortLinesBySimilarity", 0, 0);
		mSortLinesByTrait = WinMainMenuBar.getItem(Actions.alysSortLinesByTrait, "gui.Actions.alysSortLinesByTrait", 0, 0);
		mSortLinesByExternal = WinMainMenuBar.getItem(Actions.alysSortLinesByExternal, "gui.Actions.alysSortLinesByExternal", 0, 0);
		mSortLinesAlphabetically = WinMainMenuBar.getItem(Actions.alysSortLinesAlphabetically, "gui.Actions.alysSortLinesAlphabetically", 0, 0);

		mSortLines = new JMenu(RB.getString("gui.WinMainMenuBar.mAlysSortLines"));
		RB.setMnemonic(mSortLines, "gui.WinMainMenuBar.mAlysSortLines");
		mSortLines.add(mSortLinesAlphabetically);
		mSortLines.addSeparator();
		mSortLines.add(mSortLinesBySimilarity);
		mSortLines.add(mSortLinesByTrait);
		mSortLines.add(mSortLinesByExternal);

		mFilterMissingMarkers = WinMainMenuBar.getItem(Actions.editFilterMissingMarkers, "gui.Actions.editFilterMissingMarkers", 0, 0);
		mFilterMissingMarkersByLine = WinMainMenuBar.getItem(Actions.editFilterMissingMarkersByLine, "gui.Actions.editFilterMissingMarkersByLine", 0, 0);
		mFilterMonomorphicMarkers = WinMainMenuBar.getItem(Actions.editFilterMonomorphicMarkers, "gui.Actions.editFilterMonomorphicMarkers", 0, 0);

		mFilter = new JMenu(RB.getString("gui.WinMainMenuBar.mEditFilterMarkers"));
		RB.setMnemonic(mFilter, "gui.WinMainMenuBar.mEditFilterMarkers");
		mFilter.add(mFilterMissingMarkers);
		mFilter.add(mFilterMissingMarkersByLine);
		mFilter.addSeparator();
		mFilter.add(mFilterMonomorphicMarkers);
	}

	void handlePopup(MouseEvent e)
	{
		// Normal mouse-over the canvas would keep these indices up to date,
		// but because we disable mouseover events while the menu is showing, we
		// still need to make sure the x/y values are correct - this deals with
		// the situation where the user can right click multiple times (showing
		// the menu multiple times) before they actually pick an option from it
		int xIndex = canvas.getMarker(e.getPoint());
		int yIndex = canvas.getLine(e.getPoint());
		canvas.setHighlightedIndices(yIndex, xIndex);

		GTView view = canvas.view;


		// Create and display the menu
		menu = new JPopupMenu();

		menu.add(mBookmark);
		menu.addSeparator();
		menu.add(mSortLines);
		menu.add(mSplitLines);
		menu.add(mFilter);
		menu.addSeparator();
		menu.add(mColor);
		menu.add(mShowGenotypes);
		menu.add(mDisableGradients);
		menu.add(mHighlight);
		menu.addSeparator();
		menu.add(mSelectTraits);
		menu.addSeparator();
		menu.add(mFind);
		menu.add(mDataDB);

		// Set enabled/disable states
		mBookmark.setEnabled(Bookmark.allowBookmarking(view));

		mInsertSplitter.setEnabled(view.getSplitterIndex() == -1);

		if (view.getSplitterIndex() != -1)
			mRemoveSplitter.setEnabled(true);
		else
			mRemoveSplitter.setEnabled(false);

		// Can you delete a dummy line from this click?
		mDeleteLine.setEnabled(view.hasDummyLines());

		// Can you delete a duplicate line from this click?
		mDuplicateLineRemove.setEnabled(false);
		if (view.mouseOverLine >= 0 && view.mouseOverLine < view.lineCount())
			if (view.getLineInfo(view.mouseOverLine).getDuplicate())
				mDuplicateLineRemove.setEnabled(true);

		if (view.getViewSet().tableHandler().table() != null)
		{
			mInsertLine.setEnabled(false);
			mDuplicateLine.setEnabled(false);
			mInsertSplitter.setEnabled(false);
		}

		menu.show(e.getComponent(), e.getX(), e.getY());
	}
}