package flapjack.gui.visualization;

import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;

public class CanvasMenu
{
	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;

	private JPopupMenu menu = new JPopupMenu();

	private JCheckBoxMenuItem mLock;
	public static JCheckBoxMenuItem mShowGenotypes;
	private JMenu mColor;
	private JMenuItem mColorRandom;
	private JMenuItem mColorNucleotide;
	private JMenuItem mColorNucleotideSim;
	private JMenuItem mColorNucleotideSimGS;
	private JMenu mSortLines;
	private JMenuItem mSortLinesBySimilarity;
	private JMenuItem mSortLinesByLocus;

	private AbstractAction aLock;

	CanvasMenu(GenotypePanel gPanel, GenotypeCanvas canvas)
	{
		this.gPanel = gPanel;
		this.canvas = canvas;

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
		mShowGenotypes = WinMainMenuBar.getCheckedItem(Actions.vizOverlayGenotypes, KeyEvent.VK_O, KeyEvent.VK_G, 0, Prefs.visShowGenotypes);

		mColorRandom = WinMainMenuBar.getItem(Actions.vizColorRandom, KeyEvent.VK_R, 0, 0);
		mColorNucleotide = WinMainMenuBar.getItem(Actions.vizColorNucleotide, KeyEvent.VK_N, 0, 0);
		mColorNucleotideSim = WinMainMenuBar.getItem(Actions.vizColorNucleotideSim, KeyEvent.VK_2, 0, 0);
		mColorNucleotideSimGS = WinMainMenuBar.getItem(Actions.vizColorNucleotideSimGS, KeyEvent.VK_G, 0, 0);

		mColor = new JMenu(RB.getString("gui.WinMainMenuBar.mVizColor"));
		mColor.setMnemonic(KeyEvent.VK_C);
		mColor.add(mColorNucleotide);
		mColor.add(mColorNucleotideSim);
		mColor.add(mColorNucleotideSimGS);
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

		menu.show(e.getComponent(), e.getX(), e.getY());
	}
}