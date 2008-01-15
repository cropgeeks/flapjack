package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.data.*;
import flapjack.gui.*;

public class GenotypePanel extends JPanel
	implements AdjustmentListener, ChangeListener, MouseWheelListener
{
	private DataSet dataSet;
	private ChromosomeMap map;

	public static int mapIndex = 0;

	// The various (main) components that make up the display panel
	private ListPanel listPanel;
	private GenotypeCanvas canvas;
	private MapCanvas mapCanvas;
	private RowCanvas rowCanvas;
	private ColCanvas colCanvas;
	private OverviewDialog overviewDialog;

	// Secondary components needed by the panel
	private JSlider sizeSlider;
	private JScrollPane sp;
	private JScrollBar hBar, vBar;
	private JViewport view;

	private int size = 11;


	public GenotypePanel(WinMain winMain)
	{
		createControls(winMain);

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(sp);
//		centerPanel.add(mapCanvas, BorderLayout.NORTH);
		centerPanel.add(rowCanvas, BorderLayout.SOUTH);
		centerPanel.add(colCanvas, BorderLayout.EAST);

		JPanel sliderPanel = new JPanel();
		sliderPanel.add(sizeSlider);

		setLayout(new BorderLayout());
		add(centerPanel);
		add(sliderPanel, BorderLayout.SOUTH);
	}

	private void createControls(WinMain winMain)
	{
		sp = new JScrollPane();
		sp.addMouseWheelListener(this);
		view = sp.getViewport();
		hBar = sp.getHorizontalScrollBar();
		vBar = sp.getVerticalScrollBar();
		hBar.addAdjustmentListener(this);
		vBar.addAdjustmentListener(this);

		canvas = new GenotypeCanvas(this);
		listPanel = new ListPanel();
		rowCanvas = new RowCanvas(canvas);
		colCanvas = new ColCanvas(canvas);
		mapCanvas = new MapCanvas(canvas);

		overviewDialog = new OverviewDialog(winMain, this, canvas);


		sp.setRowHeaderView(listPanel);
		sp.setViewportView(canvas);
		sp.getViewport().setBackground(Color.white);

		sizeSlider = new JSlider(1, 25, 11);
		sizeSlider.addChangeListener(this);

		listPanel.computeDimensions(11);
		canvas.computeDimensions(11);

//		stateChanged(null);

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				computeRowColSizes();
			}
		});
	}

	public OverviewDialog getOverviewDialog() {
		return overviewDialog;
	}

	public void setData(DataSet dataSet)
	{
		this.dataSet = dataSet;
		map = dataSet.getMapByIndex(mapIndex);

		canvas.setData(dataSet, map);
		listPanel.setData(dataSet);

		mapCanvas.setChromosomeMap(map);

		stateChanged(null);
	}

	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		// Each time the scollbars are moved, the canvas must be redrawn, with
		// the new dimensions of the canvas being passed to it (window size
		// changes will cause scrollbar movement events)
		canvas.computeForRedraw(view.getExtentSize(), view.getViewPosition());

//		rowCanvas.repaint();
//		colCanvas.repaint();
//		mapCanvas.repaint();
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
		size = sizeSlider.getValue();

		listPanel.computeDimensions(size);
		canvas.computeDimensions(size);

		validate();

		computeRowColSizes();

		repaint();
	}

	void computeRowColSizes()
	{
		int cWidth = colCanvas.getWidth();

		System.out.println("cWidth = " + cWidth);

		rowCanvas.computeDimensions(listPanel.getWidth(), vBar.isVisible() ? (cWidth+vBar.getWidth()) : cWidth);
		colCanvas.computeDimensions(listPanel.getHeight(), hBar.isVisible() ? hBar.getHeight() : 0);

//		mapCanvas.computeDimensions(listPanel.getWidth(), vBar.isVisible() ? (cWidth+vBar.getWidth()) : cWidth);
	}

	public void mouseWheelMoved(MouseWheelEvent e)
	{
		if (e.isControlDown())
		{
			sizeSlider.setValue(sizeSlider.getValue() + e.getWheelRotation());
		}
	}

	void forceOverviewUpdate()
	{
		canvas.updateOverviewSelectionBox();
	}

	void updateOverviewSelectionBox(int xIndex, int xW, int yIndex, int yH)
	{
/*		if (overviewDialog != null)
			overviewDialog.updateOverviewSelectionBox(xIndex, xW, yIndex, yH);

		rowCanvas.updateOverviewSelectionBox(xIndex, xW);
		colCanvas.updateOverviewSelectionBox(yIndex, yH);

		mapCanvas.updateLociIndices(xIndex, xIndex+xW-1);
*/
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

//		rowCanvas.setGenotypeData(data);
//		colCanvas.setLociIndex(colIndex);
//		mapCanvas.setLociIndex(colIndex);
	}
}