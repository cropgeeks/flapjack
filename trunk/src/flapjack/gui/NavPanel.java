package flapjack.gui;

import java.awt.*;
import java.awt.dnd.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import flapjack.data.*;
import flapjack.gui.navpanel.*;
import flapjack.gui.visualization.*;

import scri.commons.gui.*;

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
	private IntroPanel introPanel = new IntroPanel();

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
		vSplitPane.setResizeWeight(1);
		vSplitPane.addPropertyChangeListener(this);
		vSplitPane.setDividerLocation(Prefs.guiOverviewSplitsLocation);
		vSplitPane.setTopComponent(treePanel);
		vSplitPane.setBottomComponent(OverviewManager.getPanel());

		hSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		hSplitPane.addPropertyChangeListener(this);
		hSplitPane.setDividerLocation(Prefs.guiNavSplitsLocation);
		hSplitPane.setLeftComponent(vSplitPane);
		hSplitPane.setRightComponent(introPanel);

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

		// Add child nodes for selecting the various views
		BaseNode selectedNode = null;
		for (int i = 0; i < dataSet.getViewSets().size(); i++)
		{
			BaseNode node = addVisualizationNode(dataSetNode, i);

			if (selectedNode == null)
				selectedNode = node;
		}

		// Update the tree with the new node(s)
		tree.setSelectionPath(new TreePath(selectedNode.getPath()));
		tree.scrollPathToVisible(new TreePath(selectedNode.getPath()));
	}

	private BaseNode addVisualizationNode(DataSetNode dataSetNode, int i)
	{
		DataSet dataSet = dataSetNode.getDataSet();
		GTViewSet viewSet = dataSet.getViewSets().get(i);

		VisualizationNode node = new VisualizationNode(dataSet, viewSet, gPanel);
		treeModel.insertNodeInto(node, dataSetNode, dataSetNode.getChildCount());

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
		return node.getDataSet();
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
		System.out.println("node is: " + node);
		treeModel.removeNodeFromParent(node);
	}

	public void valueChanged(TreeSelectionEvent e)
	{
		BaseNode node = (BaseNode) tree.getLastSelectedPathComponent();

		// Reset the Actions to their default state
		Actions.resetActions();

		int location = hSplitPane.getDividerLocation();

		if (node != null)
		{
			// Display the node
			hSplitPane.setRightComponent(node.getPanel());

			// Enable the appropriate actions for it
			node.setActions();
		}
		else
			hSplitPane.setRightComponent(introPanel);

		hSplitPane.setDividerLocation(location);

		// If we're viewing a visualization node, then enable the overview
		OverviewManager.setVisible(node instanceof VisualizationNode);
	}

	/**
	 * Panel used for display when no other tree components have been selected.
	 */
	private static class IntroPanel extends JPanel
	{
		IntroPanel()
		{
			DoeLayout layout = new DoeLayout();
			layout.getPanel().setBackground(Color.white);

			layout.add(new JLabel(Icons.GERMINATE, JLabel.CENTER),
				0, 0, 1, 1, new Insets(5, 5, 5, 5));
			layout.add(new JLabel(RB.getString("gui.NavPanel.emptyPanel"),
				JLabel.CENTER), 0, 1, 1, 1, new Insets(5, 5, 5, 5));
			layout.add(new JLabel(RB.getString("gui.NavPanel.introLabel"),
				JLabel.CENTER), 0, 2, 1, 1, new Insets(15, 5, 5, 5));

			setLayout(new BorderLayout());
			add(layout.getPanel());
		}
	}
}