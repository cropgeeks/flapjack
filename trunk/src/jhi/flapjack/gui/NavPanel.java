// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.beans.*;
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
import scri.commons.gui.matisse.*;

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
		UIScaler.setCellHeight(tree);

		menu = new NavPanelMenu(tree);
		tree.addMouseListener(menu);

		JPanel treePanel = new JPanel(new BorderLayout());
		treePanel.add(new TitlePanel(RB.getString("gui.NavPanel.title")), BorderLayout.NORTH);
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

	void insert(DefaultMutableTreeNode node, DefaultMutableTreeNode parent, int index)
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
	}

	public void addBookmarkNode(GTViewSet viewSet, Bookmark bookmark)
	{
		VisualizationNode vNode = findVisualizationNode(viewSet);

		// The Bookmark itself
		BookmarkNode node = new BookmarkNode(gPanel, vNode, bookmark);
		insert(node, vNode, vNode.getChildCount());
	}

	public void addMabcNode(GTViewSet viewSet)
	{
		VisualizationNode vNode = findVisualizationNode(viewSet);

		MabcNode node = new MabcNode(viewSet.getDataSet(), viewSet);
		insert(node, vNode, vNode.getChildCount());
	}

	public void addPedVerNode(GTViewSet viewSet)
	{
		VisualizationNode vNode = findVisualizationNode(viewSet);

		PedVerNode node = new PedVerNode(viewSet.getDataSet(), viewSet);
		insert(node, vNode, vNode.getChildCount());
	}

	public void addSimMatrixNode(GTViewSet viewSet, SimMatrix matrix)
	{
		VisualizationNode vNode = findVisualizationNode(viewSet);

		// The SimMatrix itself
		SimMatrixNode node = new SimMatrixNode(viewSet.getDataSet(), viewSet, matrix);
		insert(node, vNode, vNode.getChildCount());
	}

	void addDendogramNode(GTViewSet viewSet, Dendrogram dendrogram)
	{
		VisualizationNode vNode = findVisualizationNode(viewSet);

		// The Dendrogram itself
		DendrogramNode node = new DendrogramNode(viewSet.getDataSet(), dendrogram);
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

	public PedVerNode getPedVerNode(GTViewSet viewSet)
	{
		VisualizationNode node = findVisualizationNode(viewSet);

		if (node != null)
		{
			for (int i = 0; i < node.getChildCount(); i++)
			{
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);

				if (child instanceof PedVerNode)
					return (PedVerNode) child;
			}
		}
		return null;
	}

	// Returns the data set associated with the currently selected node
	DataSet getDataSetForSelection()
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

	public TabPanel getTraitsPanel(DataSet dataSet)
	{
		DataSetNode dataSetNode = findDataSetNode(dataSet);
		TraitsNode traitsNode = (TraitsNode) dataSetNode.getChildAt(0);

		tree.setSelectionPath(new TreePath(traitsNode.getPath()));
		tree.scrollPathToVisible(new TreePath(traitsNode.getPath()));

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

	/**
	 * Panel used for display when no other tree components have been selected.
	 */
	private class IntroPanel extends JPanel
	{
		IntroPanel()
		{
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createLineBorder(new Color(119, 126, 143), 3));

			JPanel panel = new LogoPanel(new BorderLayout(0, 0));

			JPanel welcomePanel = new JPanel(new BorderLayout());
			welcomePanel.setOpaque(false);
			welcomePanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 8, 2));
			welcomePanel.add(new TitlePanel3(
				RB.getString("gui.navpanel.NBStartWelcomePanel.title")), BorderLayout.NORTH);
			welcomePanel.add(new StartPanelWelcomeNB());

			JPanel filePanel = new JPanel(new BorderLayout());
			filePanel.setOpaque(false);
			filePanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			filePanel.add(new TitlePanel3(
				RB.getString("gui.navpanel.NBStartFilePanel.title")), BorderLayout.NORTH);
			filePanel.add(new StartPanelFileNB());

			JPanel helpPanel = new JPanel(new BorderLayout());
			helpPanel.setOpaque(false);
			helpPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			helpPanel.add(new TitlePanel3(
				RB.getString("gui.navpanel.NBStartHelpPanel.title")), BorderLayout.NORTH);
			helpPanel.add(new StartPanelHelpNB());

			JPanel citationPanel = new JPanel(new BorderLayout());
			citationPanel.setOpaque(false);
			citationPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			citationPanel.add(new TitlePanel3(
				RB.getString("gui.navpanel.StartPanelPublicationNB.title")), BorderLayout.NORTH);
			citationPanel.add(new StartPanelPublicationNB());

			JPanel huttonPanel = new JPanel(new BorderLayout());
			huttonPanel.setOpaque(false);
			huttonPanel.add(citationPanel);
			JPanel logoPanel = new JPanel(new BorderLayout());
			logoPanel.setOpaque(false);
			logoPanel.add(getHuttonLabel(), BorderLayout.WEST);
			logoPanel.add(getCIMMYTLabel(), BorderLayout.EAST);
			huttonPanel.add(logoPanel, BorderLayout.EAST);


			JPanel centrePanel = new JPanel(new GridLayout(1, 2, 0, 0));
			centrePanel.setOpaque(false);
			centrePanel.add(filePanel);
			centrePanel.add(helpPanel);

			panel.add(welcomePanel, BorderLayout.NORTH);
			panel.add(centrePanel, BorderLayout.CENTER);
			panel.add(huttonPanel, BorderLayout.SOUTH);

//			add(new JScrollPane(panel));
			add(panel);
		}
	}

	private static JLabel getHuttonLabel()
	{
		HyperLinkLabel huttonLabel = new HyperLinkLabel();
		huttonLabel.setIcon(Icons.getIcon("HUTTON"));
		huttonLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

		huttonLabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FlapjackUtils.visitURL("http://www.hutton.ac.uk");
			}
		});

		return huttonLabel;
	}

	private static JLabel getCIMMYTLabel()
	{
		HyperLinkLabel huttonLabel = new HyperLinkLabel();
		huttonLabel.setIcon(Icons.getIcon("MASAGRO"));
		huttonLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

		huttonLabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FlapjackUtils.visitURL("http://masagro.cimmyt.org");
			}
		});

		return huttonLabel;
	}

	private static class LogoPanel extends JPanel
	{
		private static ImageIcon logo = Icons.getIcon("HUTTONLARGE");

		LogoPanel(LayoutManager lm)
		{
			super(lm);
			setBackground(Color.white);
		}

		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);

			Graphics2D g = (Graphics2D) graphics;

			int w = getWidth();
			int h = getHeight();

			g.drawImage(logo.getImage(), 0, 0, w, h, null);
		}
	}
}