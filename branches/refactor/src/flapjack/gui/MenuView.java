// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui;

import scri.commons.gui.*;

import flapjack.data.*;
import flapjack.gui.dialog.*;
import flapjack.gui.visualization.*;

public class MenuView
{
	private NavPanel navPanel;
	private GenotypePanel gPanel;

	void setComponents(NavPanel navPanel)
	{
		this.navPanel = navPanel;
		gPanel = navPanel.getGenotypePanel();
	}

	void viewNewView()
	{
		DataSet dataSet = navPanel.getDataSetForSelection();
		GTViewSet viewSet = navPanel.getViewSetForSelection();

		NewViewDialog dialog = new NewViewDialog(dataSet, viewSet);

		if (dialog.isOK())
		{
			GTViewSet newViewSet = dialog.getNewViewSet();

			dataSet.getViewSets().add(newViewSet);
			navPanel.addedNewVisualizationNode(dataSet);

			Actions.projectModified();
		}
	}

	void viewRenameView()
	{
		GTViewSet viewSet = navPanel.getViewSetForSelection();

		RenameDialog dialog = new RenameDialog(viewSet.getName());

		if (dialog.isOK())
		{
			viewSet.setName(dialog.getNewName());
			navPanel.updateNodeFor(viewSet);

			Actions.projectModified();
		}
	}

	void viewDeleteView()
	{
		GTViewSet viewSet = navPanel.getViewSetForSelection();

		String msg = RB.format("gui.WinMain.deleteViewSet",
			viewSet.getName(), viewSet.getDataSet().getName());

		String[] options = new String[] {
			RB.getString("gui.WinMain.deleteViewSetButton"),
			RB.getString("gui.text.cancel") };

		if (TaskDialog.show(msg, TaskDialog.QST, 1, options) == 0)
		{
			// Remove it from both the project and the GUI
			viewSet.getDataSet().getViewSets().remove(viewSet);
			navPanel.removeVisualizationNode(viewSet);

			Actions.projectModified();
		}
	}

	void viewBookmark()
	{
		GTView view = gPanel.getView();

		ChromosomeMap chromosome = view.getChromosomeMap();
		Line line = view.getLine(view.mouseOverLine);
		Marker marker = view.getMarker(view.mouseOverMarker);
		Bookmark bookmark = new Bookmark(chromosome, line, marker);

		// Stop the highlighter from running before it gets the chance
		// (as the navpanel adding the node would normally enable it)
		BookmarkHighlighter.enable = false;

		view.getViewSet().getBookmarks().add(bookmark);
		navPanel.addedNewBookmarkNode(view.getViewSet(), bookmark);

		Actions.projectModified();
	}

	void viewDeleteBookmark()
	{
		GTViewSet viewSet = gPanel.getViewSet();

		// Remove the bookmark from the navigation panel
		Bookmark bookmark = navPanel.removeSelectedBookmarkNode();
		// And then remove it from the project
		viewSet.getBookmarks().remove(bookmark);

		Actions.projectModified();
	}

	void viewOverview()
	{
		OverviewManager.toggleOverviewDialog();
	}

	void viewToggleCanvas()
	{
		new ToggleCanvasDialog(gPanel);
	}
	
	void viewPageLeft()
	{
		gPanel.pageLeft();
	}
	
	void viewPageRight()
	{
		gPanel.pageRight();
	}
}