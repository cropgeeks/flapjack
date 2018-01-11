// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class CitationDialogNB extends javax.swing.JPanel implements ActionListener
{
	private static ImageIcon logo = Icons.getIcon("CITATIONDIALOG");

    /** Creates new form HuttonDialogNB */
    public CitationDialogNB()
	{
		initComponents();
		link1.addActionListener(this);
		link3.addActionListener(this);

		if (SystemUtils.isWindows() == false)
		{
			jLabel1.setFont(new java.awt.Font("Dialog", 1, 16));
			jLabel2.setFont(new java.awt.Font("Dialog", 1, 13));
			jLabel3.setFont(new java.awt.Font("Dialog", Font.PLAIN, 12));

			link1.setFont(new java.awt.Font("Dialog", Font.PLAIN, 12));
			link3.setFont(new java.awt.Font("Dialog", Font.PLAIN, 12));
		}

		link1.setEnabled(false);
    }

	@Override
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		Graphics2D g = (Graphics2D) graphics;

		int w = getWidth();
		int h = getHeight();

		g.drawImage(logo.getImage(), 0, 0, w, h, null);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == link1)
		{
			Container parent = getParent();
			while (parent instanceof JDialog == false)
				parent = parent.getParent();
			((JDialog)parent).setVisible(false);
		}

		else if (e.getSource() == link3)
			FlapjackUtils.visitURL("http://bioinformatics.oxfordjournals.org/content/26/24/3133");
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        link3 = new scri.commons.gui.matisse.HyperLinkLabel();
        link1 = new scri.commons.gui.matisse.HyperLinkLabel();

        setMinimumSize(new java.awt.Dimension(790, 449));
        setPreferredSize(new java.awt.Dimension(790, 449));

        jLabel1.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Citing Flapjack");

        jLabel2.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel2.setText("<html>The continued development of Flapjack is dependent on the recognition of our efforts in maintaining and extending the software. If you use Flapjack in your research we would appreciate you citing the following paper:</html>");
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel3.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel3.setText("<html>Milne I, et al. 2010. <em>Bioinformatics</em> 26(24) 3133-3134.</html>");

        link3.setForeground(new java.awt.Color(68, 106, 156));
        link3.setText("Flapjack - graphical genotype visualization");
        link3.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N

        link1.setForeground(new java.awt.Color(68, 106, 156));
        link1.setText("You can close this window in 10 seconds");
        link1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(309, 309, 309)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(link1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(link3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel1)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(link3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(link1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(139, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    scri.commons.gui.matisse.HyperLinkLabel link1;
    scri.commons.gui.matisse.HyperLinkLabel link3;
    // End of variables declaration//GEN-END:variables

}