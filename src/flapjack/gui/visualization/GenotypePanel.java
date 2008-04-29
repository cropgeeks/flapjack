package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

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

	// Called whenever the underlying data of a view has changed in such a way
	// that we need to update the view's components to reflect this
	public void refreshView()
	{
		if (viewSet == null)
			return;

		canvas.setView(viewSet, view);
		listPanel.setView(view);
		statusPanel.setView(view);

		computePanelSizes();

		OverviewManager.createImage();
	}

	public GTViewSet getViewSet()
		{ return viewSet; }

	public GTView getView()
		{ return view; }

	public void setViewSet(GTViewSet viewSet)
	{
		this.viewSet = viewSet;

		// Remove all existing tabs
		tabs.removeAll();

		// Store the viewset's selected map before updating the tabs, because
		// the tab-code will set the value with 0 before it gets used properly
		int selectedIndex = viewSet.getViewIndex();

		// Recreate them, one tab per chromosome
		for (int i = 0; i < viewSet.getChromosomeCount(); i++)
		{
			GTView view = viewSet.getView(i);

			String name = view.getChromosomeMap().getName();
			int markerCount = view.getMarkerCount();

			tabs.addTab(name, Icons.CHROMOSOME, null);
			tabs.setToolTipTextAt(i, name + " (" + markerCount + ")");
		}

		// Now set the tabs to the actual index we're interested in
		tabs.setSelectedIndex(selectedIndex);
	}

	private void displayMap(int mapIndex)
	{
		viewSet.setViewIndex(mapIndex);
		view = viewSet.getView(mapIndex);

		setEditActions();

		tabs.setComponentAt(mapIndex, displayPanel);
		refreshView();
	}

	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		// Each time the scollbars are moved, the canvas must be redrawn, with
		// the new dimensions of the canvas being passed to it (window size
		// changes will cause scrollbar movement events)
		canvas.computeForRedraw(viewport.getExtentSize(), viewport.getViewPosition());
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
		// When the slider is moved...
		if (e.getSource() == statusPanel.getSliderX() ||
			e.getSource() == statusPanel.getSliderY())
			computePanelSizes();

		// When a tab is selected...
		else if (e.getSource() == tabs)
		{
			if (tabs.getSelectedIndex() != -1)
			{
				for (int i = 0; i < tabs.getTabCount(); i++)
					tabs.setComponentAt(i, new JPanel());

				displayMap(tabs.getSelectedIndex());
			}
		}
	}

	// When changing data or the zoom level...
	private void computePanelSizes()
	{
		int zoomX = statusPanel.getSliderX().getValue();
		int zoomY = statusPanel.getSliderY().getValue();

		listPanel.computeDimensions(zoomY);
		canvas.computeDimensions(zoomX, zoomY);

		validate();

		computeRowColSizes();
	}

	// When resizing the window or changing the zoom level...
	private void computeRowColSizes()
	{
		int cWidth = colCanvas.getWidth();

		rowCanvas.computeDimensions(listPanel.getWidth(), vBar.isVisible() ? (cWidth+vBar.getWidth()) : cWidth);
		colCanvas.computeDimensions(listPanel.getHeight(), hBar.isVisible() ? hBar.getHeight() : 0);
		mapCanvas.computeDimensions(listPanel.getWidth(), vBar.isVisible() ? (cWidth+vBar.getWidth()) : cWidth);

//		statusPanel.computeDimensions(listPanel.getWidth());
	}

	public void mouseWheelMoved(MouseWheelEvent e)
	{
		// CTRL (or CMD) keyboard shortcut
		int shortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		if (e.getModifiers() == shortcut)
		{
			int currentValue = statusPanel.getSliderY().getValue();
			statusPanel.getSliderY().setValue(currentValue + e.getWheelRotation());
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
	public void jumpToPosition(int lineIndex, int markerIndex)
	{
		if (lineIndex != -1)
		{
			int y = lineIndex * canvas.boxH - (canvas.boxH);
			vBar.setValue(y);
		}

		if (markerIndex != -1)
		{
			int x = markerIndex * canvas.boxW - (canvas.boxW);
			hBar.setValue(x);
		}
	}

	// Moves the scroll bars by the given amount in the x and y directions
	void moveBy(int x, int y)
	{
		hBar.setValue(hBar.getValue() + x);
		vBar.setValue(vBar.getValue() + y);
	}

	// Called as the mouse moves over the canvas...
	void overRow(int colIndex, int rowIndex)
	{
		canvas.setHighlightedIndices(rowIndex, colIndex);

		rowCanvas.setLineIndex(rowIndex);
		colCanvas.setLociIndex(colIndex);
		mapCanvas.setLociIndex(colIndex);

		statusPanel.setIndices(rowIndex, colIndex);
	}

	public void resetBufferedState(boolean state)
	{
		canvas.resetBufferedState(state);
	}

	// Returns the back-buffer used by the canvas if it has buffered the entire
	// view's area
	public BufferedImage getCanvasBuffer()
		{ return canvas.imageFull; }

	public long computeCanvasBufferInBytes()
	{
		return (long)canvas.canvasW * (long)canvas.canvasH * 3;
	}

	// Returns the back-buffer used by the canvas to buffer the current window
	// (that is, viewport on the view)
	public BufferedImage getCanvasViewPortBuffer()
		{ return canvas.imageViewPort; }

	public long computeCanvasViewPortBufferInBytes()
	{
		return (long)viewport.getWidth() * (long)viewport.getHeight() * 3;
	}

	public BufferedImage getMapCanvasBuffer()
		{ return mapCanvas.createSavableImage(); }

	public BufferedImage getLineCanvasBuffer()
		{ return listPanel.createSavableImage(); }

	// Updates the state of the Edit menu's undo/redo actions based on the undo
	// history of the view currently being displayed
	void setEditActions()
	{
		UndoManager manager = viewSet.getUndoManager();

		boolean undo = manager.canUndo();
		Actions.editUndo.setEnabled(undo);
		String undoStr = RB.format("gui.Actions.editUndo",
			manager.getNextUndoString());
		WinMainMenuBar.mEditUndo.setText(undoStr);
		WinMainToolBar.editUndo.setToolTipText(undoStr.trim());

		boolean redo = manager.canRedo();
		Actions.editRedo.setEnabled(redo);
		String redoStr = RB.format("gui.Actions.editRedo",
			manager.getNextRedoString());
		WinMainMenuBar.mEditRedo.setText(redoStr);
		WinMainToolBar.editRedo.setToolTipText(redoStr.trim());
	}

	public void processUndoRedo(boolean undo)
	{
		IUndoState state = null;

		if (undo)
			state = viewSet.getUndoManager().processUndo();
		else
			state = viewSet.getUndoManager().processRedo();

		if (state instanceof MovedMarkersState)
			returnToView(state);
		else
			refreshView();

		setEditActions();
		Actions.projectModified();
	}

	public void addUndoState(IUndoState state)
	{
		viewSet.getUndoManager().addUndoState(state);

		setEditActions();
	}

	// An undo/redo operation that affected markers should first ensure the
	// panel is showing the correct chromosomes (GTView) for that move
	private void returnToView(IUndoState state)
	{
		MovedMarkersState mms = (MovedMarkersState) state;
		GTView returnToView = mms.getView();

		int index = viewSet.getViews().indexOf(returnToView);

		viewSet.setViewIndex(index);
		setViewSet(viewSet);
	}
}