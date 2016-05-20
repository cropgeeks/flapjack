// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import jhi.flapjack.gui.table.LineDataTable;
import jhi.flapjack.gui.table.LineDataTableModel;
import scri.commons.gui.*;

class ListPanel extends JPanel
{
	private GTViewSet viewSet;
	private GTView view;

	private LineDataTable lineTable;
	// TODO: For now we can use a DefaultTableModel this is likely to change going forward to support some sort of
	// 		 TraitTableModel base class concept?
	private LineDataTableModel lineModel;
	private static Font font;

	private boolean showMabc = false;

	ListPanel()
	{
		createControls();

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(1, 2, 0, 0));
		setBackground(Color.WHITE);
		add(lineTable);
	}

	private void createControls()
	{
		// Setup our table with a default table model
		lineModel = new TablePanelTableModel(viewSet, showMabc);

		lineTable = new LineDataTable()
		{
			// Set the cell renderers so that we get the LineInfo version for the first column and the base class
			// HighlightTableCellRenderer by default for all others
			public TableCellRenderer getCellRenderer(int row, int column)
			{
				switch (column)
				{
					case 0:	return new LineInfoCellRenderer();

					default: return new HighlightTableCellRenderer();
				}
			}
		};
		lineTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		lineTable.setEnabled(false);
		lineTable.setDefaultRenderer(LineInfo.class, new LineInfoCellRenderer());
		lineTable.setShowGrid(false);

		lineTable.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				handlePopup(e);
			}
			public void mouseReleased(MouseEvent e) {
				handlePopup(e);
			}
		});

		lineTable.setModel(lineModel);
	}

	int getPanelWidth()
		{ return Prefs.visShowLinePanel ? getWidth() : 0; }

	void setView(GTView view)
	{
		this.view = view;
		viewSet = view.getViewSet();

		populateList();
	}

	private void populateList()
	{
		if (view == null)
			return;

		lineModel = new TablePanelTableModel(viewSet, showMabc);

		lineTable.setModel(lineModel);

		// Force a computeDimensions incase the number of columns in the model has changed
		if (font != null)
			computeDimensions(font.getSize());
	}

	void computeDimensions(int size)
	{
		font = new Font("Monospaced", Font.PLAIN, size);
		lineTable.setFont(font);
		// Re-size the height of the rows by getting the height of the font using font metrics
		// TODO: This can only be called when we have a graphics object....
		if (lineTable.getGraphics() != null)
			lineTable.setRowHeight(lineTable.getGraphics().getFontMetrics().getHeight());

		// Re-size columns so that they fit their content perfectly
		// TODO: Can we cache the "widest" element of the column so we have less re-calculating to do
		for (int col = 0; col < lineTable.getColumnCount(); col++)
		{
			int width = 0;
			for (int row = 0; row < lineTable.getRowCount(); row++)
			{
				TableCellRenderer renderer = lineTable.getCellRenderer(row, col);
				Component comp = lineTable.prepareRenderer(renderer, row, col);
				width = Math.max(comp.getPreferredSize().width + 1, width);
			}
			lineTable.getColumnModel().getColumn(col).setPreferredWidth(width);
		}

//		populateList();
	}

	void moveLine(int fromIndex, int toIndex)
	{
		// TODO: this is a slow operation on very large lists. Not sure why as
		// the same operation performed on a raw Vector is very fast, so
		// something in the way JList handles changes to its data must be the
		// reason why it's slow.

		// TODO 19/04/2016: We need to make sure that this works for an entire row...think about the best way to do this
		// 					probably going to require a moveRow method in the model class...

		if (fromIndex >= lineModel.getRowCount() || toIndex >= lineModel.getRowCount())
			return;

		LineInfo li = (LineInfo) lineModel.getValueAt(fromIndex, 0);
		lineModel.setValueAt( lineModel.getValueAt(toIndex, 0), fromIndex, 0);
		lineModel.setValueAt(li, toIndex, 0);
	}

	BufferedImage createSavableImage(boolean full, int yPos)
	{
		Dimension size = getPreferredSize();
		BufferedImage image;
		image = new BufferedImage(size.width, size.height,
				BufferedImage.TYPE_INT_RGB);

		// Paint a copy of this panel (forcing its background to white too)
		Color background = lineTable.getBackground();
		lineTable.setBackground(Color.white);
		Graphics2D g = image.createGraphics();
		lineTable.setBackground(background);

		if(!full)
		{
			g.translate(0, -yPos);
		}

		paint(g);

		return image;
	}

	private void handlePopup(MouseEvent e)
	{
		if (e.isPopupTrigger() == false)
			return;

		JPopupMenu menu = new JPopupMenu();

		final JCheckBoxMenuItem mShowScores = new JCheckBoxMenuItem();
		RB.setText(mShowScores, "gui.visualization.ListPanel.showScores");
		mShowScores.setSelected(viewSet.getDisplayLineScores());
		mShowScores.addActionListener(event ->
		{
			viewSet.setDisplayLineScores(mShowScores.isSelected());
			populateList();
		});

		final JCheckBoxMenuItem mShowMabcResults = new JCheckBoxMenuItem();
		mShowMabcResults.setText("Show MABC results");
		mShowMabcResults.setSelected(showMabc);
		mShowMabcResults.addActionListener(event ->
		{
			showMabc = !showMabc;
			populateList();
		});

		menu.add(mShowScores);
		menu.add(mShowMabcResults);
		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	// Base class for any renderer which needs to highlight table cells based on the row under the mouse on the main
	// canvas. Defaults to setting the text to value's toString()
	public class HighlightTableCellRenderer extends JLabel implements TableCellRenderer
	{
		private Color selectedBG = new Color(240, 240, 240);
		private Color selectedFG = new Color(255, 0, 0);

		public HighlightTableCellRenderer()
		{
			setOpaque(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
													   int row, int column)
		{
			setFont(font);

			setText(value.toString());
			setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));

			// Highlight the line "under" the mouse
			if (row == view.mouseOverLine)
			{
				setBackground(selectedBG);
				setForeground(selectedFG);
			}
			else
			{
				setBackground(table.getBackground());
				setForeground(table.getForeground());
			}

			return this;
		}
	}

	// Sets the text of a cell to the LineInfo's name
	public class LineInfoCellRenderer extends HighlightTableCellRenderer
	{

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
													   int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			setBorder(BorderFactory.createEmptyBorder());

			if (value instanceof LineInfo && value != null)
				setText(((LineInfo)value).name());

			return this;
		}
	}
}