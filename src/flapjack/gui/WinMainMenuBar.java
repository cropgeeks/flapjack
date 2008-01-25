package flapjack.gui;

import java.awt.event.*;
import javax.swing.*;

public class WinMainMenuBar extends JMenuBar
{
	private WinMain winMain;

	private JMenu mFile;
	private JMenuItem mFileNew;
	private JMenuItem mFileOpen;
	private JMenuItem mFileSave;
	private JMenuItem mFileSaveAs;
	private JMenuItem mFileImport;
	private JMenuItem mFileExit;

	private JMenu mView;
	private JMenuItem mViewOverview;

	private JMenu mHelp;
	private JMenuItem mHelpAbout;

	WinMainMenuBar(WinMain winMain)
	{
		new Actions(winMain);

		setBorderPainted(false);

		createFileMenu();
		createViewMenu();
		createHelpMenu();
	}

	private void createFileMenu()
	{
		mFile = new JMenu(RB.getString("gui.WinMainMenuBar.mFile"));
		mFile.setMnemonic(KeyEvent.VK_F);

		mFileNew = getItem(Actions.fileNew, KeyEvent.VK_N, KeyEvent.VK_N, InputEvent.CTRL_MASK);
		mFileOpen = getItem(Actions.fileOpen, KeyEvent.VK_O, KeyEvent.VK_O, InputEvent.CTRL_MASK);
		mFileSave = getItem(Actions.fileSave, KeyEvent.VK_S, KeyEvent.VK_S, InputEvent.CTRL_MASK);
		mFileSaveAs = getItem(Actions.fileSaveAs, KeyEvent.VK_A, 0, 0);
		mFileImport = getItem(Actions.fileImport, KeyEvent.VK_I, 0, 0);
		mFileExit = getItem(Actions.fileExit, KeyEvent.VK_X, 0, 0);

		mFile.add(mFileNew);
		mFile.add(mFileOpen);
		mFile.addSeparator();
		mFile.add(mFileSave);
		mFile.add(mFileSaveAs);
		mFile.addSeparator();
		mFile.add(mFileImport);
		mFile.addSeparator();
		mFile.add(mFileExit);

		add(mFile);
	}

	private void createViewMenu()
	{
		mView = new JMenu(RB.getString("gui.WinMainMenuBar.mView"));
		mView.setMnemonic(KeyEvent.VK_V);

		mViewOverview = getItem(Actions.viewOverview, KeyEvent.VK_O, KeyEvent.VK_F7, 0);

		mView.add(mViewOverview);

		add(mView);
	}

	private void createHelpMenu()
	{
		mHelp = new JMenu(RB.getString("gui.WinMainMenuBar.mHelp"));
		mHelp.setMnemonic(KeyEvent.VK_H);

		mHelpAbout = getItem(Actions.helpAbout, KeyEvent.VK_A, 0, 0);

		mHelp.add(mHelpAbout);

		add(mHelp);
	}

	public static JMenuItem getItem(Action action, int mnemonic, int accelerator, int keymask)
	{
		JMenuItem item = new JMenuItem(action);
		item.setMnemonic(mnemonic);

		if (accelerator != 0)
			item.setAccelerator(KeyStroke.getKeyStroke(accelerator, keymask));
		// if (p != null)
		// item.addMouseListener(textListener);

		return item;
	}
}