// Copyright 2007-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

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

	GraphCanvasML(GenotypePanel gPanel, GraphCanvas graphCanvas)
	{
		this.gPanel = gPanel;
		this.graphCanvas = graphCanvas;
		canvas = gPanel.canvas;

		graphCanvas.addMouseListener(this);
		graphCanvas.addMouseMotionListener(this);
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
	}

	public void actionPerformed(ActionEvent e)
	{
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
		if (graphCanvas.graphData == null)
			return;

		// "x" is the mouse's position if it were over the main canvas
		int x = e.getPoint().x + canvas.pX1 - graphCanvas.xOffset;

		if (x >= canvas.pX1 && x <= canvas.pX2)
		{
			int mIndex = canvas.getMarker(new Point(x, 0));
			int gIndex = graphCanvas.graphIndex;

			float[] data = graphCanvas.graphData.getGraphs().get(gIndex);
			float min = graphCanvas.graphData.getMins().get(gIndex);
			float max = graphCanvas.graphData.getMaxs().get(gIndex);

			// Find out which marker is being displayed at this location
			MarkerInfo mi = canvas.view.getMarkerInfo(mIndex);

			// Then we can get the value for it from the graph data
			float value = data[mi.getIndex()];
			// And "Unnormalize" it back to its original value
			value = (value * (max-min)) + min;


			// Display on screen.
			String graph = graphCanvas.graphData.getNames().get(gIndex);

			Marker m = mi.getMarker();
			String mStr = m.getName() + "  (" + nf.format(m.getRealPosition()) + ")";
			String vStr = nf.format(value);

			gPanel.statusPanel.setGraphDetails(graph, mStr, vStr);


			// Finally, force the other displays to "track" this marker too
			gPanel.mapCanvas.setMarkerIndex(mIndex);
			gPanel.canvas.setHighlightedIndices(-1, mIndex);
		}
	}
}