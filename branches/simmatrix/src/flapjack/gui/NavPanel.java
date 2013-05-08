// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import flapjack.data.*;
import flapjack.gui.navpanel.*;
import flapjack.gui.traits.*;
import flapjack.gui.visualization.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

/**
 * Navigation panel that is responsible for control of the tree-control that
 * forms the left-hand side column of the main interface; selections of which
 * dictate what will be displayed in the main right-hand panel.
 */
class NavPanel extends JPanel
	implements TreeSelectionListener, PropertyChangeListener
{
	private JTree tree;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode root;

	private JSplitPane hSplitPane, vSplitPane;

	private NavPanelMenu menu;

	// We maintain just one GenotypePanel that is used to display any dataset
	// as it would require too much memory to assign one per dataset
	private GenotypePanel gPanel;

	NavPanel(WinMain winMain)
	{
		resetModel();

		tree = new JTree(treeModel);
		tree.addTreeSelectionListener(this);
		tree.setCellRenderer(new TreeRenderer());
		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(
			TreeSelectionModel.SINGLE_TREE_SELECTION);

		menu = new NavPanelMenu(tree);
		tree.addMouseListener(menu);

		JPanel treePanel = new JPanel(new BorderLayout());
		treePanel.add(new TitlePanel(RB.getString("gui.NavPanel.title")), BorderLayout.NORTH);
		treePanel.add(new JScrollPane(tree));

		gPanel = new GenotypePanel(winMain);

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
	}

	void setProject(Project project)
	{
		resetModel();
		tree.setModel(treeModel);

		for (int i = 0; i < project.getDataSets().size(); i++)
			addDataSetNode(project.getDataSets().get(i));

		Actions.projectSaved();
	}

	void addDataSetNode(DataSet dataSet)
	{
		// Create the nodes for the dataset's folder and its children
		DataSetNode dataSetNode = new DataSetNode(dataSet);
		treeModel.insertNodeInto(dataSetNode, root, root.getChildCount());

		addTraitsNode(dataSetNode);

		// Add child nodes for selecting the various views
		BaseNode selectedNode = null;
		for (int i = 0; i < dataSet.getViewSets().size(); i++)
		{
			BaseNode node = addVisualizationNode(dataSetNode, i);

			if (selectedNode == null)
				selectedNode = node;
		}

		// Deals with (the unusual) case of loading a project with no views
		if (selectedNode == null)
			selectedNode = dataSetNode;

		// Update the tree with the new node(s)
		tree.setSelectionPath(new TreePath(selectedNode.getPath()));
		tree.scrollPathToVisible(new TreePath(selectedNode.getPath()));
	}

	private BaseNode addTraitsNode(DataSetNode dataSetNode)
	{
		TraitsNode node = new TraitsNode(dataSetNode.getDataSet());
		treeModel.insertNodeInto(node, dataSetNode, dataSetNode.getChildCount());

		return node;
	}

	private BaseNode addVisualizationNode(DataSetNode dataSetNode, int i)
	{
		DataSet dataSet = dataSetNode.getDataSet();
		GTViewSet viewSet = dataSet.getViewSets().get(i);

		// Insert the visualization node
		VisualizationNode node = new VisualizationNode(dataSet, viewSet, gPanel);
		treeModel.insertNodeInto(node, dataSetNode, dataSetNode.getChildCount());

		// Then scan and potentially add any bookmark nodes for it
		for (Bookmark bookmark: viewSet.getBookmarks())
		{
			BookmarkNode bmNode = new BookmarkNode(gPanel, node, bookmark);
			treeModel.insertNodeInto(bmNode, node, node.getChildCount());
		}

		return node;
	}

	// Finds and adds the latest GTViewSet from a DataSet into the tree, then
	// selects it so it becomes visible too
	void addedNewVisualizationNode(DataSet dataSet)
	{
		DataSetNode node = findDataSetNode(dataSet);
		int index = dataSet.getViewSets().size() - 1;

		BaseNode newNode = addVisualizationNode(node, index);

		tree.setSelectionPath(new TreePath(newNode.getPath()));
		tree.scrollPathToVisible(new TreePath(newNode.getPath()));
	}

	void addedNewBookmarkNode(GTViewSet viewSet, Bookmark bookmark)
	{
		VisualizationNode node = findVisualizationNode(viewSet);

		BookmarkNode bmNode = new BookmarkNode(gPanel, node, bookmark);
		treeModel.insertNodeInto(bmNode, node, node.getChildCount());

		// This will expand the + for the bookmark nodes if they're not visible
		tree.setSelectionPath(new TreePath(bmNode.getPath()));
		tree.scrollPathToVisible(new TreePath(bmNode.getPath()));
	}

	void addedNewSimMatrixNode(GTViewSet viewSet, SimMatrix matrix)
	{
		VisualizationNode node = findVisualizationNode(viewSet);

		SimMatrixNode smNode = new SimMatrixNode(viewSet.getDataSet(), viewSet, matrix);
		treeModel.insertNodeInto(smNode, node, node.getChildCount());

		// This will expand the + for the SimMatrix nodes if they're not visible
		tree.setSelectionPath(new TreePath(smNode.getPath()));
		tree.scrollPathToVisible(new TreePath(smNode.getPath()));
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

	TabPanel getTraitsPanel(DataSet dataSet)
	{
		DataSetNode dataSetNode = findDataSetNode(dataSet);
		TraitsNode traitsNode = (TraitsNode) dataSetNode.getChildAt(0);

		tree.setSelectionPath(new TreePath(traitsNode.getPath()));
		tree.scrollPathToVisible(new TreePath(traitsNode.getPath()));

		return (TabPanel) traitsNode.getPanel();
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
		}
		else
			hSplitPane.setRightComponent(new IntroPanel());

		hSplitPane.setDividerLocation(location);

		// If we're viewing a visualization node, then enable the overview
		OverviewManager.setVisible(node instanceof VisualizationNode ||
			node instanceof BookmarkNode);
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

			JPanel emailPanel = new JPanel(new BorderLayout());
			emailPanel.setOpaque(false);
			emailPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			emailPanel.add(new TitlePanel3(
				RB.getString("gui.navpanel.NBStartEmailPanel.title")), BorderLayout.NORTH);
			emailPanel.add(new StartPanelEmailNB());

			JPanel huttonPanel = new JPanel(new BorderLayout());
			huttonPanel.setOpaque(false);
			huttonPanel.add(emailPanel);
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
		huttonLabel.setBorder(BorderFactory.createEmptyBorder(65, 10, 0, 10));

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
		huttonLabel.setBorder(BorderFactory.createEmptyBorder(65, 0, 0, 10));

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