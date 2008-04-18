package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;

public class CanvasMenu
{
	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;

	private int menuShortcut;

	private JPopupMenu menu = new JPopupMenu();

	private JCheckBoxMenuItem mLock;
	public static JCheckBoxMenuItem mShowGenotypes;
	private JMenu mColor;
	private JMenuItem mColorRandom;
	private JMenuItem mColorNucleotide;
	private JMenuItem mColorLineSim;
	private JMenuItem mColorLineSimGS;
	private JMenuItem mColorMarkerSim;
	private JMenuItem mColorMarkerSimGS;
	private JMenuItem mColorSimple2Color;
	private JMenu mSortLines;
	private JMenuItem mSortLinesBySimilarity;
	private JMenuItem mSortLinesByLocus;
	private JMenuItem mFind;

	private AbstractAction aLock;

	CanvasMenu(GenotypePanel gPanel, GenotypeCanvas canvas)
	{
		this.gPanel = gPanel;
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
		mLock = WinMainMenuBar.getCheckedItem(aLock, KeyEvent.VK_L, 0, 0, canvas.locked);
		mShowGenotypes = WinMainMenuBar.getCheckedItem(Actions.vizOverlayGenotypes, KeyEvent.VK_O, KeyEvent.VK_G, menuShortcut, Prefs.visShowGenotypes);
		mColorRandom = WinMainMenuBar.getItem(Actions.vizColorRandom, KeyEvent.VK_R, 0, 0);
		mColorNucleotide = WinMainMenuBar.getItem(Actions.vizColorNucleotide, KeyEvent.VK_N, 0, 0);
		mColorLineSim = WinMainMenuBar.getItem(Actions.vizColorLineSim, KeyEvent.VK_L, 0, 0);
		mColorLineSim.setDisplayedMnemonicIndex(17);
		mColorLineSimGS = WinMainMenuBar.getItem(Actions.vizColorLineSimGS, KeyEvent.VK_G, 0, 0);
		mColorMarkerSim = WinMainMenuBar.getItem(Actions.vizColorMarkerSim, KeyEvent.VK_M, 0, 0);
		mColorMarkerSim.setDisplayedMnemonicIndex(17);
		mColorMarkerSimGS = WinMainMenuBar.getItem(Actions.vizColorMarkerSimGS, KeyEvent.VK_G, 0, 0);
		mColorSimple2Color = WinMainMenuBar.getItem(Actions.vizColorSimple2Color, KeyEvent.VK_S, 0, 0);
		mFind = WinMainMenuBar.getItem(Actions.dataFind, KeyEvent.VK_F, KeyEvent.VK_F, menuShortcut);

		mColor = new JMenu(RB.getString("gui.WinMainMenuBar.mVizColor"));
		mColor.setMnemonic(KeyEvent.VK_C);
		mColor.add(mColorNucleotide);
		mColor.add(mColorSimple2Color);
		mColor.add(mColorLineSim);
//		mColor.add(mColorLineSimGS);
		mColor.add(mColorMarkerSim);
//		mColor.add(mColorMarkerSimGS);
		mColor.addSeparator();
		mColor.add(mColorRandom);

		mSortLinesBySimilarity = WinMainMenuBar.getItem(Actions.dataSortLinesBySimilarity, KeyEvent.VK_S, 0, 0);
		mSortLinesByLocus = WinMainMenuBar.getItem(Actions.dataSortLinesByLocus, KeyEvent.VK_L, 0, 0);

		mSortLines = new JMenu(RB.getString("gui.WinMainMenuBar.mDataSortLines"));
		mSortLines.setMnemonic(KeyEvent.VK_S);
		mSortLines.add(mSortLinesBySimilarity);
		mSortLines.add(mSortLinesByLocus);
	}

	void handlePopup(MouseEvent e)
	{
		menu = new JPopupMenu();

		menu.add(mLock);
		menu.add(mShowGenotypes);
		menu.add(mColor);
		menu.addSeparator();
		menu.add(mSortLines);
		menu.addSeparator();
		menu.add(mFind);

		menu.show(e.getComponent(), e.getX(), e.getY());
	}
}