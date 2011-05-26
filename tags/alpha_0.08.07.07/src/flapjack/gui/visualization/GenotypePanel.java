package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.data.*;
import flapjack.gui.*;

public class GenotypePanel extends JPanel
	implements ActionListener, AdjustmentListener, ChangeListener, MouseWheelListener
{
	private GTViewSet viewSet;
	private GTView view;

	// The various (main) components that make up the display panel
	// Non-private components will also be . accessed by other components rather
	// than passing messages through this class all the time
	GenotypeCanvas canvas;
	MapCanvas mapCanvas;
	private QTLCanvas qtlCanvas;
	private RowCanvas rowCanvas;
	private ColCanvas colCanvas;
	TraitCanvas traitCanvas;
	ListPanel listPanel;
	NBStatusPanel statusPanel;

	// Secondary components needed by the panel
	private JTabbedPane tabs;
	private JScrollPane sp;
	private JScrollBar hBar, vBar;
	private JViewport viewport;

	// Top control panel labels/controls
	private JComboBox combo;
	private JLabel chromoLabel = new JLabel();
	private JLabel lineLabel = new JLabel();
	private JLabel markerLabel = new JLabel();


	public GenotypePanel(WinMain winMain)
	{
		createControls(winMain);

		// Controls along the top for selecting the chromosome etc
		JPanel ctrlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 5));
		ctrlPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ((Color)UIManager.get("Panel.background")).darker()));
		ctrlPanel.add(new JLabel(" "));
		ctrlPanel.add(chromoLabel);
		ctrlPanel.add(new JLabel(" "));
		ctrlPanel.add(combo);
		ctrlPanel.add(new JLabel(" "));
		ctrlPanel.add(lineLabel);
		ctrlPanel.add(markerLabel);

		// Scrolling components above the main display (qtl, map, etc)
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(qtlCanvas, BorderLayout.NORTH);
		topPanel.add(mapCanvas, BorderLayout.CENTER);

		// The main genotype area
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(sp);
		centerPanel.add(topPanel, BorderLayout.NORTH);
		centerPanel.add(rowCanvas, BorderLayout.SOUTH);
		centerPanel.add(colCanvas, BorderLayout.EAST);
		centerPanel.add(traitCanvas, BorderLayout.WEST);

		setVisibleStates();

		setLayout(new BorderLayout());
//		add(tabs);
		add(ctrlPanel, BorderLayout.NORTH);
		add(centerPanel);
		add(statusPanel, BorderLayout.SOUTH);
	}

	private void createControls(WinMain winMain)
	{
		tabs = new JTabbedPane();
		tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabs.addChangeListener(this);

		combo = new JComboBox();
		combo.addActionListener(this);
		RB.setText(chromoLabel, "gui.visualization.GenotypePanel.chromoLabel");
		chromoLabel.setLabelFor(combo);

		sp = new JScrollPane();
		sp.addMouseWheelListener(this);
		viewport = sp.getViewport();
		hBar = sp.getHorizontalScrollBar();
		vBar = sp.getVerticalScrollBar();
		hBar.addAdjustmentListener(this);
		vBar.addAdjustmentListener(this);

		canvas = new GenotypeCanvas(this);
		rowCanvas = new RowCanvas(this, canvas);
		colCanvas = new ColCanvas(canvas);
		mapCanvas = new MapCanvas(this, canvas);
		qtlCanvas = new QTLCanvas(this, canvas);
		traitCanvas = new TraitCanvas(this, canvas);

		listPanel = new ListPanel();
		statusPanel = new NBStatusPanel(this);

		OverviewManager.initialize(winMain, this, canvas);

		sp.setViewportView(canvas);
		sp.getViewport().setBackground(Prefs.visColorBackground);
	}

	// Called whenever the underlying data of a view has changed in such a way
	// that we need to update the view's components to reflect this
	public void refreshView()
	{
		if (viewSet == null)
			return;

		view.updateComparisons();

		canvas.setView(viewSet, view);
		listPanel.setView(view);
		statusPanel.setView(view);
		traitCanvas.determineVisibility();

		computePanelSizes();
		setCtrlLabels();

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

		combo.removeAllItems();

		// Recreate them, one tab per chromosome
		for (int i = 0; i < viewSet.getChromosomeCount(); i++)
		{
			GTView view = viewSet.getView(i);

			String name = view.getChromosomeMap().getName();
			int markerCount = view.getMarkerCount();

			tabs.addTab(name, Icons.CHROMOSOME, null);
			combo.addItem(name);
		}

		// Now set the tabs to the actual index we're interested in
		tabs.setSelectedIndex(selectedIndex);
		combo.setSelectedIndex(selectedIndex);
	}

	private void displayMap(int mapIndex)
	{
		viewSet.setViewIndex(mapIndex);
		view = viewSet.getView(mapIndex);

		setEditActions();

//		tabs.setComponentAt(mapIndex, displayPanel);
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

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == combo && combo.getSelectedIndex() != -1)
			displayMap(combo.getSelectedIndex());
	}

	public void stateChanged(ChangeEvent e)
	{
		// When a tab is selected...
		if (e.getSource() == tabs)
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
	void computePanelSizes()
	{
		int zoomX = statusPanel.getZoomX();
		int zoomY = statusPanel.getZoomY();

		listPanel.computeDimensions(zoomY);
		canvas.computeDimensions(zoomX, zoomY);
		mapCanvas.createImage();
		qtlCanvas.createImage();
	}

	public void mouseWheelMoved(MouseWheelEvent e)
	{
		// CTRL (or CMD) keyboard shortcut
		int shortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		if (e.getModifiers() == shortcut && Prefs.visLinkSliders)
		{
			int currentValue = statusPanel.getZoomY();
			statusPanel.setZoomY(currentValue + e.getWheelRotation());
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
		mapCanvas.updateView();
		qtlCanvas.updateView();
		traitCanvas.repaint();
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
		colCanvas.setMarkerIndex(colIndex);
		mapCanvas.setMarkerIndex(colIndex);

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

		boolean canUndo = manager.canUndo();
		Actions.editUndo.setEnabled(canUndo);
		String undoStr = RB.getString("gui.Actions.editUndo");
		if (canUndo)
			undoStr = RB.format("gui.Actions.editUndoCanUndo", manager.getNextUndoString());
		WinMainMenuBar.mEditUndo.setText(undoStr);
		WinMainToolBar.editUndo.setToolTipText(undoStr);

		boolean canRedo = manager.canRedo();
		Actions.editRedo.setEnabled(canRedo);
		String redoStr = RB.getString("gui.Actions.editRedo");
		if (canRedo)
			redoStr = RB.format("gui.Actions.editRedoCanRedo", manager.getNextRedoString());
		WinMainMenuBar.mEditRedo.setText(redoStr);
		WinMainToolBar.editRedo.setToolTipText(redoStr);
	}

	public void processUndoRedo(boolean undo)
	{
		IUndoState state = null;

		if (undo)
			state = viewSet.getUndoManager().processUndo();
		else
			state = viewSet.getUndoManager().processRedo();

		if (state.getView() != null)
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
		Actions.projectModified();
	}

	// An undo/redo operation that affected markers should first ensure the
	// panel is showing the correct chromosomes (GTView) for that move
	private void returnToView(IUndoState state)
	{
		GTView returnToView = state.getView();

		int index = viewSet.getViews().indexOf(returnToView);

		viewSet.setViewIndex(index);
		setViewSet(viewSet);
	}

	public void setVisibleStates()
	{
		mapCanvas.setVisible(Prefs.visShowMapCanvas);
		qtlCanvas.setVisible(Prefs.visShowQTLCanvas);
		rowCanvas.setVisible(Prefs.visShowRowCanvas);
		colCanvas.setVisible(Prefs.visShowColCanvas);
		traitCanvas.determineVisibility();
		statusPanel.setVisible(Prefs.visShowStatusPanel);

		if (Prefs.visShowLinePanel)
			sp.setRowHeaderView(listPanel);
		else
			sp.setRowHeaderView(null);
	}

	private void setCtrlLabels()
	{
		int lineCount = view.getLineCount();
		int mrkrCount = view.getMarkerCount();

		if (lineCount == 1)
			lineLabel.setText(RB.getString("gui.visualization.GenotypePanel.lineLabel1"));
		else
			lineLabel.setText(RB.format("gui.visualization.GenotypePanel.lineLabel2", lineCount));

		if (mrkrCount == 1)
			markerLabel.setText(RB.getString("gui.visualization.GenotypePanel.markerLabel1"));
		else
			markerLabel.setText(RB.format("gui.visualization.GenotypePanel.markerLabel2", mrkrCount));
	}
}