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
	private GTViewSet viewSet;
	private GTView view;

	// The various (main) components that make up the display panel
	// Non-private components will also be . accessed by other components rather
	// than passing messages through this class all the time
	private GenotypeCanvas canvas;
	MapCanvas mapCanvas;
	private RowCanvas rowCanvas;
	private ColCanvas colCanvas;

	ListPanel listPanel;
	StatusPanel statusPanel;

	// Secondary components needed by the panel
	private JTabbedPane tabs;
	private JPanel displayPanel;
	private JScrollPane sp;
	private JScrollBar hBar, vBar;
	private JViewport viewport;

//	private int size = 11;


	public GenotypePanel(WinMain winMain)
	{
		createControls(winMain);

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(sp);
		centerPanel.add(mapCanvas, BorderLayout.NORTH);
		centerPanel.add(rowCanvas, BorderLayout.SOUTH);
		centerPanel.add(colCanvas, BorderLayout.EAST);

		displayPanel = new JPanel(new BorderLayout());
		displayPanel.add(centerPanel);
		displayPanel.add(statusPanel, BorderLayout.SOUTH);

		setLayout(new BorderLayout());
		add(tabs);
	}

	private void createControls(WinMain winMain)
	{
		tabs = new JTabbedPane();
		tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabs.addChangeListener(this);

		sp = new JScrollPane();
		sp.addMouseWheelListener(this);
		viewport = sp.getViewport();
		hBar = sp.getHorizontalScrollBar();
		vBar = sp.getVerticalScrollBar();
		hBar.addAdjustmentListener(this);
		vBar.addAdjustmentListener(this);

		canvas = new GenotypeCanvas(this);
		rowCanvas = new RowCanvas(canvas);
		colCanvas = new ColCanvas(canvas);
		mapCanvas = new MapCanvas(canvas);

		listPanel = new ListPanel();
		statusPanel = new StatusPanel(this);

		OverviewManager.initialize(winMain, this, canvas);

		sp.setRowHeaderView(listPanel);
		sp.setViewportView(canvas);
		sp.getViewport().setBackground(Color.white);

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				computeRowColSizes();
			}
		});
	}

	public void setViewSet(GTViewSet viewSet)
	{
		this.viewSet = viewSet;

		// Remove all existing tabs
		tabs.removeAll();

		// Recreate them, one tab per chromosome
		for (int i = 0; i < viewSet.getChromosomeCount(); i++)
		{
			GTView view = viewSet.getView(i);

			String name = view.getChromosomeMap().getName();
			int loci = view.getMarkerCount();

			tabs.addTab(name, Icons.CHROMOSOME, null);
			tabs.setToolTipTextAt(i, name + " (" + loci + ")");
		}

		computePanelSizes();
	}

	private void displayMap(int mapIndex)
	{
//		map = dataSet.getMapByIndex(mapIndex);

//		view = new GTView(dataSet, map);
//		view.initialize();

		view = viewSet.getView(mapIndex);

		canvas.setView(view);
		listPanel.setView(view);
		statusPanel.setView(view);

		tabs.setComponentAt(mapIndex, displayPanel);
		computePanelSizes();

		// Once everything else is updated/displayed, then update the overview
		OverviewManager.createImage();
	}

	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		// Each time the scollbars are moved, the canvas must be redrawn, with
		// the new dimensions of the canvas being passed to it (window size
		// changes will cause scrollbar movement events)
		canvas.computeForRedraw(viewport.getExtentSize(), viewport.getViewPosition());

		rowCanvas.repaint();
		colCanvas.repaint();
		mapCanvas.repaint();
	}

	void setScrollbarAdjustmentValues(int xIncrement, int yIncrement)
	{
		hBar.setUnitIncrement(xIncrement);
		hBar.setBlockIncrement(xIncrement);
		vBar.setUnitIncrement(yIncrement);
		vBar.setBlockIncrement(yIncrement);
	}

	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() == statusPanel.getSlider())
		{
			computePanelSizes();
			repaint();
		}

		else if (e.getSource() == tabs)
		{
			int index = tabs.getSelectedIndex();

			if (index == -1)
				return;

			for (int i = 0; i < tabs.getTabCount(); i++)
				tabs.setComponentAt(i, null);

			displayMap(index);
		}
	}

	private void computePanelSizes()
	{
		int size = statusPanel.getSlider().getValue();

		listPanel.computeDimensions(size);
		canvas.computeDimensions(size);

		validate();

		computeRowColSizes();

		repaint();
	}

	private void computeRowColSizes()
	{
		int cWidth = colCanvas.getWidth();

		rowCanvas.computeDimensions(listPanel.getWidth(), vBar.isVisible() ? (cWidth+vBar.getWidth()) : cWidth);
		colCanvas.computeDimensions(listPanel.getHeight(), hBar.isVisible() ? hBar.getHeight() : 0);

		mapCanvas.computeDimensions(listPanel.getWidth(), vBar.isVisible() ? (cWidth+vBar.getWidth()) : cWidth);
	}

	public void mouseWheelMoved(MouseWheelEvent e)
	{
		if (e.isControlDown())
		{
			int currentValue = statusPanel.getSlider().getValue();
			statusPanel.getSlider().setValue(currentValue + e.getWheelRotation());
		}
	}

	void forceOverviewUpdate()
	{
		canvas.updateOverviewSelectionBox();
	}

	void updateOverviewSelectionBox(int xIndex, int xW, int yIndex, int yH)
	{
		OverviewManager.updateOverviewSelectionBox(xIndex, xW, yIndex, yH);

		rowCanvas.updateOverviewSelectionBox(xIndex, xW);
		colCanvas.updateOverviewSelectionBox(yIndex, yH);

		mapCanvas.updateLociIndices(xIndex, xIndex+xW-1);
	}

	// Jumps to a position relative to a x/y index within the dataset array
	void jumpToPosition(int xIndex, int yIndex)
	{
		int x = xIndex * canvas.boxW - (canvas.boxW);
		int y = yIndex * canvas.boxH - (canvas.boxH);

		hBar.setValue(x);
		vBar.setValue(y);
	}

	// Moves the scroll bars by the given amount in the x and y directions
	void moveBy(int x, int y)
	{
		hBar.setValue(hBar.getValue() + x);
		vBar.setValue(vBar.getValue() + y);
	}

	void overRow(int colIndex, int rowIndex)
	{
		rowCanvas.setLineIndex(rowIndex);
		colCanvas.setLociIndex(colIndex);
		mapCanvas.setLociIndex(colIndex);

		statusPanel.setLineIndex(rowIndex);
		statusPanel.setMarkerIndex(colIndex);
	}
}