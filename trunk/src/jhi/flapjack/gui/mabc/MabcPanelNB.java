package jhi.flapjack.gui.mabc;

import jhi.flapjack.gui.table.*;

import scri.commons.gui.*;

public class MabcPanelNB extends javax.swing.JPanel
{
	public MabcPanelNB(MabcPanel panel)
	{
		initComponents();

		bFilter.setPopup(((LineDataTable)table).getFilterMenu());
		bFilter.setIcon(Icons.getIcon("FILTER"));

		bSort.addActionListener(panel);
		bSort.setIcon(Icons.getIcon("SORT"));

		bExport.setPopup(((LineDataTable)table).getExportMenu());
		bExport.setIcon(Icons.getIcon("EXPORTTRAITS"));

		bSelect.addActionListener(panel);
		bSelect.setIcon(Icons.getIcon("AUTOSELECT"));

		bRank.addActionListener(panel);
		bRank.setIcon(Icons.getIcon("RANK"));

		autoResize.addActionListener(panel);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new LineDataTable();
        bSort = new javax.swing.JButton();
        bSelect = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        bRank = new javax.swing.JButton();
        bExport = new scri.commons.gui.matisse.MenuButton();
        autoResize = new javax.swing.JCheckBox();
        filterLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        bFilter = new scri.commons.gui.matisse.MenuButton();

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {

            },
            new String []
            {

            }
        ));
        jScrollPane1.setViewportView(table);

        bSort.setText("Sort...");

        bSelect.setText("Auto Select...");

        jLabel1.setText("|");

        bRank.setText("Rank...");
        bRank.setEnabled(false);

        bExport.setText("Export");

        autoResize.setSelected(true);
        autoResize.setText("Auto-fit columns");
        autoResize.setOpaque(false);

        filterLabel.setText("Lines visible:");

        jLabel2.setText("|");

        bFilter.setText("Filter");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(autoResize)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(bSelect)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bRank)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bSort)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bExport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bSort)
                    .addComponent(bSelect)
                    .addComponent(jLabel1)
                    .addComponent(bRank)
                    .addComponent(bExport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(autoResize)
                    .addComponent(filterLabel)
                    .addComponent(jLabel2)
                    .addComponent(bFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JCheckBox autoResize;
    scri.commons.gui.matisse.MenuButton bExport;
    private scri.commons.gui.matisse.MenuButton bFilter;
    javax.swing.JButton bRank;
    javax.swing.JButton bSelect;
    javax.swing.JButton bSort;
    javax.swing.JLabel filterLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
