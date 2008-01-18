package flapjack.gui;

import java.awt.event.*;
import javax.swing.*;

public class WinMainMenuBar extends JMenuBar
{
	private WinMain winMain;

	private JMenu mFile;
	private JMenuItem mFileImport;
	private JMenuItem mFileExit;

	WinMainMenuBar(WinMain winMain)
	{
		new Actions(winMain);

		setBorderPainted(false);

		createFileMenu();
	}

	private void createFileMenu()
	{
		mFile = new JMenu(RB.getString("gui.WinMainMenuBar.mFile"));
		mFile.setMnemonic(KeyEvent.VK_F);

		mFileImport = getItem(Actions.fileImport, KeyEvent.VK_I, 0, 0);
		mFileExit = getItem(Actions.fileExit, KeyEvent.VK_X, 0, 0);

		mFile.add(mFileImport);
		mFile.addSeparator();
		mFile.add(mFileExit);

		add(mFile);
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