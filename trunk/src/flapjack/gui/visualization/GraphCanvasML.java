// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

class GraphCanvasML extends MouseInputAdapter implements ActionListener
{
	private NumberFormat nf = NumberFormat.getInstance();

	private GenotypePanel gPanel;
	private GraphCanvas graphCanvas;
	private GenotypeCanvas canvas;

	private static JMenuItem mDataSelectGraph;
	private static JCheckBoxMenuItem mHistogram;
	private static JCheckBoxMenuItem mLineGraph;

	GraphCanvasML(GenotypePanel gPanel, GraphCanvas graphCanvas)
	{
		this.gPanel = gPanel;
		this.graphCanvas = graphCanvas;
		canvas = gPanel.canvas;

		graphCanvas.addMouseListener(this);
		graphCanvas.addMouseMotionListener(this);

		mDataSelectGraph = WinMainMenuBar.getItem(Actions.dataSelectGraph, "gui.Actions.dataSelectGraph", 0, 0);
		mHistogram = new JCheckBoxMenuItem(RB.getString("gui.GraphCanvasML.mHistogram"));
		mHistogram.addActionListener(this);
		mLineGraph = new JCheckBoxMenuItem(RB.getString("gui.GraphCanvasML.mLineGraph"));
		mLineGraph.addActionListener(this);

		ButtonGroup grp = new ButtonGroup();
		grp.add(mHistogram);
		grp.add(mLineGraph);

		if (Prefs.guiGraphStyle == 0)
			mHistogram.setSelected(true);
		else if (Prefs.guiGraphStyle == 1)
			mLineGraph.setSelected(true);
	}

	public void mouseReleased(MouseEvent e)
	{
		if (e.isPopupTrigger())
			displayMenu(e);
	}

	public void mousePressed(MouseEvent e)
	{
		if (e.isPopupTrigger())
			displayMenu(e);
	}

	private void displayMenu(MouseEvent e)
	{
		JPopupMenu menu = new JPopupMenu();

		menu.add(mDataSelectGraph);
		menu.addSeparator();
		menu.add(mHistogram);
		menu.add(mLineGraph);

		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == mHistogram)
		{
			Prefs.guiGraphStyle = 0;
			gPanel.refreshView();
		}
		else if (e.getSource() == mLineGraph)
		{
			Prefs.guiGraphStyle = 1;
			gPanel.refreshView();
		}
	}

	public void mouseEntered(MouseEvent e)
	{
		gPanel.statusPanel.setForGraphUse();
	}

	public void mouseExited(MouseEvent e)
	{
		gPanel.statusPanel.setGraphDetails(null, null, null);
		gPanel.statusPanel.setForMainUse();
	}

	public void mouseMoved(MouseEvent e)
	{
		GraphData data = graphCanvas.graphData;

		if (data == null)
			return;


		// "x" is the mouse's position if it were over the main canvas
		int x = e.getPoint().x + canvas.pX1 - graphCanvas.xOffset;

		if (x >= canvas.pX1 && x <= canvas.pX2)
		{
			int mIndex = canvas.getMarker(new Point(x, 0));

			// Find out which marker is being displayed at this location
			MarkerInfo mi = canvas.view.getMarkerInfo(mIndex);
			// And what its graph value is
			float value = data.getRealValueAt(mi.getIndex());

			// Display on screen.
			String graph = data.getName();

			// Ignore it if it's a dummy marker
			if (mi.dummyMarker())
			{
				gPanel.statusPanel.setGraphDetails(graph, " ", " ");
				return;
			}




			Marker m = mi.getMarker();
			String mStr = m.getName() + "  (" + nf.format(m.getRealPosition()) + ")";
			String vStr = nf.format(value);
			if (data.getHasThreshold())
				vStr += "  (" + nf.format(data.getThreshold()) + ")";

			gPanel.statusPanel.setGraphDetails(graph, mStr, vStr);


			// Finally, force the other displays to "track" this marker too
			gPanel.mapCanvas.setMarkerIndex(mIndex);
			gPanel.canvas.setHighlightedIndices(-1, mIndex);
		}
		else
			gPanel.statusPanel.setGraphDetails(null, null, null);
	}
}