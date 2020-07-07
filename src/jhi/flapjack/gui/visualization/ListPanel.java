// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.image.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.dialog.*;
import jhi.flapjack.gui.table.*;

import scri.commons.gui.*;

public class ListPanel extends JPanel implements MouseMotionListener, MouseListener
{
	private GTViewSet viewSet;
	private GTView view;

	private LineDataTable lineTable;
	private LineDataTableModel lineModel;

	private static Font font;

	private int rowUnderMouse = -1;

	private GenotypePanel genotypePanel;

	ListPanel(GenotypePanel genotypePanel)
	{
		this.genotypePanel = genotypePanel;

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

	public void populateList()
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

		JMenuItem mSelectTextTraits = new JMenuItem();
		RB.setText(mSelectTextTraits, "gui.visualization.ListPanel.selectTraits");
		mSelectTextTraits.addActionListener(event ->
		{
			Flapjack.winMain.mData.dataSelectTextTraits();
		});

		JMenuItem mShowTableResults = new JMenuItem();
		RB.setText(mShowTableResults, "gui.visualization.ListPanel.tableResults");
		mShowTableResults.setEnabled(viewSet.tableHandler().model() != null);
		mShowTableResults.addActionListener(event ->
		{
			LinkedColumnSelectionDialog columnDialog = new LinkedColumnSelectionDialog(viewSet);
			if (columnDialog.isOK())
				populateList();
		});

		JMenuItem mTruncateNames = new JMenuItem();
		RB.setText(mTruncateNames, "gui.visualization.ListPanel.truncateNames");
		mTruncateNames.addActionListener(event -> new TruncateLongNamesDialog(viewSet, this));

		JMenuItem mCopyNames = new JMenuItem();
		RB.setText(mCopyNames, "gui.visualization.ListPanel.copyNames");
		mCopyNames.addActionListener(event ->
		{
			StringBuilder str = new StringBuilder();
			String newLine = System.getProperty("line.separator");
			for (int i = 0; i < lineTable.getRowCount(); i++)
				str.append(lineTable.getObjectAt(i, 0) + newLine);

			StringSelection selection = new StringSelection(str.toString());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
				selection, null);
		});

		JMenuItem mTruncateTraits = new JMenuItem();
		RB.setText(mTruncateTraits, "gui.visualization.ListPanel.truncateTraits");
		mTruncateTraits.setEnabled(viewSet.getTraits().length > 0 || viewSet.getTxtTraits().length > 0);
		mTruncateTraits.addActionListener(event -> new TruncateLongTraitValuesDialog(viewSet, this));

		JCheckBoxMenuItem mShowScores = new JCheckBoxMenuItem();
		RB.setText(mShowScores, "gui.visualization.ListPanel.showScores");
		mShowScores.setSelected(viewSet.getDisplayLineScores());
		mShowScores.addActionListener(event ->
		{
			viewSet.setDisplayLineScores(mShowScores.isSelected());
			populateList();
		});

		menu.add(mSelectTextTraits);
		menu.add(mShowTableResults);
		menu.addSeparator();
		menu.add(mCopyNames);
		menu.add(mTruncateNames);
		menu.add(mTruncateTraits);
		menu.add(mShowScores);
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
		int colUnderMouse = lineTable.columnAtPoint(e.getPoint());

		lineTable.repaint();

		if (rowUnderMouse > 0)
		{
			// Gathers values to pass to the StatusPanel
			String lineName = ((LineInfo) lineTable.getObjectAt(rowUnderMouse, 0)).name();
			if (colUnderMouse == 0 || colUnderMouse == 1)
				genotypePanel.statusPanel.setResultsValues(lineName, null, null);
			else
			{
				String columnName = lineTable.getColumnName(colUnderMouse) + ":";

				Object data = lineTable.getObjectAt(rowUnderMouse, colUnderMouse);
				if (data != null)
				{
					if (data instanceof Number)
						genotypePanel.statusPanel.setResultsValues(lineName, columnName, NumberFormat.getInstance().format(data));
					else
						genotypePanel.statusPanel.setResultsValues(lineName, columnName, data.toString());
				}
				else
					genotypePanel.statusPanel.setResultsValues(lineName, columnName, "");
			}
		}
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

		genotypePanel.statusPanel.setResultsValues(null, null, null);
		genotypePanel.statusPanel.setForMainUse();
	}

	// Base class for any renderer which needs to highlight table cells based on the row under the mouse on the main
	// canvas. Defaults to setting the text to value's toString()
	public class HighlightTableCellRenderer extends DefaultTableCellRenderer
	{
		private final DecimalFormat df = new DecimalFormat("0.##");

		private final Color selectedBG = new Color(240, 240, 240);
		private final Color selectedBG2 = new Color(245, 245, 255);
		private final Color selectedFG = new Color(255, 0, 0);

		public HighlightTableCellRenderer()
		{
			setOpaque(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus,
													   int row, int column)
		{
			Object o = ((CellData)obj).getData();
			super.getTableCellRendererComponent(table, obj, isSelected,
				hasFocus, row, column);

			String ttText = "";

			if (o instanceof Number)
			{
				setText(df.format(o));
				setHorizontalAlignment(JLabel.RIGHT);

				ttText = getText();
			}
			else if (o != null)
			{
				if (Prefs.guiTruncateNames && column == 0 && o.toString().length() > Prefs.guiTruncateNamesLength + 3)
					setText(o.toString().substring(0, Prefs.guiTruncateNamesLength) + "...");
				else if (Prefs.guiTruncateTraits && (column > 0 && o.toString().length() > Prefs.guiTruncateTraitsLength + 3))
					setText(o.toString().substring(0, Prefs.guiTruncateTraitsLength) + "...");
				else
					setText(o.toString());

				ttText = o.toString();

				setHorizontalAlignment(JLabel.LEFT);
			}

			setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));

			// Alternating grey/white column colours (from col 2 onwards)
			if (column > 0 && column % 2 == 0)
			{
				setBackground(selectedBG2);
			}
			else
			{
				setBackground(table.getBackground());
				setForeground(table.getForeground());
			}

			// Highlight the line "under" the mouse
			if (row == view.mouseOverLine || row == rowUnderMouse)
			{
				setBackground(selectedBG);
				setForeground(selectedFG);
			}
			else
			{
//				setBackground(table.getBackground());
//				setForeground(table.getForeground());
			}

			// TODO: Investigate colouring options, using something like this?
//			setBackground(lineModel.getDisplayColor(row, column));

			if (table.getColumnName(column).isEmpty() == false)
				setToolTipText("<html><b>" + table.getColumnName(column) + "</b><br>" + ttText + "</html>");
			else
				setToolTipText(null);

			return this;
		}
	}
}