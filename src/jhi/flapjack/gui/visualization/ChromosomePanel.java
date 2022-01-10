// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import java.awt.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class ChromosomePanel extends JPanel
{
	private GTViewSet viewSet;

	private JScrollPane sp;
	private ChromosomeCanvas canvas;

	private ChromosomeCanvasGraph graph;
	private ChromosomeCanvasKey key;

	public ChromosomePanel()
	{
		canvas = new ChromosomeCanvas();
		sp = new JScrollPane(canvas);
		sp.setBorder(BorderFactory.createEmptyBorder());
		sp.getVerticalScrollBar().setUnitIncrement(10);
		sp.getVerticalScrollBar().setBlockIncrement(10);

		graph = new ChromosomeCanvasGraph(canvas);
//		graph.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
		key = new ChromosomeCanvasKey(canvas);
		key.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

		canvas.setHelpers(key, graph);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(key);
		panel.add(graph, BorderLayout.SOUTH);

		setLayout(new BorderLayout());
		add(new TitlePanel(
			RB.getString("flapjack.gui.visualization.ChromosomePanel.title")),
			BorderLayout.NORTH);
		add(sp);
		add(panel, BorderLayout.SOUTH);
	}

	public void setViewSet(GTViewSet viewSet)
	{
		this.viewSet = viewSet;

		canvas.setView(viewSet);
		graph.display(-1);
	}
}