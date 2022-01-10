// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui;

import java.awt.*;
import java.awt.dnd.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.navpanel.*;
import jhi.flapjack.gui.traits.*;
import jhi.flapjack.gui.simmatrix.*;
import jhi.flapjack.gui.visualization.*;

import scri.commons.gui.*;

/**
 * Navigation panel that is responsible for control of the tree-control that
 * forms the left-hand side column of the main interface; selections of which
 * dictate what will be displayed in the main right-hand panel.
 */
public class NavPanel extends JPanel
	implements TreeSelectionListener, PropertyChangeListener, TreeExpansionListener
{
	private JTree tree;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode root;
	private TitlePanel titlePane;

	private JSplitPane hSplitPane, vSplitPane;

	private NavPanelMenu menu;

	// We maintain just one GenotypePanel that is used to display any dataset
	// as it would require too much memory to assign one per dataset
	private GenotypePanel gPanel;
	private ChromosomePanel cPanel;

	// The default node to view on project open
	private DefaultMutableTreeNode defaultNode;
	private boolean isOpening = false;

	NavPanel(WinMain winMain)
	{
		resetModel();

		tree = new JTree(treeModel);
		tree.addTreeSelectionListener(this);
		tree.addTreeExpansionListener(this);
		tree.setCellRenderer(new TreeRenderer());
		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(
			TreeSelectionModel.SINGLE_TREE_SELECTION);

		menu = new NavPanelMenu(tree);
		tree.addMouseListener(menu);

		titlePane = new TitlePanel(RB.format("gui.NavPanel.title", 0));
		JPanel treePanel = new JPanel(new BorderLayout());
		treePanel.add(titlePane, BorderLayout.NORTH);
		treePanel.add(new JScrollPane(tree));

		gPanel = new GenotypePanel(winMain);
		cPanel = new ChromosomePanel();

		vSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		vSplitPane.setBorder(BorderFactory.createEmptyBorder());
		vSplitPane.setResizeWeight(1);
		vSplitPane.addPropertyChangeListener(this);
		vSplitPane.setDividerLocation(Prefs.guiOverviewSplitsLocation);
		vSplitPane.setTopComponent(treePanel);
		vSplitPane.setBottomComponent(OverviewManager.getPanel());

		hSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		hSplitPane.setBorder(BorderFactory.createEmptyBorder());
		hSplitPane.addPropertyChangeListener(this);
		hSplitPane.setDividerLocation(Prefs.guiNavSplitsLocation);
		hSplitPane.setLeftComponent(vSplitPane);
		hSplitPane.setRightComponent(new IntroPanel());

		FileDropAdapter dropAdapter = new FileDropAdapter(winMain);
		setDropTarget(new DropTarget(this, dropAdapter));

		setLayout(new BorderLayout());
		add(hSplitPane);
	}

	GenotypePanel getGenotypePanel()
		{ return gPanel; }

	public void propertyChange(PropertyChangeEvent e)
	{
		if (e.getSource() == hSplitPane)
			Prefs.guiNavSplitsLocation = hSplitPane.getDividerLocation();

		else if (e.getSource() == vSplitPane)
			Prefs.guiOverviewSplitsLocation = vSplitPane.getDividerLocation();
	}

	private void resetModel()
	{
		root = new DefaultMutableTreeNode("root");
		treeModel = new DefaultTreeModel(root);
		defaultNode = null;

		treeModel.addTreeModelListener(new TreeModelListener() {
			public void treeNodesChanged(TreeModelEvent e) {}
			public void treeNodesInserted(TreeModelEvent e) { updateDataSetCount(); }
			public void treeNodesRemoved(TreeModelEvent e) { updateDataSetCount(); }
			public void treeStructureChanged(TreeModelEvent e) {}
		});

		updateDataSetCount();
		updateTreeState();
	}

	void setProject(Project project)
	{
		String treeState = project.getTreeState();
		int[] selectedRows = project.getTreeSelectedRows();

		resetModel();
		tree.setModel(treeModel);

		isOpening = true;
		for (int i = 0; i < project.getDataSets().size(); i++)
			addDataSetNode(project.getDataSets().get(i));

		setExpansionState(treeState);

		isOpening = false;

		// Default selection (ideally the first viewset of the first data set)
		if (selectedRows != null)
		{
			tree.setSelectionRows(selectedRows);
			tree.scrollRowToVisible(selectedRows[0]);
		}
		else if (defaultNode != null)
		{
			tree.setSelectionPath(new TreePath(defaultNode.getPath()));
			tree.scrollPathToVisible(new TreePath(defaultNode.getPath()));
		}

		Actions.projectSaved();
	}

	private void updateDataSetCount()
	{
		if (titlePane != null)
			titlePane.setTitle(RB.format("gui.NavPanel.title", root.getChildCount()));
	}

	private void insert(DefaultMutableTreeNode node, DefaultMutableTreeNode parent, int index)
	{
		treeModel.insertNodeInto(node, parent, index);

		// Only select a node if it's being added "live", rather than at load time
		if (isOpening == false)
			tree.setSelectionPath(new TreePath(node.getPath()));

		tree.scrollPathToVisible(new TreePath(node.getPath()));
	}

	void addDataSetNode(DataSet dataSet)
	{
		// The DataSet itself
		DataSetNode dataSetNode = new DataSetNode(dataSet);
		insert(dataSetNode, root, root.getChildCount());

		// The TraitsPanel
		addTraitsNode(dataSetNode);

		// And any child GTViewSet objects
		for (GTViewSet viewSet: dataSet.getViewSets())
			addVisualizationNode(dataSet, viewSet);
	}

	private void addTraitsNode(DataSetNode dataSetNode)
	{
		TraitsNode node = new TraitsNode(dataSetNode.getDataSet());
		insert(node, dataSetNode, 0);
	}

	public void addVisualizationNode(DataSet dataSet, GTViewSet viewSet)
	{
		DataSetNode dataSetNode = findDataSetNode(dataSet);

		// The GTViewSet itself
		VisualizationNode node = new VisualizationNode(dataSet, viewSet, gPanel, cPanel);
		insert(node, dataSetNode, dataSetNode.getChildCount());

		if (defaultNode == null)
			defaultNode = node;

		// Bookmark objects
		for (Bookmark bookmark: viewSet.getBookmarks())
			addBookmarkNode(viewSet, bookmark);

		// SimMatrix objects
		for (SimMatrix matrix: viewSet.getMatrices())
			addSimMatrixNode(viewSet, matrix);

		// Dendrogram objects
		for (Dendrogram dendrogram: viewSet.getDendrograms())
			addDendogramNode(viewSet, dendrogram);

		addMabcNode(node, viewSet);
		addPedVerF1sNode(node, viewSet);
		addPedVerLinesNode(node, viewSet);
		addForwardBreedingNode(node, viewSet);
		addIfbNode(node, viewSet);
	}

	public void addBookmarkNode(GTViewSet viewSet, Bookmark bookmark)
	{
		VisualizationNode vNode = findVisualizationNode(viewSet);

		// The Bookmark itself
		BookmarkNode node = new BookmarkNode(gPanel, vNode, bookmark);
		insert(node, vNode, vNode.getChildCount());
	}

	private void addMabcNode(VisualizationNode vNode, GTViewSet viewSet)
	{
		if (containsMabcResults(viewSet))
		{
			MabcNode node = new MabcNode(gPanel, viewSet);
			insert(node, vNode, vNode.getChildCount());
		}
	}

	// Searches a view's list of lines (or its hidden list) to see if the lines
	// are holding MabcResult objects
	private boolean containsMabcResults(GTViewSet viewSet)
	{
		ArrayList<LineInfo> lines = viewSet.getLines();
		if (viewSet.getLines().isEmpty())
			lines = viewSet.getHideLines();

		if (!lines.isEmpty())
			return lines.get(0).getLineResults().getMabcResult() != null;

		return false;
	}

	public void addPedVerF1sNode(VisualizationNode vNode, GTViewSet viewSet)
	{
		if (containsPedVerF1sResults(viewSet))
		{
			PedVerF1sNode node = new PedVerF1sNode(gPanel, viewSet);
			insert(node, vNode, vNode.getChildCount());
		}
	}

	// Searches a view's list of lines (or its hidden list) to see if the lines
	// are holding MabcResult objects
	private boolean containsPedVerF1sResults(GTViewSet viewSet)
	{
		ArrayList<LineInfo> lines = viewSet.getLines();
		if (viewSet.getLines().isEmpty())
			lines = viewSet.getHideLines();

		if (!lines.isEmpty())
			return lines.get(0).getLineResults().getPedVerF1sResult() != null;

		return false;
	}

	public void addPedVerLinesNode(VisualizationNode vNode, GTViewSet viewSet)
	{
		if (containsPedVerLinesResults(viewSet))
		{
			PedVerLinesNode node = new PedVerLinesNode(gPanel, viewSet);
			insert(node, vNode, vNode.getChildCount());
		}
	}

	// Searches a view's list of lines (or its hidden list) to see if the lines
	// are holding MabcResult objects
	private boolean containsPedVerLinesResults(GTViewSet viewSet)
	{
		ArrayList<LineInfo> lines = viewSet.getLines();
		if (viewSet.getLines().isEmpty())
			lines = viewSet.getHideLines();

		if (!lines.isEmpty())
			return lines.get(0).getLineResults().getPedVerLinesResult() != null;

		return false;
	}

	public void addForwardBreedingNode(VisualizationNode vNode, GTViewSet viewSet)
	{
		if (containsForwardBreedingResults(viewSet))
		{
			FBNode node = new FBNode(gPanel, viewSet);
			insert(node, vNode, vNode.getChildCount());
		}
	}

	private boolean containsForwardBreedingResults(GTViewSet viewSet)
	{
		ArrayList<LineInfo> lines = viewSet.getLines();
		if (viewSet.getLines().isEmpty())
			lines = viewSet.getHideLines();

		if (!lines.isEmpty())
			return lines.get(0).getLineResults().getForwardBreedingResult() != null;

		return false;
	}

	public void addIfbNode(VisualizationNode vNode, GTViewSet viewSet)
	{
		if (containsIfbResults(viewSet))
		{
			IFBNode node = new IFBNode(gPanel, viewSet);
			insert(node, vNode, vNode.getChildCount());
		}
	}

	private boolean containsIfbResults(GTViewSet viewSet)
	{
		ArrayList<LineInfo> lines = viewSet.getLines();
		if (viewSet.getLines().isEmpty())
			lines = viewSet.getHideLines();

		if (!lines.isEmpty())
			return lines.get(0).getLineResults().getIFBResult() != null;

		return false;
	}

	public void addSimMatrixNode(GTViewSet viewSet, SimMatrix matrix)
	{
		VisualizationNode vNode = findVisualizationNode(viewSet);

		// The SimMatrix itself
		SimMatrixNode node = new SimMatrixNode(gPanel, viewSet, matrix);
		insert(node, vNode, vNode.getChildCount());
	}

	private void addDendogramNode(GTViewSet viewSet, Dendrogram dendrogram)
	{
		VisualizationNode vNode = findVisualizationNode(viewSet);

		// The Dendrogram itself
		DendrogramNode node = new DendrogramNode(gPanel, dendrogram);
		insert(node, vNode, vNode.getChildCount());
	}

	private DataSetNode findDataSetNode(DataSet dataSet)
	{
		// Search until we find the node for this data set
		for (int i = 0; i < root.getChildCount(); i++)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getChildAt(i);

			if (node instanceof DataSetNode)
				if (((DataSetNode)node).getDataSet() == dataSet)
					return (DataSetNode) node;
		}

		return null;
	}

	private VisualizationNode findVisualizationNode(GTViewSet viewSet)
	{
		// A viewset must be hanging from a dataset
		DataSetNode dataSetNode = findDataSetNode(viewSet.getDataSet());

		// Search until we find the node for this view set
		for (int i = 0; i < dataSetNode.getChildCount(); i++)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) dataSetNode.getChildAt(i);

			if (node instanceof VisualizationNode)
				if (((VisualizationNode)node).getViewSet() == viewSet)
					return (VisualizationNode) node;
		}

		return null;
	}

	// Returns the data set associated with the currently selected node
	public DataSet getDataSetForSelection()
	{
		BaseNode node = (BaseNode) tree.getLastSelectedPathComponent();
		return (node == null) ? null: node.getDataSet();
	}

	// Returns the view set associated with the currently selected node
	GTViewSet getViewSetForSelection()
	{
		BaseNode node = (BaseNode) tree.getLastSelectedPathComponent();

		if (node instanceof VisualizationNode)
			return ((VisualizationNode)node).getViewSet();
		else
			return null;
	}

	BaseNode getNodeForSelection()
	{
		return (BaseNode) tree.getLastSelectedPathComponent();
	}

	void updateNodeFor(DataSet dataSet)
	{
		DataSetNode node = findDataSetNode(dataSet);
		treeModel.nodeChanged(node);
	}

	void updateNodeFor(GTViewSet viewSet)
	{
		VisualizationNode node = findVisualizationNode(viewSet);
		treeModel.nodeChanged(node);
	}

	void updateNode(BaseNode node)
	{
		treeModel.nodeChanged(node);
	}

	void removeDataSetNode(DataSet dataSet)
	{
		DataSetNode node = findDataSetNode(dataSet);
		treeModel.removeNodeFromParent(node);
	}

	void removeVisualizationNode(GTViewSet viewSet)
	{
		VisualizationNode node = findVisualizationNode(viewSet);
		treeModel.removeNodeFromParent(node);
	}

	Bookmark removeSelectedBookmarkNode()
	{
		BookmarkNode node = (BookmarkNode) tree.getLastSelectedPathComponent();
		BaseNode baseNode = (BaseNode) node.getParent();

		treeModel.removeNodeFromParent(node);
		tree.setSelectionPath(new TreePath(baseNode.getPath()));
		tree.scrollPathToVisible(new TreePath(baseNode.getPath()));
		return node.getBookmark();
	}

	public TabPanel getTraitsPanel(DataSet dataSet, boolean navigateTo)
	{
		DataSetNode dataSetNode = findDataSetNode(dataSet);
		TraitsNode traitsNode = (TraitsNode) dataSetNode.getChildAt(0);

		if (navigateTo)
		{
			tree.setSelectionPath(new TreePath(traitsNode.getPath()));
			tree.scrollPathToVisible(new TreePath(traitsNode.getPath()));
		}

		return (TabPanel) traitsNode.getPanel();
	}

	SimMatrixPanel getActiveSimMatrixPanel()
	{
		return (SimMatrixPanel) hSplitPane.getRightComponent();
	}

	public void toggleGenotypePanelViews()
	{
		valueChanged(null);
	}

	public void valueChanged(TreeSelectionEvent e)
	{
		BaseNode node = (BaseNode) tree.getLastSelectedPathComponent();

		// Reset the Actions to their default state
		Actions.resetActions();

		// Hide dialogs that are too complicated to track between nodes
		Flapjack.winMain.hideDialogs();

		int location = hSplitPane.getDividerLocation();

		if (node != null)
		{
			// Display the node
			hSplitPane.setRightComponent(node.getPanel());

			// Enable the appropriate actions for it
			node.setActions();

			Flapjack.winMain.getProject().setTreeSelectedRows(tree.getSelectionRows());
		}
		else
			hSplitPane.setRightComponent(new IntroPanel());

		hSplitPane.setDividerLocation(location);

		// If we're viewing a visualization node, then enable the overview
		OverviewManager.setVisible(node instanceof VisualizationNode ||
			node instanceof BookmarkNode);
	}

	@Override
	public void treeExpanded(TreeExpansionEvent event)
	{
		updateTreeState();
	}

	@Override
	public void treeCollapsed(TreeExpansionEvent event)
	{
		updateTreeState();
	}

	// Updates our tracking of the tree expansion state
	private void updateTreeState()
	{
		if (tree != null)
		{
			StringBuilder sb = new StringBuilder();

			// For each row that is expanded add its index to the string
			for (int i = 0; i < tree.getRowCount(); i++)
				if (tree.isExpanded(i))
					sb.append(i).append(",");

			Flapjack.winMain.getProject().setTreeState(sb.toString());
			// The tree selection state can also change on tree expansion
			Flapjack.winMain.getProject().setTreeSelectedRows(tree.getSelectionRows());
		}
	}

	// Takes a string representing an expansion state for the tree and updates the tree to match that state
	public void setExpansionState(String s)
	{
		// If we have no information on the tree state, do nothing
		if (s.isEmpty())
			return;

		// The string is a list of numbers separated by commas, so we split on commas
		String[] indexes = s.split(",");

		// Temporarily remove the expansion listener so it doesn't respond to events
		tree.removeTreeExpansionListener(this);

		// Collapse all rows in the tree
		for (int i = tree.getRowCount() - 1; i >= 0; i--)
			tree.collapseRow(i);

		// Expand the rows found in the string passed to the method
		for ( String st : indexes )
		{
			if (!st.isEmpty())
			{
				int row = Integer.parseInt(st);
				tree.expandRow(row);
			}
		}

		// Save this reloaded tree state
		updateTreeState();

		// Add the listener again so we can update the tree state as appropriate
		tree.addTreeExpansionListener(this);
	}

	public void selectDataSetNode(DataSet dataSet)
	{
		DataSetNode node = this.findDataSetNode(dataSet);

		tree.setSelectionPath(new TreePath(node.getPath()));
		tree.scrollPathToVisible(new TreePath(node.getPath()));
	}

	private JComponent setAndScrollTo(BaseNode node)
	{
		tree.setSelectionPath(new TreePath(node.getPath()));
		tree.scrollPathToVisible(new TreePath(node.getPath()));

		return node.getPanel();
	}

	public JComponent selectMabcNode(GTViewSet viewSet)
	{
		VisualizationNode vNode = findVisualizationNode(viewSet);

		MabcNode pNode = null;
		for (int i = 0; i < vNode.getChildCount(); i++)
		{
			if (vNode.getChildAt(i) instanceof MabcNode)
				pNode = (MabcNode) vNode.getChildAt(i);
		}

		return setAndScrollTo(pNode);
	}

	public JComponent selectPedVerF1sNode(GTViewSet viewSet)
	{
		VisualizationNode vNode = findVisualizationNode(viewSet);

		PedVerF1sNode pNode = null;
		for (int i = 0; i < vNode.getChildCount(); i++)
		{
			if (vNode.getChildAt(i) instanceof PedVerF1sNode)
				pNode = (PedVerF1sNode) vNode.getChildAt(i);
		}

		return setAndScrollTo(pNode);
	}

	public JComponent selectPedVerLinesNode(GTViewSet viewSet)
	{
		VisualizationNode vNode = findVisualizationNode(viewSet);

		PedVerLinesNode pNode = null;
		for (int i = 0; i < vNode.getChildCount(); i++)
		{
			if (vNode.getChildAt(i) instanceof PedVerLinesNode)
				pNode = (PedVerLinesNode) vNode.getChildAt(i);
		}

		return setAndScrollTo(pNode);
	}

	public JComponent selectForwardBreedingNode(GTViewSet viewSet)
	{
		VisualizationNode vNode = findVisualizationNode(viewSet);

		FBNode pNode = null;
		for (int i = 0; i < vNode.getChildCount(); i++)
		{
			if (vNode.getChildAt(i) instanceof FBNode)
				pNode = (FBNode) vNode.getChildAt(i);
		}

		return setAndScrollTo(pNode);
	}

	public JComponent selectIFBNode(GTViewSet viewSet)
	{
		VisualizationNode vNode = findVisualizationNode(viewSet);

		IFBNode pNode = null;
		for (int i = 0; i < vNode.getChildCount(); i++)
		{
			if (vNode.getChildAt(i) instanceof IFBNode)
				pNode = (IFBNode) vNode.getChildAt(i);
		}

		return setAndScrollTo(pNode);
	}
}