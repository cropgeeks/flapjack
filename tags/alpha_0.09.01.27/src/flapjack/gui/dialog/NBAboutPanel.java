package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.lang.management.*;
import java.text.*;

import flapjack.gui.*;

class NBAboutPanel extends javax.swing.JPanel
{
	public NBAboutPanel()
	{
		initComponents();

		initWebStuff();
		setBackground(Color.white);
		p2.setBackground(Color.white);

		String javaVer = System.getProperty("java.version");
		long freeMem = (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax()
				- ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed());
		NumberFormat nf = NumberFormat.getInstance();

		versionLabel.setText(RB.format("gui.dialog.NBAboutPanel.versionLabel", Install4j.VERSION));
		RB.setText(copyrightLabel, "gui.dialog.NBAboutPanel.copyrightLabel");
		javaLabel.setText(RB.format("gui.dialog.NBAboutPanel.javaLabel", javaVer));
		memLabel.setText(RB.format("gui.dialog.NBAboutPanel.memLabel", nf.format((long)(freeMem/1024f/1024f)) + "MB"));
		localeLabel.setText(RB.format("gui.dialog.NBAboutPanel.localeLabel", java.util.Locale.getDefault()));
		idLabel.setText(RB.format("gui.dialog.NBAboutPanel.idLabel", Prefs.flapjackID));

		scriIcon.setText("");
		scriIcon.setIcon(Icons.getIcon("ABOUT"));
	}

	private void initWebStuff()
	{
		final String flapHTML = "http://bioinf.scri.ac.uk/flapjack";
		final String scriHTML = "http://www.scri.ac.uk";

		// Turns the label into a blue mouse-over clickable link to a website
		webLabel.setForeground(Color.blue);
		webLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		webLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event)
			{
				FlapjackUtils.visitURL(flapHTML);
			}
		});

		scriIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
		scriIcon.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event)
			{
				FlapjackUtils.visitURL(scriHTML);
			}
		});
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
        webLabel = new javax.swing.JLabel();
        scriIcon = new javax.swing.JLabel();

        idLabel.setForeground(java.awt.Color.gray);
        idLabel.setText("Flapjack ID:");

        localeLabel.setForeground(java.awt.Color.gray);
        localeLabel.setText("Current Locale:");

        nameLabel.setText("Iain Milne, Micha Bayer, Paul Shaw, Linda Cardle, David Marshall");

        copyrightLabel.setText("Copyright (C) 2007-2008, Plant Bioinformatics Group, SCRI");

        versionLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        versionLabel.setText("Flapjack - x.xx.xx.xx");

        javaLabel.setForeground(java.awt.Color.gray);
        javaLabel.setText("Java version:");

        memLabel.setForeground(java.awt.Color.gray);
        memLabel.setText("Memory available to JVM:");

        webLabel.setText("http://bioinf.scri.ac.uk/flapjack");

        scriIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        scriIcon.setText("SCRI LOGO");

        org.jdesktop.layout.GroupLayout p2Layout = new org.jdesktop.layout.GroupLayout(p2);
        p2.setLayout(p2Layout);
        p2Layout.setHorizontalGroup(
            p2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(p2Layout.createSequentialGroup()
                .addContainerGap()
                .add(p2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(versionLabel)
                    .add(webLabel)
                    .add(copyrightLabel)
                    .add(nameLabel)
                    .add(javaLabel)
                    .add(memLabel)
                    .add(localeLabel)
                    .add(idLabel)
                    .add(scriIcon, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE))
                .addContainerGap())
        );
        p2Layout.setVerticalGroup(
            p2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(p2Layout.createSequentialGroup()
                .addContainerGap()
                .add(versionLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(webLabel)
                .add(18, 18, 18)
                .add(copyrightLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(nameLabel)
                .add(18, 18, 18)
                .add(javaLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(memLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(localeLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(idLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(scriIcon)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(p2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(p2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel copyrightLabel;
    private javax.swing.JLabel idLabel;
    private javax.swing.JLabel javaLabel;
    private javax.swing.JLabel localeLabel;
    private javax.swing.JLabel memLabel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JPanel p2;
    private javax.swing.JLabel scriIcon;
    private javax.swing.JLabel versionLabel;
    private javax.swing.JLabel webLabel;
    // End of variables declaration//GEN-END:variables

}