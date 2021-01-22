// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

class AboutFundingPanelNB extends JPanel
{
	public AboutFundingPanelNB()
	{
		initComponents();
		initComponents2();

		FlapjackUtils.initPanel(this, panel1, panel2, panel3);
	}

	private void initComponents2()
	{
		setLabel(gobii, Icons.getIcon("FUNDING-GOBII"),
			"http://www.gobiiproject.org/");
		setLabel(bmgf, Icons.getIcon("FUNDING-BMGF"),
			"http://www.gatesfoundation.org/");
		setLabel(cimmyt, Icons.getIcon("FUNDING-CIMMYT"),
			"http://www.cimmyt.org/");
		setLabel(masagro, Icons.getIcon("FUNDING-MASAGRO"),
			"http://masagro.mx/index.php/en/");
		setLabel(croptrust, Icons.getIcon("FUNDING-CROPTRUST"),
			"https://www.croptrust.org/");
		setLabel(icrisat, Icons.getIcon("FUNDING-ICRISAT"),
			"http://www.icrisat.org/");
		setLabel(scotgov, Icons.getIcon("FUNDING-SCOTGOV"),
			"http://www.gov.scot/");
	}

	private void setLabel(JLabel label, ImageIcon icon, String url)
	{
		label.setText("");
		label.setIcon(icon);
		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event)
			{
				FlapjackUtils.visitURL(url);
			}
		});
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
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        panel3 = new javax.swing.JPanel();
        panel2 = new javax.swing.JPanel();
        croptrust = new javax.swing.JLabel();
        icrisat = new javax.swing.JLabel();
        scotgov = new javax.swing.JLabel();
        panel1 = new javax.swing.JPanel();
        gobii = new javax.swing.JLabel();
        bmgf = new javax.swing.JLabel();
        cimmyt = new javax.swing.JLabel();
        masagro = new javax.swing.JLabel();

        jLabel1.setText("Flapjack development has only been possible due to the generous support and assistance of the following:");

        panel3.setLayout(new java.awt.GridBagLayout());

        panel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 5));

        croptrust.setText("croptrust");
        panel2.add(croptrust);

        icrisat.setText("icrisat");
        panel2.add(icrisat);

        scotgov.setText("scotgov");
        panel2.add(scotgov);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(18, 10, 11, 10);
        panel3.add(panel2, gridBagConstraints);

        panel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 5));

        gobii.setText("gobii");
        panel1.add(gobii);

        bmgf.setText("bmgf");
        panel1.add(bmgf);

        cimmyt.setText("cimmyt");
        panel1.add(cimmyt);

        masagro.setText("masagro");
        panel1.add(masagro);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(11, 10, 0, 10);
        panel3.add(panel1, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(panel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(panel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bmgf;
    private javax.swing.JLabel cimmyt;
    private javax.swing.JLabel croptrust;
    private javax.swing.JLabel gobii;
    private javax.swing.JLabel icrisat;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel masagro;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panel2;
    private javax.swing.JPanel panel3;
    private javax.swing.JLabel scotgov;
    // End of variables declaration//GEN-END:variables
}