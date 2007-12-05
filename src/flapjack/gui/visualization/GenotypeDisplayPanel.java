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
	RowPanel rowPanel;
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

		JPanel p1 = new JPanel();
		p1.add(sizeSlider);

		JPanel p2 = new JPanel(new BorderLayout());
		p2.add(rowPanel);
		p2.add(p1, BorderLayout.SOUTH);

		add(p2, BorderLayout.SOUTH);
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
		rowPanel = new RowPanel(canvas);

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

		rowPanel.repaint();
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

		rowPanel.computeDimensions(listPanel.getWidth(), vBar.isVisible() ? vBar.getWidth() : 0);

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

		rowPanel.updateOverviewSelectionBox(xIndex, xW);
	}

	void jumpToPosition(int xIndex, int yIndex)
	{
		int x = xIndex * canvas.boxW - (canvas.boxW);
		int y = yIndex * canvas.boxH - (canvas.boxH);

		hBar.setValue(x);
		vBar.setValue(y);
	}

	void overRow(int colIndex, int rowIndex)
	{
		GenotypeData data = null;

		try
		{
			Line line = dataSet.getLineByIndex(rowIndex);
			data = line.getGenotypeDataByMap(map);
		}
		catch (ArrayIndexOutOfBoundsException e) {}

		rowPanel.setGenotypeData(data);
	}
}