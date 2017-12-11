// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.dialog.*;
import jhi.flapjack.gui.dialog.analysis.*;
import jhi.flapjack.gui.visualization.*;
import jhi.flapjack.io.*;

import scri.commons.gui.*;

public class WinMain extends JFrame
{
	private WinMainMenuBar menubar;
	private WinMainToolBar toolbar;
	private WinMainStatusBar statusbar;

	private NavPanel navPanel;
	private GenotypePanel gPanel;

	private FindDialog findDialog;
	public FilterQTLsDialog filterQTLDialog;
	public SelectGraphDialog selectGraphDialog;

	// The user's project
	private Project project = new Project();

	// Menu event handlers
	public MenuFile mFile = new MenuFile();
	public MenuEdit mEdit = new MenuEdit();
	public MenuView mView = new MenuView();
	public MenuVisualization mViz = new MenuVisualization();
	public MenuAnalysis mAnalysis = new MenuAnalysis();
	public MenuData mData = new MenuData();
	MenuHelp mHelp = new MenuHelp();


	WinMain()
	{
		setTitle(RB.getString("gui.WinMain.title") + " - " + Install4j.VERSION);
		setIconImage(Icons.getIcon("FLAPJACK").getImage());

		menubar = new WinMainMenuBar(this);
		toolbar = new WinMainToolBar();
		setJMenuBar(menubar);

		navPanel = new NavPanel(this);
		gPanel = navPanel.getGenotypePanel();
		statusbar = new WinMainStatusBar(gPanel);

		mFile.setComponents(this, menubar, navPanel);
		mEdit.setComponents(gPanel);
		mView.setComponents(navPanel);
		mViz.setComponents(navPanel);
		mAnalysis.setComponents(this, navPanel);
		mData.setComponents(this, navPanel);
		mHelp.setComponents(gPanel);

		add(toolbar, BorderLayout.NORTH);
		add(navPanel);
		add(statusbar, BorderLayout.SOUTH);

		setSize(Prefs.guiWinMainW, Prefs.guiWinMainH);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);


		SwingUtils.positionWindow(
			this, null, Prefs.guiWinMainX, Prefs.guiWinMainY);

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
					Prefs.guiWinMainW  = getSize().width;
					Prefs.guiWinMainH = getSize().height;
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

	public Project getProject()
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

	public void hideDialogs()
	{
		if (findDialog != null)
			findDialog.setVisible(false);

		if (filterQTLDialog != null)
			filterQTLDialog.setVisible(false);
	}

	public GenotypePanel getGenotypePanel()
	{
		return navPanel.getGenotypePanel();
	}

	public NavPanel getNavPanel()
	{
		return navPanel;
	}
}