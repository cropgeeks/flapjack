package flapjack.gui.dialog.analysis;

import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.gui.visualization.*;

class NBSortLinesByTraitPanel extends JPanel
{
	private SortLinesByTraitDialog dialog;

	public NBSortLinesByTraitPanel(SortLinesByTraitDialog dialog, GenotypePanel gPanel)
	{
		this.dialog = dialog;

		initComponents();

		panel1.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.analysis.NBSortLinesByTraitPanel.panel1.title")));
		panel2.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.analysis.NBSortLinesByTraitPanel.panel2.title")));
		RB.setText(rAsc1, "gui.dialog.analysis.NBSortLinesByTraitPanel.ascending");
		RB.setText(rAsc2, "gui.dialog.analysis.NBSortLinesByTraitPanel.ascending");
		RB.setText(rDes1, "gui.dialog.analysis.NBSortLinesByTraitPanel.descending");
		RB.setText(rDes2, "gui.dialog.analysis.NBSortLinesByTraitPanel.descending");


		// Fill the combo boxes with the possible traits
		DataSet dataSet = gPanel.getViewSet().getDataSet();

		combo2.addItem("");
		for (Trait trait: dataSet.getTraits())
		{
			combo1.addItem(trait.getName());
			combo2.addItem(trait.getName());
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        panel1 = new javax.swing.JPanel();
        combo1 = new javax.swing.JComboBox();
        rAsc1 = new javax.swing.JRadioButton();
        rDes1 = new javax.swing.JRadioButton();
        panel2 = new javax.swing.JPanel();
        combo2 = new javax.swing.JComboBox();
        rAsc2 = new javax.swing.JRadioButton();
        rDes2 = new javax.swing.JRadioButton();

        panel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Sort on this trait first:"));

        buttonGroup1.add(rAsc1);
        rAsc1.setSelected(true);
        rAsc1.setText("Ascending");

        buttonGroup1.add(rDes1);
        rDes1.setText("Descending");

        org.jdesktop.layout.GroupLayout panel1Layout = new org.jdesktop.layout.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(combo1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 183, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 18, Short.MAX_VALUE)
                .add(panel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(rDes1)
                    .add(rAsc1))
                .addContainerGap())
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(panel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(rAsc1)
                    .add(combo1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rDes1)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Then on this trait second:"));

        buttonGroup2.add(rAsc2);
        rAsc2.setSelected(true);
        rAsc2.setText("Ascending");

        buttonGroup2.add(rDes2);
        rDes2.setText("Descending");

        org.jdesktop.layout.GroupLayout panel2Layout = new org.jdesktop.layout.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(combo2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 183, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 18, Short.MAX_VALUE)
                .add(panel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(rDes2)
                    .add(rAsc2))
                .addContainerGap())
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(panel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(rAsc2)
                    .add(combo2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rDes2)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, panel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, panel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(panel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    javax.swing.JComboBox combo1;
    javax.swing.JComboBox combo2;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panel2;
    javax.swing.JRadioButton rAsc1;
    javax.swing.JRadioButton rAsc2;
    javax.swing.JRadioButton rDes1;
    javax.swing.JRadioButton rDes2;
    // End of variables declaration//GEN-END:variables
}