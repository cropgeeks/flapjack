/*
 * NBSortLinesPanel.java
 *
 * Created on 25 February 2008, 08:37
 */

package flapjack.gui.dialog.analysis;

import javax.swing.*;

import flapjack.data.*;

/**
 *
 * @author  imilne
 */
class NBSortLinesPanel extends javax.swing.JPanel
{
	private GTView view;

	private DefaultComboBoxModel lineModel;

	/** Creates new form NBSortLinesPanel */
	public NBSortLinesPanel(GTViewSet viewSet)
	{
		initComponents();

		view = viewSet.getSelectedView();

		lineModel = new DefaultComboBoxModel();
		for (int i = 0; i < view.getLineCount(); i++)
			lineModel.addElement(view.getLine(i));

		selectedLine.setModel(lineModel);

		if (view.mouseOverLine != -1)
			selectedLine.setSelectedIndex(view.mouseOverLine);
	}

	boolean isOK()
	{
		view.mouseOverLine = selectedLine.getSelectedIndex();

		return true;
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        group1 = new javax.swing.ButtonGroup();
        group2 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        selectedLine = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        radioMarkersCurrent = new javax.swing.JRadioButton();
        radioMarkersAll = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        radioSortCurrent = new javax.swing.JRadioButton();
        radioSortAll = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();

        jLabel1.setLabelFor(selectedLine);
        jLabel1.setText("TODO: Sort lines by EITHER similarity or locus comparison to this line:");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Sort using the markers from:"));

        group1.add(radioMarkersCurrent);
        radioMarkersCurrent.setSelected(true);
        radioMarkersCurrent.setText("The current chromosome only");
        radioMarkersCurrent.setEnabled(false);

        group1.add(radioMarkersAll);
        radioMarkersAll.setText("All chromosomes in the dataset");
        radioMarkersAll.setEnabled(false);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(radioMarkersCurrent)
                    .add(radioMarkersAll))
                .addContainerGap(180, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(radioMarkersCurrent)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(radioMarkersAll))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Apply the sort to:"));

        group2.add(radioSortCurrent);
        radioSortCurrent.setSelected(true);
        radioSortCurrent.setText("The current chromosome only");
        radioSortCurrent.setEnabled(false);

        group2.add(radioSortAll);
        radioSortAll.setText("All chromosomes across the current view");
        radioSortAll.setEnabled(false);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(radioSortCurrent)
                    .add(radioSortAll))
                .addContainerGap(134, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(radioSortCurrent)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(radioSortAll))
        );

        jLabel2.setText("This dialog is a WORK IN PROGRESS placeholder.");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, selectedLine, 0, 377, Short.MAX_VALUE)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel2)
                    .add(jLabel1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(selectedLine, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup group1;
    private javax.swing.ButtonGroup group2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton radioMarkersAll;
    private javax.swing.JRadioButton radioMarkersCurrent;
    private javax.swing.JRadioButton radioSortAll;
    private javax.swing.JRadioButton radioSortCurrent;
    private javax.swing.JComboBox selectedLine;
    // End of variables declaration//GEN-END:variables

}
