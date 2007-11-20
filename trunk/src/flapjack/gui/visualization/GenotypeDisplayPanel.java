package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.data.*;

public class GenotypeDisplayPanel extends JPanel implements AdjustmentListener
{
	private DataSet dataSet;
	private ChromosomeMap map;

	private JScrollPane sp;
	private JScrollBar hBar, vBar;
	private JViewport view;

	private ListPanel listPanel;
	private GenotypeCanvas canvas;

	public GenotypeDisplayPanel()
	{
		try
		{
			dataSet = flapjack.gui.WinMain.getDataSet();
			map = dataSet.getMapByIndex(0);
		}
		catch (Exception e) {}

		createControls();


		setLayout(new BorderLayout());
		add(sp);
	}

	private void createControls()
	{
		sp = new JScrollPane();
		view = sp.getViewport();
		hBar = sp.getHorizontalScrollBar();
		vBar = sp.getVerticalScrollBar();
		hBar.addAdjustmentListener(this);
		vBar.addAdjustmentListener(this);

		listPanel = new ListPanel(dataSet);
		canvas = new GenotypeCanvas(this, dataSet, map);

		sp.setRowHeaderView(listPanel);
		sp.setViewportView(canvas);
	}

	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		// Each time the scollbars are moved, the canvas must be redrawn, with
		// the new dimensions of the canvas being passed to it (window size
		// changes will cause scrollbar movement events)
		canvas.computeForRedraw(view.getExtentSize(), view.getViewPosition());
	}

	void computeScrollbarAdjustmentValues(int xIncrement, int yIncrement)
	{
		hBar.setUnitIncrement(xIncrement);
		hBar.setBlockIncrement(xIncrement);
		vBar.setUnitIncrement(yIncrement);
		vBar.setBlockIncrement(yIncrement);
	}
}