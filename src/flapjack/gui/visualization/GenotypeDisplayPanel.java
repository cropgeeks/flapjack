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

	public static int mapIndex = 0;

	private JScrollPane sp;
	private JScrollBar hBar, vBar;
	private JViewport view;

	ListPanel listPanel;
	GenotypeCanvas canvas;
	MapCanvas mapCanvas;
	RowPanel rowPanel;
	ColPanel colPanel;
	OverviewDialog overviewDialog;

	private JSlider sizeSlider;


	public GenotypeDisplayPanel()
	{
		try
		{
			dataSet = flapjack.gui.WinMain.getDataSet();
			map = dataSet.getMapByIndex(mapIndex);
		}
		catch (Exception e) {}

		createControls();

		setBackground(Color.white);

		setLayout(new BorderLayout());

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(sp);
		centerPanel.add(mapCanvas, BorderLayout.NORTH);
		centerPanel.add(rowPanel, BorderLayout.SOUTH);
		centerPanel.add(colPanel, BorderLayout.EAST);
		add(centerPanel);

		JPanel p1 = new JPanel();
		p1.add(sizeSlider);

//		JPanel p2 = new JPanel(new BorderLayout());
//		p2.add(rowPanel);
//		p2.add(p1, BorderLayout.SOUTH);

//		add(p2, BorderLayout.SOUTH);
		add(p1, BorderLayout.SOUTH);
//		add(colPanel, BorderLayout.EAST);
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
		colPanel = new ColPanel(canvas);
		mapCanvas = new MapCanvas(canvas);
		mapCanvas.setChromosomeMap(map);

		sp.setRowHeaderView(listPanel);
		sp.setViewportView(canvas);
		sp.getViewport().setBackground(Color.white);

		sizeSlider = new JSlider(1, 25, 11);
		sizeSlider.addChangeListener(this);

		listPanel.computeDimensions(11);
		canvas.computeDimensions(11);

		stateChanged(null);

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				computeRowColSizes();
			}
		});
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
		colPanel.repaint();
		mapCanvas.repaint();
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

		validate();

		computeRowColSizes();

		repaint();
	}

	void computeRowColSizes()
	{
		int cWidth = colPanel.getWidth();

		System.out.println("cWidth = " + cWidth);

		rowPanel.computeDimensions(listPanel.getWidth(), vBar.isVisible() ? (cWidth+vBar.getWidth()) : cWidth);
		colPanel.computeDimensions(listPanel.getHeight(), hBar.isVisible() ? hBar.getHeight() : 0);

		mapCanvas.computeDimensions(listPanel.getWidth(), vBar.isVisible() ? (cWidth+vBar.getWidth()) : cWidth);
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
		colPanel.updateOverviewSelectionBox(yIndex, yH);

		mapCanvas.updateLociIndices(xIndex, xIndex+xW-1);
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
		colPanel.setLociIndex(colIndex);
		mapCanvas.setLociIndex(colIndex);
	}
}