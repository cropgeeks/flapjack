package flapjack.gui.simmatrix;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.io.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import scri.commons.gui.*;

public class SimMatrixPanelNB extends JPanel implements ActionListener
{
	private SimMatrixTableModel model;
	private GTViewSet viewSet;
	private SimMatrix matrix;

	private static int mouseRow = -1;
	private static int mouseCol = -1;

	private TableCellRenderer headerRenderer;
	private TableCellRenderer colorHeaderRenderer;

	private JTable rowHeaderTable;

	private static Color headerBG;

    /** Creates new form SimMatrixPanelNB2 */
    public SimMatrixPanelNB(GTViewSet viewSet, SimMatrix matrix, JScrollPane sp)
	{
		this.viewSet = viewSet;
		this.matrix = matrix;

		initComponents();


		// Table setup
		model = new SimMatrixTableModel(matrix);
		matrixTable.setModel(model);
		matrixTable.getTableHeader().setReorderingAllowed(false);

		headerBG = UIManager.getColor("TableHeader:\"TableHeader.renderer\"[MouseOver].backgroundPainter");

		headerRenderer = matrixTable.getTableHeader().getDefaultRenderer();
		colorHeaderRenderer = new ColorHeaderRenderer();
		matrixTable.getTableHeader().setDefaultRenderer(colorHeaderRenderer);

		// Create a table for the row header and set the rowHeaderView of the
		// scroll pane to be this table. This allows us to have a row header
		// for the table which doesn't scroll offscreen.
		rowHeaderTable = createRowHeaderTable(matrix.getLineInfos().size(), 1);
		setupRowHeaderTable(matrix);
		tableScrollPane.setRowHeaderView(rowHeaderTable);
		tableScrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowHeaderTable.getTableHeader());

		tabs.setComponentAt(0, tabsPanel.add(sp));
		tabs.setTitleAt(0, RB.getString("gui.simmatrix.SimMatrixPanelNB.tab1"));
		tabs.setTitleAt(1, RB.getString("gui.simmatrix.SimMatrixPanelNB.tab2"));

		bDendrogram.setText(RB.getString("gui.simmatrix.SimMatrixPanelNB.bDendrogram"));
		bExport.setText((RB.getString("gui.simmatrix.SimMatrixPanelNB.bExport")));
		bExport.setIcon(Icons.getIcon("EXPORTTRAITS"));

		bDendrogram.addActionListener(this);
		bPCoA.addActionListener(this);
		bExport.addActionListener(this);

		MouseHandler mh = new MouseHandler();
		matrixTable.addMouseListener(mh);
		matrixTable.addMouseMotionListener(mh);
	}

	private JTable createTable()
	{
		return new JTable()
		{
			public TableCellRenderer getCellRenderer(int row, int col)
			{
				TableCellRenderer tcr = SimMatrixTableModel.getCellRenderer();
				return (tcr != null) ? tcr : super.getCellRenderer(row, col);
			}
		};
	}

	private JTable createRowHeaderTable(int rows, int cols)
	{
		return new JTable(rows, cols)
		{
			public TableCellRenderer getCellRenderer(int row, int col)
			{
				return colorHeaderRenderer;
			}

			public boolean isCellEditable(int row, int col)
			{
				return false;
			}
		};
	}

	private void setupRowHeaderTable(SimMatrix matrix)
	{
		// Setup the model with the line names
		DefaultTableModel rowModel = new DefaultTableModel();
		Object[] objects = new Object[matrix.getLineInfos().size()];
		for (int i=0; i < matrix.getLineInfos().size(); i++)
			objects[i] = matrix.getLineInfos().get(i).name();
		rowModel.addColumn("", objects);
		rowHeaderTable.setModel(rowModel);

		// Tweaks to ensure behaviour of table is consistent with what we
		// would expected from a row header
		rowHeaderTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		rowHeaderTable.getColumnModel().getColumn(0).setPreferredWidth(100);
		rowHeaderTable.getColumnModel().getColumn(0).setResizable(false);
		rowHeaderTable.getTableHeader().setReorderingAllowed(false);
		rowHeaderTable.setPreferredScrollableViewportSize(rowHeaderTable.getPreferredSize());
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bDendrogram)
			Flapjack.winMain.mAnalysis.dendrogram();

		else if (e.getSource() == bPCoA)
			Flapjack.winMain.mAnalysis.principalCordAnalysis();

		else if (e.getSource() == bExport)
			exportSimMatrix();
	}

	private void exportSimMatrix()
	{
		String name = RB.format("gui.simmatrix.SimMatrixPanelNB.filename", viewSet.getName());
		File saveAs = new File(Prefs.guiCurrentDir, name);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("other.Filters.tabtxt"), "txt");
		String filename = FlapjackUtils.getSaveFilename(RB.getString("gui.simmatrix.SimMatrixPanelNB.saveAs"), saveAs, filter);

		if (filename != null)
		{
			SimMatrixExporter exporter = new SimMatrixExporter(matrix, filename);
			ProgressDialog dialog = new ProgressDialog(exporter,
				RB.format("gui.simmatrix.SimMatrixPanelNB.exportTitle"),
				RB.format("gui.simmatrix.SimMatrixPanelNB.exportLabel"), Flapjack.winMain);

			// If the operation failed or was cancelled...
			if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
			{
				if (dialog.getResult() == ProgressDialog.JOB_FAILED)
				{
					dialog.getException().printStackTrace();
					TaskDialog.error(
						RB.format("gui.simmatrix.SimMatrixPanelNB.exportException",
						dialog.getException().getMessage()),
						RB.getString("gui.text.close"));
				}

				return;
			}

			TaskDialog.showFileOpen(RB.format("gui.simmatrix.SimMatrixPanelNB.exportSuccess", filename),
				TaskDialog.INF, new File(filename));
		}
	}

	private class MouseHandler extends MouseInputAdapter
	{
		public void mouseExited(MouseEvent e)
		{
			// Ensure header highlighting is switched off when the mouse leaves
			// the component. Also requires repaint.
			mouseRow = mouseCol = -1;
			matrixTable.getTableHeader().repaint();
			rowHeaderTable.repaint();
		}

		public void mouseMoved(MouseEvent e)
		{
			mouseRow = matrixTable.rowAtPoint(e.getPoint());
			mouseCol = matrixTable.columnAtPoint(e.getPoint());
			// Repaints required to get highlights to appear
			matrixTable.getTableHeader().repaint();
			rowHeaderTable.repaint();
		}
	}

	// Used as the header renderer for both the normal table and the row header
	// table. Highlights the header of the row / column on mouse over.
	class ColorHeaderRenderer extends DefaultTableCellRenderer
	{
		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			JLabel comp = (JLabel) headerRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (table == rowHeaderTable && row == mouseRow)
			{
				comp = (JLabel) headerRenderer.getTableCellRendererComponent(table, value, true, hasFocus, row, column);
				comp.setBackground(headerBG);
			}

			else if (table == matrixTable && column == mouseCol)
				comp = (JLabel) headerRenderer.getTableCellRendererComponent(table, value, true, hasFocus, row, column);

			return comp;
		}
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabs = new javax.swing.JTabbedPane();
        tabsPanel = new javax.swing.JPanel();
        tableScrollPane = new javax.swing.JScrollPane();
        matrixTable = createTable();
        bDendrogram = new javax.swing.JButton();
        bExport = new javax.swing.JButton();
        bPCoA = new javax.swing.JButton();

        javax.swing.GroupLayout tabsPanelLayout = new javax.swing.GroupLayout(tabsPanel);
        tabsPanel.setLayout(tabsPanelLayout);
        tabsPanelLayout.setHorizontalGroup(
            tabsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 395, Short.MAX_VALUE)
        );
        tabsPanelLayout.setVerticalGroup(
            tabsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 144, Short.MAX_VALUE)
        );

        tabs.addTab("tab1", tabsPanel);

        matrixTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        matrixTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        matrixTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        matrixTable.setRowSelectionAllowed(false);
        tableScrollPane.setViewportView(matrixTable);

        tabs.addTab("tab2", tableScrollPane);

        bDendrogram.setText("Create dendrogram");

        bExport.setText("Export data");

        bPCoA.setText("Create PCoA");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(bExport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bDendrogram)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bPCoA)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bDendrogram)
                    .addComponent(bExport)
                    .addComponent(bPCoA))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton bDendrogram;
    javax.swing.JButton bExport;
    private javax.swing.JButton bPCoA;
    private javax.swing.JTable matrixTable;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JPanel tabsPanel;
    // End of variables declaration//GEN-END:variables

}