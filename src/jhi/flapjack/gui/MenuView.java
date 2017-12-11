// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui;

import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.dialog.*;
import jhi.flapjack.gui.navpanel.*;
import jhi.flapjack.gui.visualization.*;

import scri.commons.gui.*;

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
			navPanel.addVisualizationNode(dataSet, newViewSet);

			Actions.projectModified();
		}
	}

	void viewRenameView()
	{
		BaseNode node = navPanel.getNodeForSelection();

		if (node instanceof VisualizationNode)
		{
			GTViewSet viewSet = ((VisualizationNode) node).getViewSet();

			RenameDialog dialog = new RenameDialog(viewSet.getName());

			if (dialog.isOK())
			{
				viewSet.setName(dialog.getNewName());
				navPanel.updateNodeFor(viewSet);

				Actions.projectModified();
			}
		}
		else if (node instanceof VisualizationChildNode)
		{
			VisualizationChildNode vNode = (VisualizationChildNode) node;
			RenameDialog dialog = new RenameDialog(vNode.getName());

			if (dialog.isOK())
			{
				// Grab the new name for our node from the dialog
				String newName = dialog.getNewName();

				// Set that on the node itself
				vNode.setName(newName);

				// Then set the name on the actual object so that it comes back
				// from a project save correctly
				if (vNode instanceof MabcNode || vNode instanceof PedVerF1sNode
					|| vNode instanceof PedVerLinesNode)
				{
					GTViewSet viewSet = vNode.getViewSet();
					// In the case of any of our analyses we'll have to set the
					// name on each line's LineResults entry
					viewSet.getLines().forEach(line -> line.getResults().setName(newName));
				}
				else if (vNode instanceof SimMatrixNode)
				{
					SimMatrixNode sNode = (SimMatrixNode)vNode;
					sNode.getMatrix().setTitle(newName);
				}
				else if (vNode instanceof  DendrogramNode)
				{
					DendrogramNode dNode = (DendrogramNode)vNode;
					dNode.getDendrogram().setTitle(newName);
				}
				navPanel.updateNode(vNode);

				Actions.projectModified();
			}
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
		navPanel.addBookmarkNode(view.getViewSet(), bookmark);

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
		gPanel.getController().pageLeft();
	}

	void viewPageRight()
	{
		gPanel.getController().pageRight();
	}

	void viewGenotypesOrChromosomes(boolean showChromosomes)
	{
		Prefs.visShowChromosomes = showChromosomes;
		Actions.viewGenotypes.putValue(Action.SELECTED_KEY, !Prefs.visShowChromosomes);
		Actions.viewChromosomes.putValue(Action.SELECTED_KEY, Prefs.visShowChromosomes);

		navPanel.toggleGenotypePanelViews();
	}
}