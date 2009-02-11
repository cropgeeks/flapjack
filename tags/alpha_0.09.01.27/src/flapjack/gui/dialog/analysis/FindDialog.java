package flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import flapjack.analysis.*;
import flapjack.data.*;
import flapjack.gui.*;
import flapjack.gui.visualization.*;

import scri.commons.gui.*;

public class FindDialog extends JDialog implements ListSelectionListener
{
	private NBFindPanel nbPanel = new NBFindPanel(this);
	private GenotypePanel gPanel;

	private Finder finder = new Finder();
	private DecimalFormat d = new DecimalFormat("0.0");

	// Tracks the viewSet in use at the time of the last search
	private GTViewSet viewSet;

	// Highlighters for when lines/markers are selected
	private LMHighlighter lmHighlighter;

	public FindDialog(JFrame parent, GenotypePanel gPanel)
	{
		super(parent, RB.getString("gui.dialog.FindDialog.title"), false);

		this.gPanel = gPanel;

		add(new TitlePanel2(), BorderLayout.NORTH);
		add(nbPanel);
		addListeners();

		getRootPane().setDefaultButton(nbPanel.searchButton);
		SwingUtils.addCloseHandler(this, nbPanel);

		setTableModel(null);

		pack();
		setResizable(false);

		// Work out the current screen's width and height
		int scrnW = SwingUtils.getVirtualScreenDimension().width;
		int scrnH = SwingUtils.getVirtualScreenDimension().height;

		// Determine where on screen to display
		if (Prefs.guiFindDialogShown == false ||
			Prefs.guiFindDialogX > (scrnW-50) || Prefs.guiFindDialogY > (scrnH-50))
			setLocationRelativeTo(Flapjack.winMain);
		else
			setLocation(Prefs.guiFindDialogX, Prefs.guiFindDialogY);
	}

	private void addListeners()
	{
		addComponentListener(new ComponentAdapter()
		{
			public void componentMoved(ComponentEvent e)
			{
				Prefs.guiFindDialogX = getLocation().x;
				Prefs.guiFindDialogY = getLocation().y;
			}
		});

		addWindowListener(new WindowAdapter() {
			public void windowActivated(WindowEvent e)
			{
				// When focus returns to the Find Dialog...

				// If the view is different from what it was when the results
				// were generated, then clear them, as they're now invalid
				if (gPanel.getViewSet() != viewSet)
				{
					int count = nbPanel.tableModel.getRowCount();
					setTableModel(null);

					// And optionally inform the user about why
					if (count > 0 && Prefs.warnFindDialogResultsCleared)
					{
						JCheckBox checkbox = new JCheckBox();
						RB.setText(checkbox, "gui.dialog.FindDialog.focusCheckbox");

						TaskDialog.info(
							RB.getString("gui.dialog.FindDialog.focusWarning"),
							RB.getString("gui.text.close"),
							checkbox);

						Prefs.warnFindDialogResultsCleared = !checkbox.isSelected();
					}
				}
			}
		});
	}

	void setTableModel(Object[][] data)
	{
		String[] columnNames;

		if (Prefs.guiFindMethod == 0)
			columnNames = new String[] {
				RB.getString("gui.dialog.NBFindPanel.colLineName") };
		else
			columnNames = new String[] {
				RB.getString("gui.dialog.NBFindPanel.colMarkerName"),
				RB.getString("gui.dialog.NBFindPanel.colChromosome"),
				RB.getString("gui.dialog.NBFindPanel.colPosition") };


		// If there's no data, clear the table's headers
		if (data == null)
		{
			// Note: the model is created to be non-editable
			nbPanel.tableModel = new DefaultTableModel(columnNames, 0) {
        		public boolean isCellEditable(int rowIndex, int mColIndex) {
        			return false;
        	}};

			nbPanel.resultLabel.setText(
				RB.format("gui.dialog.NBFindPanel.resultLabel2", 0));
		}
		// Otherwise, dump the data and list the number of matches found
		else
		{
			// Note: the model is created to be non-editable
			nbPanel.tableModel = new DefaultTableModel(data, columnNames) {
        		public boolean isCellEditable(int rowIndex, int mColIndex) {
        			return false;
        	}};

			if (data.length == 1)
				nbPanel.resultLabel.setText(
					RB.getString("gui.dialog.NBFindPanel.resultLabel1"));
			else
				nbPanel.resultLabel.setText(
					RB.format("gui.dialog.NBFindPanel.resultLabel2", data.length));
		}

		nbPanel.table.setModel(nbPanel.tableModel);
		nbPanel.table.setCellEditor(null);
	}

	void runSearch()
	{
		new Thread(finder).start();
	}

	private class Finder implements Runnable
	{
		// Objects used to perform the searching
		FindLine lineFinder;
		FindMarker markerFinder;

		public void run()
		{
			// Check that any regex is valid
			if (Prefs.guiFindUseRegex)
			{
				try { Pattern.compile(nbPanel.getSearchStr()); }
				catch (PatternSyntaxException e)
				{
					TaskDialog.error(
						RB.format("gui.dialog.FindDialog.regexError", e.getMessage()),
						RB.getString("gui.text.close"));
					return;
				}
			}

			// TODO: This is a bad try/catch to have - provide more tests so we
			// know the code won't go wrong while searching
			try
			{
				viewSet = gPanel.getViewSet();

				if (Prefs.guiFindMethod == 0)
				{
					lineFinder = new FindLine(gPanel.getView(),
						Prefs.guiFindMatchCase, Prefs.guiFindUseRegex);
					findLine();
				}
				else
				{
					// Search across all chromosomes?
					boolean allChromosomes = Prefs.guiFindMethod == 2;

					markerFinder = new FindMarker(gPanel.getViewSet(),
						allChromosomes, Prefs.guiFindMatchCase, Prefs.guiFindUseRegex);
					findMarker();
				}
			}
			catch (Exception e) { e.printStackTrace(); }
		}

		private void findLine()
		{
			// Run the search
			LinkedList<FindLine.Result> results =
				lineFinder.search(nbPanel.getSearchStr());

			// Create an object array to hold the results for table
			Object[][] data = new Object[results.size()][1];

			int i = 0;
			for (FindLine.Result result: results)
				data[i++][0] = result.line;

			updateTable(data);
		}

		private void findMarker()
		{
			// Run the search
			LinkedList<FindMarker.Result> results =
				markerFinder.search(nbPanel.getSearchStr());

			// Create an object array to hold the results for table
			Object[][] data = new Object[results.size()][3];

			int i = 0;
			for (FindMarker.Result result: results)
			{
				data[i][0] = result.marker;
				data[i][1] = result.map;
				data[i][2] = d.format(result.marker.getPosition());

				i++;
			}

			updateTable(data);
		}

		private void updateTable(final Object[][] data)
		{
			Runnable r = new Runnable() {
				public void run() {
					setTableModel(data);
				}
			};

			try { SwingUtilities.invokeAndWait(r); }
			catch (Exception e) {}
		}
	}

	// Respond to changes in the selection of the results table
	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting() || nbPanel.table.getSelectedRow() == -1)
			return;

		processTableSelection();
	}

	void processTableSelection()
	{
		int row = nbPanel.table.getSelectedRow();
		Object selected = nbPanel.tableModel.getValueAt(row, 0);

		if (selected instanceof Line)
			displayLine((Line)selected);
		else if (selected instanceof Marker)
		{
			Object map = nbPanel.tableModel.getValueAt(row, 1);
			displayMarker((Marker)selected, (ChromosomeMap) map);
		}

//		if (nbPanel.tableModel.getRowCount() == 1)
//			nbPanel.table.getSelectionModel().clearSelection();
	}

	private void displayLine(Line line)
	{
		int lineIndex = viewSet.indexOf(line);
		jumpToPosition(lineIndex, -1);

		lmHighlighter = new LMHighlighter(gPanel, lineIndex, lmHighlighter, 0);
	}

	private void displayMarker(Marker marker, ChromosomeMap map)
	{
		// What view displays this chromosome's data?
		GTView view = viewSet.getView(map);

		int viewIndex = viewSet.indexof(view);
		final int markerIndex = view.indexOf(marker);

		// Make sure the panel is viewing the correct chromosome
		if (gPanel.getView() != view)
		{
			viewSet.setViewIndex(viewIndex);
			gPanel.setViewSet(viewSet);
		}

		jumpToPosition(-1, markerIndex);

		lmHighlighter = new LMHighlighter(gPanel, markerIndex, lmHighlighter, 1);
	}

	// Do the jump in a separate thread so that changes to the view on the main
	// window have had a chance to take effect, otherwise (with markers) it is
	// too much for it to change the view *and* scroll in the same EDT command.
	// This ensures that the view has changed and is waiting before attempting
	// to move the scrollbars.
	private void jumpToPosition(final int lineIndex, final int markerIndex)
	{
		Runnable r = new Runnable() {
			public void run() {
				gPanel.jumpToPosition(lineIndex, markerIndex, true);
			}
		};

		SwingUtilities.invokeLater(r);
	}
}