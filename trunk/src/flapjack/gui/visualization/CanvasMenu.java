package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;

public class CanvasMenu
{
	private GenotypeCanvas canvas;

	private int menuShortcut;

	private JPopupMenu menu = new JPopupMenu();

	private JCheckBoxMenuItem mLock;
	public static JCheckBoxMenuItem mShowGenotypes;
	public static JCheckBoxMenuItem mHighlightHZ;
	private JMenuItem mSelectTraits;
	private JMenu mColor;
	private JMenuItem mColorCustomize;
	private JMenuItem mColorRandom;
	private JMenuItem mColorNucleotide;
	private JMenuItem mColorLineSim;
	private JMenuItem mColorLineSimGS;
	private JMenuItem mColorMarkerSim;
	private JMenuItem mColorMarkerSimGS;
	private JMenuItem mColorSimple2Color;
	private JMenuItem mColorAlleleFreq;
	private JMenuItem mToggleCanvas;
	private JMenu mDataDB;
	private JMenuItem mDBLineName;
	private JMenuItem mDBMarkerName;
	private JMenu mSortLines;
	private JMenuItem mSortLinesBySimilarity;
	private JMenuItem mSortLinesByTrait;
	private JMenuItem mFind;


	private AbstractAction aLock;

	CanvasMenu(GenotypeCanvas canvas)
	{
		this.canvas = canvas;

		// Returns value for "CTRL" under most OSs, and the "apple" key for OS X
		menuShortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		createActions();
		createItems();
	}

	boolean isShowingMenu()
		{ return menu.isVisible(); }

	private void createActions()
	{
		aLock = new AbstractAction(RB.getString("gui.Actions.canvasLock")) {
			public void actionPerformed(ActionEvent e) {
				canvas.locked = !canvas.locked;
			}
		};
	}

	private void createItems()
	{
		mLock = WinMainMenuBar.getCheckedItem(aLock, "gui.Actions.canvasLock", 0, 0, canvas.locked);
		mShowGenotypes = WinMainMenuBar.getCheckedItem(Actions.vizOverlayGenotypes, "gui.Actions.vizOverlayGenotypes", KeyEvent.VK_G, menuShortcut, Prefs.visShowGenotypes);
		mHighlightHZ = WinMainMenuBar.getCheckedItem(Actions.vizHighlightHZ, "gui.Actions.vizHighlightHZ", KeyEvent.VK_H, menuShortcut, Prefs.visHighlightHZ);
		mSelectTraits = WinMainMenuBar.getItem(Actions.vizSelectTraits, "gui.Actions.vizSelectTraits", 0, 0);
		mColorCustomize = WinMainMenuBar.getItem(Actions.vizColorCustomize, "gui.Actions.vizColorCustomize", 0, 0);
		mColorRandom = WinMainMenuBar.getItem(Actions.vizColorRandom, "gui.Actions.vizColorRandom", 0, 0);
		mColorNucleotide = WinMainMenuBar.getItem(Actions.vizColorNucleotide, "gui.Actions.vizColorNucleotide", 0, 0);
		mColorSimple2Color = WinMainMenuBar.getItem(Actions.vizColorSimple2Color, "gui.Actions.vizColorSimple2Color", 0, 0);
		mColorLineSim = WinMainMenuBar.getItem(Actions.vizColorLineSim, "gui.Actions.vizColorLineSim", 0, 0);
//		mColorLineSimGS = WinMainMenuBar.getItem(Actions.vizColorLineSimGS, KeyEvent.VK_G, 0, 0);
		mColorMarkerSim = WinMainMenuBar.getItem(Actions.vizColorMarkerSim, "gui.Actions.vizColorMarkerSim", 0, 0);
//		mColorMarkerSimGS = WinMainMenuBar.getItem(Actions.vizColorMarkerSimGS, KeyEvent.VK_G, 0, 0);
		mColorAlleleFreq = WinMainMenuBar.getItem(Actions.vizColorAlleleFreq, "gui.Actions.vizColorAlleleFreq", 0, 0);
		mToggleCanvas = WinMainMenuBar.getItem(Actions.vizToggleCanvas, "gui.Actions.vizToggleCanvas", 0, 0);
		mFind = WinMainMenuBar.getItem(Actions.dataFind, "gui.Actions.dataFind", KeyEvent.VK_F, menuShortcut);
		mDBLineName = WinMainMenuBar.getItem(Actions.dataDBLineName, "gui.Actions.dataDBLineName", 0, 0);
		mDBMarkerName = WinMainMenuBar.getItem(Actions.dataDBMarkerName, "gui.Actions.dataDBMarkerName", 0, 0);

		mColor = new JMenu(RB.getString("gui.WinMainMenuBar.mVizColor"));
		RB.setMnemonic(mColor, "gui.WinMainMenuBar.mVizColor");
		mColor.add(mColorNucleotide);
		mColor.add(mColorSimple2Color);
		mColor.add(mColorLineSim);
//		mColor.add(mColorLineSimGS);
		mColor.add(mColorMarkerSim);
//		mColor.add(mColorMarkerSimGS);
		mColor.add(mColorAlleleFreq);
		mColor.addSeparator();
		mColor.add(mColorRandom);
		mColor.addSeparator();
		mColor.add(mColorCustomize);

		mDataDB = new JMenu(RB.getString("gui.WinMainMenuBar.mDataDB"));
		RB.setMnemonic(mDataDB, "gui.WinMainMenuBar.mDataDB");
		mDataDB.add(mDBLineName);
		mDataDB.add(mDBMarkerName);

		mSortLinesBySimilarity = WinMainMenuBar.getItem(Actions.dataSortLinesBySimilarity, "gui.Actions.dataSortLinesBySimilarity", 0, 0);
		mSortLinesByTrait = WinMainMenuBar.getItem(Actions.dataSortLinesByTrait, "gui.Actions.dataSortLinesByTrait", 0, 0);

		mSortLines = new JMenu(RB.getString("gui.WinMainMenuBar.mDataSortLines"));
		RB.setMnemonic(mSortLines, "gui.WinMainMenuBar.mDataSortLines");
		mSortLines.add(mSortLinesBySimilarity);
		mSortLines.add(mSortLinesByTrait);
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


		// Create and display the menu
		menu = new JPopupMenu();

		menu.add(mLock);
		menu.addSeparator();
		menu.add(mColor);
		menu.add(mShowGenotypes);
		menu.add(mHighlightHZ);
		menu.add(mSelectTraits);
		menu.addSeparator();
		menu.add(mSortLines);
		menu.add(mFind);
		menu.add(mDataDB);

		menu.show(e.getComponent(), e.getX(), e.getY());
	}
}