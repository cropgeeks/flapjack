package flapjack.gui;

import java.awt.*;
import java.awt.event.*;
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

		gPanel = new GenotypePanel(winMain);

		vSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		vSplitPane.setResizeWeight(1);
		vSplitPane.addPropertyChangeListener(this);
		vSplitPane.setDividerLocation(Prefs.guiOverviewSplitsLocation);
		vSplitPane.setTopComponent(new JScrollPane(tree));
		vSplitPane.setBottomComponent(OverviewManager.getPanel());

		hSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		hSplitPane.addPropertyChangeListener(this);
		hSplitPane.setDividerLocation(Prefs.guiNavSplitsLocation);
		hSplitPane.setLeftComponent(vSplitPane);
		hSplitPane.setRightComponent(introPanel);

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
		dataSetNode.add(node);

		return node;
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

			setLayout(new BorderLayout());
			add(layout.getPanel());
		}
	}
}