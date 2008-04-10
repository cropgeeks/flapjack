package flapjack.gui.dialog;

import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;

class NBImportOptionsPanel extends javax.swing.JPanel
{

	/** Creates new form NBImportOptionsPanel */
	public NBImportOptionsPanel(MouseAdapter dcl)
	{
		initComponents();

		panel.setBorder(BorderFactory.createTitledBorder(
			RB.getString("gui.dialog.NBImportOptionsPanel.instruction")));

		rFromFile.setText(RB.getString("gui.dialog.NBImportOptionsPanel.rFromFile"));
		rFromFile.setSelected(Prefs.guiImportMethod == 0);
		rFromFile.addMouseListener(dcl);

		rFromDB.setText(RB.getString("gui.dialog.NBImportOptionsPanel.rFromDB"));
		rFromDB.setSelected(Prefs.guiImportMethod == 1);
//		rFromDB.addMouseListener(dcl);

		rFromSample.setText(RB.getString("gui.dialog.NBImportOptionsPanel.rFromSample"));
		rFromSample.setSelected(Prefs.guiImportMethod == 2);
		rFromSample.addMouseListener(dcl);
	}

	void isOK()
	{
		if (rFromFile.isSelected())
			Prefs.guiImportMethod = 0;
		else if (rFromDB.isSelected())
			Prefs.guiImportMethod = 1;
		else if (rFromSample.isSelected())
			Prefs.guiImportMethod = 2;
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup = new javax.swing.ButtonGroup();
        panel = new javax.swing.JPanel();
        rFromFile = new javax.swing.JRadioButton();
        rFromDB = new javax.swing.JRadioButton();
        rFromSample = new javax.swing.JRadioButton();

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Import by:"));

        buttonGroup.add(rFromFile);
        rFromFile.setText("Providing the map and genotype data from files located on disk");

        buttonGroup.add(rFromDB);
        rFromDB.setText("Connecting to a Germinate database and importing directly");
        rFromDB.setEnabled(false);

        buttonGroup.add(rFromSample);
        rFromSample.setText("Using the built-in example dataset bundled with Flapjack");

        org.jdesktop.layout.GroupLayout panelLayout = new org.jdesktop.layout.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(rFromFile)
                    .add(rFromSample)
                    .add(rFromDB))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelLayout.createSequentialGroup()
                .add(rFromFile)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rFromDB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rFromSample))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JPanel panel;
    private javax.swing.JRadioButton rFromDB;
    private javax.swing.JRadioButton rFromFile;
    private javax.swing.JRadioButton rFromSample;
    // End of variables declaration//GEN-END:variables

}
