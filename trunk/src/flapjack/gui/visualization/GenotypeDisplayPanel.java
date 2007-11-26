package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.data.*;

public class GenotypeDisplayPanel extends JPanel
	implements AdjustmentListener, ChangeListener, MouseWheelListener
{
	private DataSet dataSet;
	private ChromosomeMap map;

	private JScrollPane sp;
	private JScrollBar hBar, vBar;
	private JViewport view;

	ListPanel listPanel;
	GenotypeCanvas canvas;
	OverviewDialog overviewDialog;

	private JSlider sizeSlider;

	static int BS = 10;

	public GenotypeDisplayPanel()
	{
		try
		{
			dataSet = flapjack.gui.WinMain.getDataSet();
			map = dataSet.getMapByIndex(0);
		}
		catch (Exception e) {}

		createControls();

		setBackground(Color.white);

		setLayout(new BorderLayout());
		add(sp);

		JPanel panel = new JPanel();
		panel.add(sizeSlider);
		add(panel, BorderLayout.SOUTH);
	}

	private void createControls()
	{
		sp = new JScrollPane();
		sp.addMouseWheelListener(this);
		view = sp.getViewport();
		hBar = sp.getHorizontalScrollBar();
		vBar = sp.getVerticalScrollBar();
		hBar.addAdjustmentListener(this);
		vBar.addAdjustmentListener(this);

		listPanel = new ListPanel(dataSet);
		canvas = new GenotypeCanvas(this, dataSet, map);

		sp.setRowHeaderView(listPanel);
		sp.setViewportView(canvas);
		sp.getViewport().setBackground(Color.white);

		sizeSlider = new JSlider(1, 40, 11);
		sizeSlider.addChangeListener(this);
		stateChanged(null);
	}

	public void setOverviewDialog(OverviewDialog overviewDialog)
	{
		this.overviewDialog = overviewDialog;
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

	public void stateChanged(ChangeEvent e)
	{
		int size = sizeSlider.getValue();

		System.out.println("slider size = " + size);

		listPanel.computeDimensions(size);
		canvas.computeDimensions(size);

		repaint();
	}

	public void mouseWheelMoved(MouseWheelEvent e)
	{
		if (e.isControlDown())
		{
			sizeSlider.setValue(sizeSlider.getValue() + e.getWheelRotation());
		}
	}

	DataSet getDataSet()
	{
		return dataSet;
	}

	ChromosomeMap getMap()
	{
		return map;
	}

	void forceOverviewUpdate()
	{
		canvas.updateOverviewSelectionBox();
	}

	void updateOverviewSelectionBox(int xIndex, int xW, int yIndex, int yH)
	{
		if (overviewDialog != null)
			overviewDialog.updateOverviewSelectionBox(xIndex, xW, yIndex, yH);
	}

	void jumpToPosition(int xIndex, int yIndex)
	{
		int x = xIndex * canvas.boxW - (canvas.boxW);
		int y = yIndex * canvas.boxH - (canvas.boxH);

		hBar.setValue(x);
		vBar.setValue(y);
	}
}