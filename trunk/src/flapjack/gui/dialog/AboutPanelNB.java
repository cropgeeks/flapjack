// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.lang.management.*;
import java.text.*;

import flapjack.gui.*;

import scri.commons.gui.*;

class AboutPanelNB extends javax.swing.JPanel implements ActionListener
{
	public AboutPanelNB()
	{
		initComponents();

		initWebStuff();
		setBackground(Color.white);
		p2.setBackground(Color.white);
		iconPanel.setBackground(Color.white);

		webLabel.addActionListener(this);

		String javaVer = System.getProperty("java.version");
		long freeMem = (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax()
				- ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed());
		NumberFormat nf = NumberFormat.getInstance();

		versionLabel.setText(RB.format("gui.dialog.NBAboutPanel.versionLabel", Install4j.VERSION));
		copyrightLabel.setText(RB.format("gui.dialog.NBAboutPanel.copyrightLabel", "\u0026"));
		RB.setText(nameLabel, "gui.dialog.NBAboutPanel.nameLabel");
		javaLabel.setText(RB.format("gui.dialog.NBAboutPanel.javaLabel", javaVer));
		memLabel.setText(RB.format("gui.dialog.NBAboutPanel.memLabel", nf.format((long)(freeMem/1024f/1024f)) + "MB"));
		localeLabel.setText(RB.format("gui.dialog.NBAboutPanel.localeLabel", java.util.Locale.getDefault()));
		idLabel.setText(RB.format("gui.dialog.NBAboutPanel.idLabel", Prefs.flapjackID));

		jhiIcon.setText("");
		jhiIcon.setIcon(Icons.getIcon("ABOUT"));
		cimmytIcon.setText("");
		cimmytIcon.setIcon(Icons.getIcon("ABOUT-CIMMYT"));
	}

	private void initWebStuff()
	{
		jhiIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
		jhiIcon.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event)
			{
				FlapjackUtils.visitURL("http://www.hutton.ac.uk");
			}
		});

		cimmytIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
		cimmytIcon.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event)
			{
				FlapjackUtils.visitURL("http://www.cimmyt.org");
			}
		});
	}

	public void actionPerformed(ActionEvent e)
	{
		final String flapHTML = "http://bioinf.hutton.ac.uk/flapjack";

		if(e.getSource() == webLabel)
		{
			FlapjackUtils.visitURL(flapHTML);
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

        p2 = new javax.swing.JPanel();
        idLabel = new javax.swing.JLabel();
        localeLabel = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        copyrightLabel = new javax.swing.JLabel();
        versionLabel = new javax.swing.JLabel();
        javaLabel = new javax.swing.JLabel();
        memLabel = new javax.swing.JLabel();
        webLabel = new scri.commons.gui.matisse.HyperLinkLabel();
        iconPanel = new javax.swing.JPanel();
        jhiIcon = new javax.swing.JLabel();
        cimmytIcon = new javax.swing.JLabel();

        idLabel.setForeground(java.awt.Color.gray);
        idLabel.setText("Flapjack ID:");

        localeLabel.setForeground(java.awt.Color.gray);
        localeLabel.setText("Current Locale:");

        nameLabel.setText("Iain Milne, Micha Bayer, Paul Shaw, Linda Cardle, Gordon Stephen, and David Marshall");

        copyrightLabel.setText("Copyright (C) 2007-2008, Plant Bioinformatics Group, JHI");

        versionLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        versionLabel.setText("Flapjack - x.xx.xx.xx");

        javaLabel.setForeground(java.awt.Color.gray);
        javaLabel.setText("Java version:");

        memLabel.setForeground(java.awt.Color.gray);
        memLabel.setText("Memory available to JVM:");

        webLabel.setText("http://bioinf.hutton.ac.uk/flapjack");

        iconPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 5));

        jhiIcon.setText("JHI");
        iconPanel.add(jhiIcon);

        cimmytIcon.setText("CIMMYT");
        iconPanel.add(cimmytIcon);

        javax.swing.GroupLayout p2Layout = new javax.swing.GroupLayout(p2);
        p2.setLayout(p2Layout);
        p2Layout.setHorizontalGroup(
            p2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p2Layout.createSequentialGroup()
                .addGroup(p2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(p2Layout.createSequentialGroup()
                        .addGroup(p2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(p2Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(versionLabel))
                            .addGroup(p2Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(copyrightLabel))
                            .addGroup(p2Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(nameLabel))
                            .addGroup(p2Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(javaLabel))
                            .addGroup(p2Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(memLabel))
                            .addGroup(p2Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(localeLabel))
                            .addGroup(p2Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(idLabel))
                            .addGroup(p2Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(webLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(iconPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        p2Layout.setVerticalGroup(
            p2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(versionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(webLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(copyrightLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nameLabel)
                .addGap(18, 18, 18)
                .addComponent(javaLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(memLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(localeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(idLabel)
                .addGap(18, 18, 18)
                .addComponent(iconPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(p2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(p2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel cimmytIcon;
    private javax.swing.JLabel copyrightLabel;
    private javax.swing.JPanel iconPanel;
    private javax.swing.JLabel idLabel;
    private javax.swing.JLabel javaLabel;
    private javax.swing.JLabel jhiIcon;
    private javax.swing.JLabel localeLabel;
    private javax.swing.JLabel memLabel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JPanel p2;
    private javax.swing.JLabel versionLabel;
    scri.commons.gui.matisse.HyperLinkLabel webLabel;
    // End of variables declaration//GEN-END:variables


}