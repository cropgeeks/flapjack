package flapjack.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import scri.commons.gui.*;

public class WinMainMenuBar extends JMenuBar
{
	private WinMain winMain;
	private int menuShortcut;

	private JMenu mFile;
	private JMenu mFileRecent;
	private JMenuItem mFileNew;
	private JMenuItem mFileOpen;
	private JMenuItem mFileSave;
	private JMenuItem mFileSaveAs;
	private JMenuItem mFileImport;
	private JMenuItem mFileExit;

	private JMenu mEdit;
	private JMenuItem mEditUndo;
	private JMenuItem mEditRedo;

	private JMenu mViz;
	public static JCheckBoxMenuItem mVizOverview;
	private JMenuItem mVizExportImage;
	private JMenu mVizColor;
	private JMenuItem mVizColorRandom;
	private JMenuItem mVizColorNucleotide;
	private JMenuItem mVizColorLineSim;
	private JMenuItem mVizColorLineSimGS;
	private JMenuItem mVizColorMarkerSim;
	private JMenuItem mVizColorMarkerSimGS;
	private JMenuItem mVizColorSimple2Color;
	static  JCheckBoxMenuItem mVizOverlayGenotypes;

	private JMenu mData;
	private JMenu mDataSortLines;
	private JMenuItem mDataSortLinesBySimilarity;
	private JMenuItem mDataSortLinesByLocus;
	private JMenuItem mDataFind;

	private JMenu mWnd;
	private JMenuItem mWndMinimize;
	private JMenuItem mWndZoom;
	static  JCheckBoxMenuItem mWndFlapjack;

	private JMenu mHelp;
	private JMenuItem mHelpPrefs;
	private JMenuItem mHelpUpdate;
	private JMenuItem mHelpAbout;

	WinMainMenuBar(WinMain winMain)
	{
		this.winMain = winMain;

		// Returns value for "CTRL" under most OSs, and the "apple" key for OS X
		menuShortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		new Actions(winMain);

		setBorderPainted(false);

		createFileMenu();
		createEditMenu();
		createVizMenu();
		createDataMenu();
		createWndMenu();
		createHelpMenu();
	}

	private void createFileMenu()
	{
		mFile = new JMenu(RB.getString("gui.WinMainMenuBar.mFile"));
		mFile.setMnemonic(KeyEvent.VK_F);

		mFileNew = getItem(Actions.fileNew, KeyEvent.VK_N, KeyEvent.VK_N, menuShortcut);
		mFileOpen = getItem(Actions.fileOpen, KeyEvent.VK_O, KeyEvent.VK_O, menuShortcut);
		mFileSave = getItem(Actions.fileSave, KeyEvent.VK_S, KeyEvent.VK_S, menuShortcut);
		mFileSaveAs = getItem(Actions.fileSaveAs, KeyEvent.VK_A, 0, 0);
		mFileImport = getItem(Actions.fileImport, KeyEvent.VK_I, 0, 0);
		mFileExit = getItem(Actions.fileExit, KeyEvent.VK_X, 0, 0);

		mFileRecent = new JMenu(RB.getString("gui.WinMainMenuBar.mFileRecent"));
		mFileRecent.setMnemonic(KeyEvent.VK_R);
		createRecentMenu(null);

		mFile.add(mFileNew);
		mFile.add(mFileOpen);
		mFile.addSeparator();
		mFile.add(mFileSave);
		mFile.add(mFileSaveAs);
		mFile.addSeparator();
		mFile.add(mFileImport);
		mFile.addSeparator();
		mFile.add(mFileRecent);
		// We don't add these options to OS X as they are auto-added by Apple
		if (SystemUtils.isMacOS() == false)
		{
			mFile.addSeparator();
			mFile.add(mFileExit);
		}

		add(mFile);
	}

	private void createEditMenu()
	{
		mEdit = new JMenu(RB.getString("gui.WinMainMenuBar.mEdit"));
		mEdit.setMnemonic(KeyEvent.VK_E);

		mEditUndo = getItem(Actions.editUndo, KeyEvent.VK_U, KeyEvent.VK_Z, menuShortcut);
		mEditRedo = getItem(Actions.editRedo, KeyEvent.VK_R, KeyEvent.VK_Y, menuShortcut);

		mEdit.add(mEditUndo);
		mEdit.add(mEditRedo);

		add(mEdit);
	}

	private void createVizMenu()
	{
		mViz = new JMenu(RB.getString("gui.WinMainMenuBar.mViz"));
		mViz.setMnemonic(KeyEvent.VK_V);

		mVizColor = new JMenu(RB.getString("gui.WinMainMenuBar.mVizColor"));
		mVizColor.setMnemonic(KeyEvent.VK_C);

		mVizOverview = getCheckedItem(Actions.vizOverview, KeyEvent.VK_S, KeyEvent.VK_F7, 0, Prefs.guiOverviewDialog);
		mVizExportImage = getItem(Actions.vizExportImage, KeyEvent.VK_E, 0, 0);
		mVizColorRandom = getItem(Actions.vizColorRandom, KeyEvent.VK_R, 0, 0);
		mVizColorNucleotide = getItem(Actions.vizColorNucleotide, KeyEvent.VK_N, 0, 0);
		mVizColorLineSim = getItem(Actions.vizColorLineSim, KeyEvent.VK_L, 0, 0);
		mVizColorLineSim.setDisplayedMnemonicIndex(17);
		mVizColorLineSimGS = getItem(Actions.vizColorLineSimGS, KeyEvent.VK_G, 0, 0);
		mVizColorMarkerSim = getItem(Actions.vizColorMarkerSim, KeyEvent.VK_K, 0, 0);
		mVizColorMarkerSim.setDisplayedMnemonicIndex(17);
		mVizColorMarkerSimGS = getItem(Actions.vizColorMarkerSimGS, KeyEvent.VK_G, 0, 0);
		mVizColorSimple2Color = getItem(Actions.vizColorSimple2Color, KeyEvent.VK_S, 0, 0);
		mVizOverlayGenotypes = getCheckedItem(Actions.vizOverlayGenotypes, KeyEvent.VK_O, KeyEvent.VK_G, menuShortcut, Prefs.visShowGenotypes);

		mVizColor.add(mVizColorNucleotide);
		mVizColor.add(mVizColorSimple2Color);
		mVizColor.add(mVizColorLineSim);
//		mVizColor.add(mVizColorLineSimGS);
		mVizColor.add(mVizColorMarkerSim);
//		mVizColor.add(mVizColorMarkerSimGS);
		mVizColor.addSeparator();
		mVizColor.add(mVizColorRandom);

		mViz.add(mVizExportImage);
		mViz.addSeparator();
		mViz.add(mVizColor);
		mViz.add(mVizOverlayGenotypes);
		mViz.add(mVizOverview);

		add(mViz);
	}

	private void createDataMenu()
	{
		mData = new JMenu(RB.getString("gui.WinMainMenuBar.mData"));
		mData.setMnemonic(KeyEvent.VK_D);

		mDataSortLines = new JMenu(RB.getString("gui.WinMainMenuBar.mDataSortLines"));
		mDataSortLines.setMnemonic(KeyEvent.VK_S);
		mDataSortLinesBySimilarity = getItem(Actions.dataSortLinesBySimilarity, KeyEvent.VK_S, 0, 0);
		mDataSortLinesByLocus = getItem(Actions.dataSortLinesByLocus, KeyEvent.VK_L, 0, 0);
		mDataFind = getItem(Actions.dataFind, KeyEvent.VK_F, KeyEvent.VK_F, menuShortcut);

		mDataSortLines.add(mDataSortLinesBySimilarity);
		mDataSortLines.add(mDataSortLinesByLocus);

		mData.add(mDataSortLines);
		mData.addSeparator();
		mData.add(mDataFind);

		add(mData);
	}

	private void createWndMenu()
	{
		mWnd = new JMenu(RB.getString("gui.WinMainMenuBar.mWnd"));
		mWnd.setMnemonic(KeyEvent.VK_W);

		mWndMinimize = getItem(Actions.wndMinimize, KeyEvent.VK_M, KeyEvent.VK_M, menuShortcut);
		mWndZoom = getItem(Actions.wndZoom, KeyEvent.VK_Z, 0, 0);
		mWndFlapjack = getCheckedItem(Actions.wndFlapjack, KeyEvent.VK_F, 0, 0, true);

		mWnd.add(mWndMinimize);
		mWnd.add(mWndZoom);
		mWnd.addSeparator();
		mWnd.add(mWndFlapjack);

		if (SystemUtils.isMacOS())
			add(mWnd);
	}

	private void createHelpMenu()
	{
		mHelp = new JMenu(RB.getString("gui.WinMainMenuBar.mHelp"));
		mHelp.setMnemonic(KeyEvent.VK_H);

		mHelpPrefs = getItem(Actions.helpPrefs, KeyEvent.VK_P, 0, 0);
		mHelpUpdate = getItem(Actions.helpUpdate, KeyEvent.VK_C, 0, 0);
		mHelpAbout = getItem(Actions.helpAbout, KeyEvent.VK_A, 0, 0);

		// We don't add this option to OS X as it is auto-added by Apple
		if (SystemUtils.isMacOS() == false)
		{
			mHelp.add(mHelpPrefs);
			mHelp.addSeparator();
		}

		mHelp.add(mHelpUpdate);

		// We don't add this option to OS X as it is auto-added by Apple
		if (SystemUtils.isMacOS() == false)
		{
			mHelp.addSeparator();
			mHelp.add(mHelpAbout);
		}

		add(mHelp);
	}

	public static JMenuItem getItem(Action action, int mnemonic, int keymask, int modifiers)
	{
		JMenuItem item = new JMenuItem(action);
		item.setMnemonic(mnemonic);

		if (keymask != 0)
			item.setAccelerator(KeyStroke.getKeyStroke(keymask, modifiers));

		return item;
	}

	public static JCheckBoxMenuItem getCheckedItem(Action action, int mnemonic, int keymask, int modifiers, boolean state)
	{
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(action);
		item.setMnemonic(mnemonic);
		item.setState(state);

		if (keymask != 0)
			item.setAccelerator(KeyStroke.getKeyStroke(keymask, modifiers));

		return item;
	}

	public static JRadioButtonMenuItem getRadioItem(Action action, int mnemonic, int keymask, int modifiers)
	{
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(action);
		item.setMnemonic(mnemonic);

		if (keymask != 0)
			item.setAccelerator(KeyStroke.getKeyStroke(keymask, modifiers));

		return item;
	}

	// Maintains and creates the Recent Projects file menu, adding new entries
	// as previously unseen projects are opened or saved, and ensuring that:
	//   a) the most recently accessed file is always at the start of the list
	//   b) the list never grows bigger than four entries
	void createRecentMenu(File file)
	{
		// Begin by making a list of the recent file locations
		LinkedList<String> entries = new LinkedList<String>();
		entries.add(Prefs.guiRecentProject1);
		entries.add(Prefs.guiRecentProject2);
		entries.add(Prefs.guiRecentProject3);
		entries.add(Prefs.guiRecentProject4);

		// See if any of the items on that list match the file being accessed,
		// moving (or adding) the entry to the first location
		if (file != null)
		{
			int location = -1;
			for (int i = 0; i < entries.size(); i++)
				if (entries.get(i) != null)
					if (entries.get(i).equals(file.getPath()))
						location = i;

			if (location != -1)
				entries.remove(location);

			entries.addFirst(file.getPath());

			if (entries.size() > 4)
				entries.removeLast();
		}

		// The menu can then be built up, one item per entry
		mFileRecent.removeAll();

		int vk = 0;
		for (final String entry: entries)
		{
			if (entry != null)
			{
				JMenuItem item = new JMenuItem((++vk) + " " + entry);
				item.setMnemonic(KeyEvent.VK_0 + vk);
				item.addActionListener(new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						winMain.fileOpen(new File(entry));
					}
				});

				mFileRecent.add(item);
			}
		}

		mFileRecent.setEnabled(mFileRecent.getItemCount() > 0);

		// Finally, update the preference strings with the new ordering
		Prefs.guiRecentProject1 = entries.get(0);
		Prefs.guiRecentProject2 = entries.get(1);
		Prefs.guiRecentProject3 = entries.get(2);
		Prefs.guiRecentProject4 = entries.get(3);
	}
}