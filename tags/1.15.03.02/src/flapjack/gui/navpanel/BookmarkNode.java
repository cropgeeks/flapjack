// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.navpanel;

import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.gui.visualization.*;

public class BookmarkNode extends BaseNode
{
	private GenotypePanel gPanel;
	private VisualizationNode parent;

	private Bookmark bookmark;
	private GTViewSet viewSet;
	private String name;

	public BookmarkNode(GenotypePanel gPanel, VisualizationNode parent, Bookmark bookmark)
	{
		super(parent.getDataSet());

		this.gPanel = gPanel;
		this.parent = parent;
		this.bookmark = bookmark;

		viewSet = parent.getViewSet();

		// Work out a sensible name for the bookmark
		name = bookmark.getChromosome().getName()
			+ ": " + bookmark.getLine().getName()
			+ " / " + bookmark.getMarker().getName();
	}

	public Bookmark getBookmark()
		{ return bookmark; }

	public GTViewSet getViewSet()
		{ return parent.getViewSet(); }

	public String toString()
	{
		return name;
	}

	public void setActions()
	{
		// Most of the actions will be identical to the main visualization node
		parent.setActions();

		Actions.viewRenameView.setEnabled(false);
		Actions.viewDeleteView.setEnabled(false);
		Actions.viewDeleteBookmark.setEnabled(true);
	}

	public JPanel getPanel()
	{
		// Ensure the correct chromosome is selected for this bookmark
		int index = viewSet.indexof(viewSet.getView(bookmark.getChromosome()));
		viewSet.setViewIndex(index);

		GTView view = viewSet.getView(index);

		// Work out the current indices of the line and marker
		int lineIndex = viewSet.indexOf(bookmark.getLine());
		int mrkrIndex = view.indexOf(bookmark.getMarker());

		new BookmarkHighlighter(gPanel, view, lineIndex, mrkrIndex);

		return parent.getPanel();
	}
}