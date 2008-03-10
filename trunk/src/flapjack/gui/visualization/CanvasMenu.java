package flapjack.gui.visualization;

import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;

class CanvasMenu
{
	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;

	private JPopupMenu menu = new JPopupMenu();

	private JCheckBoxMenuItem mLock;
	private JMenuItem mSortLines;
	private JCheckBoxMenuItem mShowGenotypes;
	private JMenu mColor;
	private JRadioButtonMenuItem mColorRandom;
	private JRadioButtonMenuItem mColorNucleotide;

	private AbstractAction aLock;
	private AbstractAction aShowGenotypes;

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

		aShowGenotypes = new AbstractAction(RB.getString("gui.Actions.canvasShowGenotypes")) {
			public void actionPerformed(ActionEvent e)
			{
				Prefs.visShowGenotypes = !Prefs.visShowGenotypes;
				gPanel.refreshView();
			}
		};
	}

	private void createItems()
	{
		mLock = WinMainMenuBar.getCheckedItem(aLock, KeyEvent.VK_L, 0, 0, canvas.locked);
		mSortLines = WinMainMenuBar.getItem(Actions.dataSortLines, KeyEvent.VK_S, 0, 0);
		mShowGenotypes = WinMainMenuBar.getCheckedItem(aShowGenotypes, KeyEvent.VK_O, 0, 0, Prefs.visShowGenotypes);

		mColorRandom = WinMainMenuBar.getRadioItem(Actions.dataColorRandom, KeyEvent.VK_R, 0, 0);
		mColorNucleotide = WinMainMenuBar.getRadioItem(Actions.dataColorNucleotide, KeyEvent.VK_N, 0, 0);

		ButtonGroup group = new ButtonGroup();
		group.add(mColorRandom);
		group.add(mColorNucleotide);

		mColor = new JMenu(RB.getString("gui.WinMainMenuBar.mDataColor"));
		mColor.setMnemonic(KeyEvent.VK_C);
		mColor.add(mColorRandom);
		mColor.add(mColorNucleotide);
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