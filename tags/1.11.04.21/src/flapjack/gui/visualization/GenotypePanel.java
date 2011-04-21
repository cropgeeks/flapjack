// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.text.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

public class GenotypePanel extends JPanel implements ActionListener
{
	private GTViewSet viewSet;
	private GTView view;

	// The various (main) components that make up the display panel
	// Non-private components will also be . accessed by other components rather
	// than passing messages through this class all the time
	GenotypeCanvas canvas;
	MapCanvas mapCanvas;
	MiniMapCanvas miniMapCanvas;
	private RowCanvas rowCanvas;
	private ColCanvas colCanvas;
	QTLCanvas qtlCanvas;
	GraphCanvas graphCanvas;
	TraitCanvas traitCanvas;
	ListPanel listPanel;
	StatusPanelNB statusPanel;

	private CanvasController controller;

	// Secondary components needed by the panel
	private JScrollPane sp;
	private JSplitPane qtlSplitter;

	// Top control panel labels/controls
	private JComboBox combo;
	private JLabel chromoLabel = new JLabel();
	private JLabel lineLabel = new JLabel();
	private JLabel markerLabel = new JLabel();
	private JLabel lengthLabel = new JLabel();

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
		ctrlPanel.add(lengthLabel);

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(ctrlPanel, BorderLayout.NORTH);
		topPanel.add(miniMapCanvas, BorderLayout.SOUTH);

		// Scrolling components above the main display (qtl, map, etc)
		JPanel mapPanel = new JPanel(new BorderLayout());
//		topPanel.add(qtlCanvas, BorderLayout.NORTH);
//		mapPanel.add(miniMapCanvas, BorderLayout.NORTH);
		mapPanel.add(mapCanvas, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.add(graphCanvas, BorderLayout.NORTH);
		bottomPanel.add(rowCanvas, BorderLayout.SOUTH);

		// The main genotype area
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(sp);
		centerPanel.add(mapPanel, BorderLayout.NORTH);
		centerPanel.add(bottomPanel, BorderLayout.SOUTH);
		centerPanel.add(colCanvas, BorderLayout.EAST);
		centerPanel.add(traitCanvas, BorderLayout.WEST);


		// The split pane holding the QTLs (top) and everything else (bottom)
		qtlSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
//		qtlSplitter.setDividerLocation(Prefs.guiQTLSplitterLocation);
		qtlSplitter.setTopComponent(qtlCanvas);
		qtlSplitter.setBottomComponent(centerPanel);
		qtlSplitter.setContinuousLayout(true);
		qtlSplitter.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		qtlSplitter.addPropertyChangeListener(qtlCanvas);

		setVisibleStates();

		setLayout(new BorderLayout());
		add(topPanel, BorderLayout.NORTH);
		add(qtlSplitter);
		add(statusPanel, BorderLayout.SOUTH);
	}

	private void createControls(WinMain winMain)
	{
		combo = new JComboBox();
		combo.setRenderer(new ComboRenderer());
		RB.setText(chromoLabel, "gui.visualization.GenotypePanel.chromoLabel");
		chromoLabel.setLabelFor(combo);
		chromoLabel.setIcon(Icons.getIcon("CHROMOSOME"));

		canvas = new GenotypeCanvas(this);
		rowCanvas = new RowCanvas(this, canvas);
		colCanvas = new ColCanvas(canvas);
		mapCanvas = new MapCanvas(this, canvas);
		miniMapCanvas = new MiniMapCanvas(this, canvas);
		traitCanvas = new TraitCanvas(this, canvas);
		qtlCanvas = new QTLCanvas(this, canvas, mapCanvas);
		graphCanvas = new GraphCanvas(this, canvas);
		listPanel = new ListPanel();
		statusPanel = new StatusPanelNB(this);

		OverviewManager.initialize(winMain, this, canvas);

		sp = new JScrollPane();
		sp.setViewportView(canvas);
		sp.getViewport().setBackground(Prefs.visColorBackground);
		sp.setWheelScrollingEnabled(false);

		controller = new CanvasController(this, sp);
	}

	public void setViewSet(GTViewSet viewSet)
	{
		this.viewSet = viewSet;

		// Store the viewset's selected map before updating, because the event
		// code will set the value with 0 before it gets used properly
		int selectedIndex = viewSet.getViewIndex();

		combo.removeActionListener(this);
		combo.removeAllItems();

		// Add each chromosome to the combo box
		for (GTView view: viewSet.getViews())
			combo.addItem(view.getChromosomeMap());

		// Now set the combo box to the actual index we're interested in
		combo.addActionListener(this);
		combo.setSelectedIndex(selectedIndex);
	}

	public GTViewSet getViewSet()
		{ return viewSet; }

	public GTView getView()
		{ return view; }

	public CanvasController getController()
		{ return controller; }

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
		graphCanvas.determineVisibility();

		controller.computePanelSizes();
		setCtrlLabels();

		OverviewManager.createImage();
	}

	private void displayMap(int mapIndex)
	{
		viewSet.setViewIndex(mapIndex);
		view = viewSet.getView(mapIndex);

		setEditActions();

		refreshView();
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == combo && combo.getSelectedIndex() != -1)
			displayMap(combo.getSelectedIndex());
	}

	void forceOverviewUpdate()
	{
		canvas.updateOverviewSelectionBox();
	}

	void canvasViewChanged(int xIndex, int xW, int yIndex, int yH)
	{
		OverviewManager.updateOverviewSelectionBox(xIndex, xW, yIndex, yH);

		rowCanvas.updateOverviewSelectionBox(xIndex, xW);
		colCanvas.updateOverviewSelectionBox(yIndex, yH);

		repaint();
	}

	// Called as the mouse moves over the canvas...
	void overRow(int colIndex, int rowIndex)
	{
		canvas.setHighlightedIndices(rowIndex, colIndex);
		listPanel.repaint();

		rowCanvas.setLineIndex(rowIndex);
		colCanvas.setMarkerIndex(colIndex);
		mapCanvas.setMarkerIndex(colIndex);

		statusPanel.setIndices(rowIndex, colIndex);
	}

	public void resetBufferedState(boolean state)
	{
		canvas.resetBufferedState(state);
	}

	public long computeCanvasBufferInBytes()
	{
		return (long)canvas.canvasW * (long)canvas.canvasH * 3;
	}

	public BufferedImage getCanvasBuffer(boolean full) throws Error, Exception
		{ return canvas.createSavableImage(full); }

	public BufferedImage getMapCanvasBuffer(boolean full) throws Error, Exception
		{ return mapCanvas.createSavableImage(full); }

	public BufferedImage getLineCanvasBuffer(boolean full)
		{ return listPanel.createSavableImage(full, canvas.pY1); }

	public BufferedImage getTraitCanvasBuffer(boolean full)
		{ return traitCanvas.createSavableImage(full); }

	public BufferedImage getQTLCanvasBuffer(boolean full) throws Error, Exception
		{ return qtlCanvas.createSavableImage(full); }

	public BufferedImage getGraphCanvasBuffer(boolean full) throws Error, Exception
		{ return graphCanvas.createSavableImage(full); }

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
		miniMapCanvas.setVisible(Prefs.visShowMiniMapCanvas);
		mapCanvas.setVisible(Prefs.visShowMapCanvas);
		qtlCanvas.setVisible(Prefs.visShowQTLCanvas);
		rowCanvas.setVisible(Prefs.visShowRowCanvas);
		colCanvas.setVisible(Prefs.visShowColCanvas);
		traitCanvas.determineVisibility();
		graphCanvas.determineVisibility();
		statusPanel.setVisible(Prefs.visShowStatusPanel);

		if (Prefs.visShowLinePanel)
			sp.setRowHeaderView(listPanel);
		else
			sp.setRowHeaderView(null);

		if (Prefs.visShowQTLCanvas)
		{
			if (SystemUtils.isMacOS())
				qtlSplitter.setDividerSize(5);
			else
				qtlSplitter.setDividerSize(3);

			qtlSplitter.setDividerLocation(Prefs.guiQTLSplitterLocation);
		}
		// Hide the qtlSplitter if the QTL panel isn't visible
		else
			qtlSplitter.setDividerSize(0);
	}

	private void setCtrlLabels()
	{
		int lineCount = view.getLineCount();
		int mrkrCount = view.countGenuineMarkers();
		String length = NumberFormat.getInstance().format(view.getChromosomeMap().getLength());

		if (lineCount == 1)
			lineLabel.setText(RB.getString("gui.visualization.GenotypePanel.lineLabel1"));
		else
			lineLabel.setText(RB.format("gui.visualization.GenotypePanel.lineLabel2", lineCount));

		if (mrkrCount == 1)
			markerLabel.setText(RB.getString("gui.visualization.GenotypePanel.markerLabel1"));
		else
			markerLabel.setText(RB.format("gui.visualization.GenotypePanel.markerLabel2", mrkrCount));

		lengthLabel.setText(RB.format("gui.visualization.GenotypePanel.lengthLabel", length));
	}

	public void moveToPosition(final int rowIndex, final int colIndex, final boolean centre)
	{
		controller.moveToLater(rowIndex, colIndex, centre);
	}

	class ComboRenderer extends DefaultListCellRenderer
	{
		public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus)
		{
			Component c = super.getListCellRendererComponent(list, value, index,
				isSelected, cellHasFocus);

			ChromosomeMap map = (ChromosomeMap) value;

			setText(map.getName());

			if (map.isSpecialChromosome())
				setForeground(isSelected ? Color.white : Color.blue);
			else
				setForeground(isSelected ? Color.white : Color.black);

			return c;
		}
	}
}