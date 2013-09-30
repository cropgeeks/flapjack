package flapjack.gui.simmatrix;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.io.*;

import scri.commons.gui.*;

public class SimMatrixPanel extends JPanel implements AncestorListener
{
	private GTViewSet viewSet;
	private SimMatrix matrix;

	// Controls for visualization
	private SimMatrixCanvas sCanvas;
	private CanvasController controller;
	private JScrollPane sp;

	private SimMatrixPanelNB nbPanel;

	public SimMatrixPanel(GTViewSet viewSet, SimMatrix matrix)
	{
		this.viewSet = viewSet;
		this.matrix = matrix;

		createControls();

		addAncestorListener(this);
	}

	public GTViewSet getViewSet()
		{ return viewSet; }

	public SimMatrix getSimMatrix()
		{ return matrix; }

	private void createControls()
	{
		// Visualization setup
		sCanvas = new SimMatrixCanvas(this, matrix);

		sp = new JScrollPane();
		sp.setViewportView(sCanvas);
		sp.setWheelScrollingEnabled(false);

		controller = new CanvasController(this, sp);

		new SimMatrixCanvasML(this);

		nbPanel = new SimMatrixPanelNB(viewSet, matrix, sp);

		setLayout(new BorderLayout(0, 0));
		add(new TitlePanel(getTitle()), BorderLayout.NORTH);
		add(nbPanel);
	}

	public String getTitle()
	{
		int lines = matrix.getLineInfos().size();
		return RB.format("gui.simmatrix.SimMatrixPanel.title", lines, lines);
	}

	SimMatrixCanvas getSimMatrixCanvas()
		{ return sCanvas; }

	CanvasController getController()
	{
		return controller;
	}

	public void ancestorAdded(AncestorEvent event)
	{
		ProjectSerializerDB.setFromCache(matrix);
	}

	public void ancestorRemoved(AncestorEvent event)
	{
		matrix.dbClear();
	}

	public void ancestorMoved(AncestorEvent event)
	{
	}
}