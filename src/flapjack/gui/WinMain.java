package flapjack.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.dialog.analysis.*;
import flapjack.gui.visualization.*;
import flapjack.io.*;

import scri.commons.gui.*;

public class WinMain extends JFrame
{
	private WinMainMenuBar menubar;
	private WinMainToolBar toolbar;
	private WinMainStatusBar statusbar;

	private NavPanel navPanel;
	private GenotypePanel gPanel;

	private FindDialog findDialog;

	// The user's project
	private Project project = new Project();

	// Menu event handlers
	public MenuFile mFile = new MenuFile();
	public MenuEdit mEdit = new MenuEdit();
	public MenuVisualization mViz = new MenuVisualization();
	MenuData mData = new MenuData();
	MenuHelp mHelp = new MenuHelp();


	WinMain()
	{
		setTitle(RB.getString("gui.WinMain.title") + " - " + Install4j.VERSION);
		setIconImage(Icons.getIcon("FLAPJACK").getImage());

		menubar = new WinMainMenuBar(this);
		toolbar = new WinMainToolBar();
		statusbar = new WinMainStatusBar();
		setJMenuBar(menubar);

		navPanel = new NavPanel(this);
		gPanel = navPanel.getGenotypePanel();

		mFile.setComponents(this, menubar, navPanel);
		mEdit.setComponents(gPanel);
		mViz.setComponents(navPanel);
		mData.setComponents(this, navPanel);
		mHelp.setComponents(gPanel);

		add(toolbar, BorderLayout.NORTH);
		add(navPanel);
		add(statusbar, BorderLayout.SOUTH);

		setSize(Prefs.guiWinMainWidth, Prefs.guiWinMainHeight);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);


		// Work out the current screen's width and height
		int scrnW = SwingUtils.getVirtualScreenDimension().width;
		int scrnH = SwingUtils.getVirtualScreenDimension().height;

		// Determine where on screen to display
		if (Prefs.isFirstRun || Prefs.guiWinMainX > (scrnW-50) || Prefs.guiWinMainY > (scrnH-50))
			setLocationRelativeTo(null);
		else
			setLocation(Prefs.guiWinMainX, Prefs.guiWinMainY);

		// Maximize the frame if neccassary
		if (Prefs.guiWinMainMaximized)
			setExtendedState(Frame.MAXIMIZED_BOTH);

		// Window listeners are added last so they don't interfere with the
		// maximization from above
		addListeners();
	}

	private void addListeners()
	{
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e)
			{
				if (getExtendedState() != Frame.MAXIMIZED_BOTH)
				{
					Prefs.guiWinMainWidth  = getSize().width;
					Prefs.guiWinMainHeight = getSize().height;
					Prefs.guiWinMainX = getLocation().x;
					Prefs.guiWinMainY = getLocation().y;

					Prefs.guiWinMainMaximized = false;
				}
				else
					Prefs.guiWinMainMaximized = true;
			}

			public void componentMoved(ComponentEvent e)
			{
				if (getExtendedState() != Frame.MAXIMIZED_BOTH)
				{
					Prefs.guiWinMainX = getLocation().x;
					Prefs.guiWinMainY = getLocation().y;
				}
			}
		});
	}

	boolean okToExit()
	{
		return ProjectSerializer.okToContinue(project, true);
	}

	Project getProject()
		{ return project; }

	void setProject(Project project)
		{ this.project = project; }

	WinMainMenuBar getWinMainMenuBar()
		{ return menubar; }

	FindDialog getFindDialog()
	{
		if (findDialog == null)
			findDialog = new FindDialog(this, gPanel);

		return findDialog;
	}

	void fileExit()
	{
		WindowEvent evt = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		processWindowEvent(evt);
	}
}