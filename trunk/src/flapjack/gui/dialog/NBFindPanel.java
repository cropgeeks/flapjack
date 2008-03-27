package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;

import flapjack.gui.*;

import scri.commons.gui.*;

class NBFindPanel extends JPanel implements ActionListener
{
	NBFindPanel()
	{
		initComponents();

		findLabel.setText(RB.getString("gui.dialog.NBFindPanel.findLabel"));
		searchLabel.setText(RB.getString("gui.dialog.NBFindPanel.searchLabel"));
		panel.setBorder(BorderFactory.createTitledBorder(
			RB.getString("gui.dialog.NBFindPanel.panelTitle")));
		foundLabel1.setText(RB.getString("gui.dialog.NBFindPanel.foundLabel1"));
		foundLabel2.setText("...");

		checkChromo.setText(RB.getString("gui.dialog.NBFindPanel.checkChromo"));
		checkChromo.setSelected(Prefs.guiFindAllChromo);
		checkChromo.setEnabled(Prefs.guiFindMethod == 1);
		checkRegular.setText(RB.getString("gui.dialog.NBFindPanel.checkRegular"));
		checkRegular.setSelected(Prefs.guiFindRegular);

		searchCombo.addItem(RB.getString("gui.dialog.NBFindPanel.lines"));
		searchCombo.addItem(RB.getString("gui.dialog.NBFindPanel.markers"));
		searchCombo.setSelectedIndex(Prefs.guiFindMethod);
		searchCombo.addActionListener(this);

		initLinkLabel();
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == searchCombo)
			checkChromo.setEnabled(searchCombo.getSelectedIndex() == 1);
	}

	void isOK()
	{
		Prefs.guiFindAllChromo = checkChromo.isSelected();
		Prefs.guiFindRegular = checkRegular.isSelected();
		Prefs.guiFindMethod = searchCombo.getSelectedIndex();
	}

	private void initLinkLabel()
	{
		boolean makeLinkLabel = true;

		// The Mac can't (yet) run Java 6 so don't even bother trying...
		if (SystemUtils.isMacOS())
			makeLinkLabel = false;

		// ...checking for browser support
		else if (Desktop.isDesktopSupported())
		{
			Desktop desktop = Desktop.getDesktop();
			makeLinkLabel = desktop.isSupported(Desktop.Action.BROWSE);
		}

		if (makeLinkLabel)
			makeLinkLabel();
		else
			link.setText(RB.getString("gui.dialog.NBFindPanel.hint"));
	}

	// Turns the label into a blue mouse-over clickable link to a website
	private void makeLinkLabel()
	{
		link.setText(RB.getString("gui.dialog.NBFindPanel.link"));
		link.setForeground(Color.blue);
		link.setCursor(new Cursor(Cursor.HAND_CURSOR));
		link.setIcon(Icons.WEB);

		link.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent event)
			{
				try
				{
					Desktop desktop = Desktop.getDesktop();

		        	URI uri = new URI("http://java.sun.com/javase/6/docs/api/java/util/regex/Pattern.html");
		        	desktop.browse(uri);
				}
				catch (Exception e) {}
			}
		});
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        findLabel = new javax.swing.JLabel();
        findCombo = new javax.swing.JComboBox();
        searchLabel = new javax.swing.JLabel();
        searchCombo = new javax.swing.JComboBox();
        panel = new javax.swing.JPanel();
        checkChromo = new javax.swing.JCheckBox();
        checkRegular = new javax.swing.JCheckBox();
        link = new javax.swing.JLabel();
        foundLabel1 = new javax.swing.JLabel();
        foundLabel2 = new javax.swing.JLabel();

        findLabel.setDisplayedMnemonic('f');
        findLabel.setLabelFor(findCombo);
        findLabel.setText("Find what:");

        findCombo.setEditable(true);

        searchLabel.setDisplayedMnemonic('s');
        searchLabel.setLabelFor(searchCombo);
        searchLabel.setText("Search within:");

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Options:"));

        checkChromo.setMnemonic('c');
        checkChromo.setText("Search across all chromosomes");
        checkChromo.setDisplayedMnemonicIndex(18);

        checkRegular.setMnemonic('r');
        checkRegular.setText("Use regular expression pattern matching");

        link.setText("Click here for further details on searching using regular expressions");

        org.jdesktop.layout.GroupLayout panelLayout = new org.jdesktop.layout.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(panelLayout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(link))
                    .add(checkChromo)
                    .add(checkRegular))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelLayout.createSequentialGroup()
                .add(checkChromo)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(checkRegular)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(link)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        foundLabel1.setText("Found:");

        foundLabel2.setText("<>");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(searchLabel)
                            .add(findLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(findCombo, 0, 304, Short.MAX_VALUE)
                            .add(searchCombo, 0, 304, Short.MAX_VALUE)))
                    .add(panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(foundLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(foundLabel2)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(findLabel)
                    .add(findCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(searchLabel)
                    .add(searchCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(foundLabel1)
                    .add(foundLabel2))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkChromo;
    private javax.swing.JCheckBox checkRegular;
    private javax.swing.JComboBox findCombo;
    private javax.swing.JLabel findLabel;
    private javax.swing.JLabel foundLabel1;
    private javax.swing.JLabel foundLabel2;
    private javax.swing.JLabel link;
    private javax.swing.JPanel panel;
    private javax.swing.JComboBox searchCombo;
    private javax.swing.JLabel searchLabel;
    // End of variables declaration//GEN-END:variables
}