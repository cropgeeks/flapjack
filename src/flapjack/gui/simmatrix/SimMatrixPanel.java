// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.simmatrix;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

public class SimMatrixPanel extends JPanel
{
	private GTViewSet viewSet;

	private JTabbedPane tabs;

	// Controls for visualization
	private SimMatrixCanvas sCanvas;
	private CanvasController controller;
	private JScrollPane sp;

	// Controls for table
	private SimMatrixPanelNB tablePanel;
	private JTable table;
	private SimMatrixTableModel model;

	public SimMatrixPanel(GTViewSet viewSet)
	{
		this.viewSet = viewSet;

		createControls();
	}

	private void createControls()
	{
		// Visualization setup
		sCanvas = new SimMatrixCanvas(this, viewSet);

		sp = new JScrollPane();
		sp.setViewportView(sCanvas);
		sp.setWheelScrollingEnabled(false);

		controller = new CanvasController(this, sp);

		new SimMatrixCanvasML(this);


		// Table setup
		tablePanel = new SimMatrixPanelNB(viewSet);

		model = new SimMatrixTableModel(viewSet.lineScores);

		table = tablePanel.table;
		table.setModel(model);


		// Tabs setup
		tabs = new JTabbedPane();
		tabs.addTab("Visual", sp);
		tabs.addTab("Data", tablePanel);

		setLayout(new BorderLayout(0, 0));
		add(new TitlePanel("Similarity Matrix"), BorderLayout.NORTH);
		add(tabs);
	}

	SimMatrixCanvas getSimMatrixCanvas()
		{ return sCanvas; }

	CanvasController getController()
	{
		return controller;
	}
}