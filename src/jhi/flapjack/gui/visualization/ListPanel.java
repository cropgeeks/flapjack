// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.text.*;
import javax.swing.*;
import javax.swing.table.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.table.*;

import scri.commons.gui.*;

class ListPanel extends JPanel implements MouseMotionListener, MouseListener
{
	private GTViewSet viewSet;
	private GTView view;

	private LineDataTable lineTable;
	private LineDataTableModel lineModel;

	private static Font font;

	private int rowUnderMouse = -1;

	ListPanel()
	{
		createControls();

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(1, 2, 0, 0));
		setBackground(Color.WHITE);
		add(lineTable);

		lineTable.addMouseMotionListener(this);
		lineTable.addMouseListener(this);
	}

	private void createControls()
	{
		// Setup our table with a default table model
		lineModel = new TablePanelTableModel(viewSet);

		lineTable = new LineDataTable();
		lineTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		lineTable.setEnabled(false);
		lineTable.setDefaultRenderer(CellData.class, new HighlightTableCellRenderer());
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

		lineModel = new TablePanelTableModel(viewSet);

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
		if (lineTable.getGraphics() != null)
			lineTable.setRowHeight(lineTable.getGraphics().getFontMetrics().getHeight());

		// If we don't have a graphics object we can fall back on the less accurate component.getFontMetrics
		else
			lineTable.setRowHeight(lineTable.getFontMetrics(font).getHeight());

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

		LineInfo li = (LineInfo) lineModel.getObjectAt(fromIndex, 0);
		lineModel.setValueAt(lineModel.getObjectAt(toIndex, 0), fromIndex, 0);
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

		JCheckBoxMenuItem mShowScores = new JCheckBoxMenuItem();
		RB.setText(mShowScores, "gui.visualization.ListPanel.showScores");
		mShowScores.setSelected(viewSet.getDisplayLineScores());
		mShowScores.addActionListener(event ->
		{
			viewSet.setDisplayLineScores(mShowScores.isSelected());
			populateList();
		});

		JCheckBoxMenuItem mShowTableResults = new JCheckBoxMenuItem();
		mShowTableResults.setText("Show table results");
		mShowTableResults.setSelected(viewSet.getLinkedModelCols().length > 0);
		mShowTableResults.setVisible(viewSet.tableHandler().getModel() != null);
		mShowTableResults.addActionListener(event ->
		{
			LinkedColumnSelectionDialog columnDialog = new LinkedColumnSelectionDialog(viewSet);
			if (columnDialog.isOK())
				populateList();
		});

		menu.add(mShowScores);
		menu.add(mShowTableResults);
		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		rowUnderMouse = lineTable.rowAtPoint(e.getPoint());
		lineTable.repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		rowUnderMouse = -1;
		lineTable.repaint();
	}

	// Base class for any renderer which needs to highlight table cells based on the row under the mouse on the main
	// canvas. Defaults to setting the text to value's toString()
	public class HighlightTableCellRenderer extends JLabel implements TableCellRenderer
	{
		protected DecimalFormat df = new DecimalFormat("0.00");

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
			setHorizontalAlignment(JLabel.LEFT);

			// LineInfo and blank line (if included) aren't double wrapped in
			// CellData so we can get them and set their text here
			Object val = ((CellData)value).getData();

			if (val instanceof LineInfo || val instanceof String)
				setText(val.toString());

			// Anything which comes in from the linked table is wrapped in two
			// layers of CellData, so we have to do another cast on the result
			// of getData to get to the actual object
			else if (val instanceof CellData)
			{
				Object v = ((CellData)val).getData();

				if (v instanceof Number)
				{
					setText(df.format(v));
					setHorizontalAlignment(JLabel.RIGHT);
				}

				else if (v != null)
					setText(v.toString());
			}

			setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));

			// Highlight the line "under" the mouse
			if (row == view.mouseOverLine || row == rowUnderMouse)
			{
				setBackground(selectedBG);
				setForeground(selectedFG);
			}
			else
			{
				setBackground(table.getBackground());
				setForeground(table.getForeground());
			}

			if (table.getColumnName(column).isEmpty() == false)
				setToolTipText("<html><b>" + table.getColumnName(column) + "</b><br>" + getText() + "</html>");
			else
				setToolTipText(null);

			return this;
		}
	}
}