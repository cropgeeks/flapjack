// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

public class CanvasMenu
{
	private GenotypeCanvas canvas;

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
	private JMenuItem mColorRandom;
	private JMenuItem mColorRandomWSP;
	private JMenuItem mColorNucleotide;
	private JMenuItem mColorLineSim;
	private JMenuItem mColorLineSimGS;
	private JMenuItem mColorMarkerSim;
	private JMenuItem mColorMarkerSimGS;
	private JMenuItem mColorSimple2Color;
	private JMenuItem mColorABHData;
	private JMenuItem mColorAlleleFreq;
	private JMenuItem mColorBinned;
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


	CanvasMenu(GenotypeCanvas canvas)
	{
		this.canvas = canvas;

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
		mColorRandom = WinMainMenuBar.getItem(Actions.vizColorRandom, "gui.Actions.vizColorRandom", 0, 0);
		mColorRandomWSP = WinMainMenuBar.getItem(Actions.vizColorRandomWSP, "gui.Actions.vizColorRandomWSP", 0, 0);
		mColorNucleotide = WinMainMenuBar.getItem(Actions.vizColorNucleotide, "gui.Actions.vizColorNucleotide", 0, 0);
		mColorSimple2Color = WinMainMenuBar.getItem(Actions.vizColorSimple2Color, "gui.Actions.vizColorSimple2Color", 0, 0);
		mColorABHData = WinMainMenuBar.getItem(Actions.vizColorABHData, "gui.Actions.vizColorABHData", 0, 0);
		mColorLineSim = WinMainMenuBar.getItem(Actions.vizColorLineSim, "gui.Actions.vizColorLineSim", 0, 0);
//		mColorLineSimGS = WinMainMenuBar.getItem(Actions.vizColorLineSimGS, KeyEvent.VK_G, 0, 0);
		mColorMarkerSim = WinMainMenuBar.getItem(Actions.vizColorMarkerSim, "gui.Actions.vizColorMarkerSim", 0, 0);
//		mColorMarkerSimGS = WinMainMenuBar.getItem(Actions.vizColorMarkerSimGS, KeyEvent.VK_G, 0, 0);
		mColorAlleleFreq = WinMainMenuBar.getItem(Actions.vizColorAlleleFreq, "gui.Actions.vizColorAlleleFreq", 0, 0);
		mColorBinned = WinMainMenuBar.getItem(Actions.vizColorBinned, "gui.Actions.vizColorBinned", 0, 0);
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
		mColor.add(mColorNucleotide);
		mColor.add(mColorABHData);
		mColor.add(mColorSimple2Color);
		mColor.add(mColorLineSim);
//		mColor.add(mColorLineSimGS);
		mColor.add(mColorMarkerSim);
//		mColor.add(mColorMarkerSimGS);
		mColor.add(mColorAlleleFreq);
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
		menu.add(mSplitLines);
		menu.addSeparator();
		menu.add(mColor);
		menu.add(mShowGenotypes);
		menu.add(mDisableGradients);
		menu.add(mHighlight);
		menu.addSeparator();
		menu.add(mSelectTraits);
		menu.addSeparator();
		menu.add(mSortLines);
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


		menu.show(e.getComponent(), e.getX(), e.getY());
	}
}