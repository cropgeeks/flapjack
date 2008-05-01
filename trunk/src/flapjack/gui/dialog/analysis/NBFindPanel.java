package flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

class NBFindPanel extends JPanel implements ActionListener
{
	private FindDialog findDialog;
	DefaultTableModel tableModel;
	DefaultComboBoxModel findModel;

	NBFindPanel(FindDialog findDialog)
	{
		this.findDialog = findDialog;

		initComponents();

		link.setIcon(Icons.WEB);

		findLabel.setText(RB.getString("gui.dialog.NBFindPanel.findLabel"));
		searchLabel.setText(RB.getString("gui.dialog.NBFindPanel.searchLabel"));
		searchButton.setText(RB.getString("gui.dialog.NBFindPanel.searchButton"));
		panel.setBorder(BorderFactory.createTitledBorder(
			RB.getString("gui.dialog.NBFindPanel.panelTitle")));
		resultLabel.setText(RB.format("gui.dialog.NBFindPanel.resultLabel2", 0));
		hintLabel.setText(RB.getString("gui.dialog.NBFindPanel.hintLabel"));

		checkCase.setText(RB.getString("gui.dialog.NBFindPanel.checkCase"));
		checkCase.setSelected(Prefs.guiFindMatchCase);
		checkCase.addActionListener(this);
		checkRegular.setText(RB.getString("gui.dialog.NBFindPanel.checkRegular"));
		checkRegular.setSelected(Prefs.guiFindUseRegex);
		checkRegular.addActionListener(this);

		searchCombo.addItem(RB.getString("gui.dialog.NBFindPanel.lines"));
		searchCombo.addItem(RB.getString("gui.dialog.NBFindPanel.markers"));
		searchCombo.addItem(RB.getString("gui.dialog.NBFindPanel.markersAll"));
		searchCombo.setSelectedIndex(Prefs.guiFindMethod);
		searchCombo.addActionListener(this);

		searchButton.addActionListener(this);

		findModel = new DefaultComboBoxModel();
		findCombo.setModel(findModel);

		table.getSelectionModel().addListSelectionListener(findDialog);

		updateFindHistory();
		initLinkLabel();
	}

	public void actionPerformed(ActionEvent e)
	{
		Prefs.guiFindMatchCase = checkCase.isSelected();
		Prefs.guiFindUseRegex = checkRegular.isSelected();
		Prefs.guiFindMethod = searchCombo.getSelectedIndex();

		// Toggle between searching for lines/markers
		if (e.getSource() == searchCombo)
		{
			updateFindHistory();
			findDialog.setTableModel(null);
			findDialog.runSearch();
		}

		else if (e.getSource() == searchButton || e.getSource() == findCombo)
		{
			updateFindHistory();
			findDialog.runSearch();
		}
	}

	// Maintains and updates the history of entries for the find combo box. The
	// preferences tracks this as a single string (tab deliminated), so this
	// method must convert to/from the string into a LinkedList.
	private void updateFindHistory()
	{
		// Use a list to hold the preferences history for easier sorting
		LinkedList<String> findHistory = new LinkedList<String>();

		// Fill the list with the tab-deliminated history
		if (Prefs.guiFindHistory != null)
		{
			String[] entries = Prefs.guiFindHistory.split("\t");
			for (String entry: entries)
				findHistory.add(entry);
		}

		// Now determine what's currently in the combo box (if anything)
		String str = (String) findCombo.getSelectedItem();
		if (str != null)
		{
			// If the search term already exists, remove it
			int index = findHistory.indexOf(str);
			if (index != -1)
				findHistory.remove(index);

			// Insert the term at the start of the list
			findHistory.addFirst(str);
		}

		// Don't let the list get any larger than 20 elements
		if (findHistory.size() > 20)
			findHistory.removeLast();

		// Now copy the list's elements into the combo box and the prefs history
		Prefs.guiFindHistory = "";
		findCombo.removeActionListener(this);
		findModel.removeAllElements();
		for (String entry: findHistory)
		{
			findModel.addElement(entry);
			Prefs.guiFindHistory += entry + "\t";
		}
		findCombo.addActionListener(this);
	}

	private void initLinkLabel()
	{
		String html = "http://java.sun.com/javase/6/docs/api/java/util/regex/Pattern.html";
		boolean makeLinkLabel = true;

		// Turns the label into a blue mouse-over clickable link to a website
		link.setText(RB.getString("gui.dialog.NBFindPanel.link"));
		link.setForeground(Color.blue);
		link.setCursor(new Cursor(Cursor.HAND_CURSOR));

		if (SystemUtils.isMacOS())
			makeJava5OSXLinkLabel(html);
		else
			makeJava6LinkLabel(html);
	}

	// Active the link using the Java 6 Desktop support object
	private void makeJava6LinkLabel(final String html)
	{
		link.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent event)
			{
				try
				{
					Desktop desktop = Desktop.getDesktop();

		        	URI uri = new URI(html);
		        	desktop.browse(uri);
				}
				catch (Exception e) {}
			}
		});
	}

	// Active the link using OSX Java 5 compatible code
	// See: http://www.centerkey.com/java/browser/
	private void makeJava5OSXLinkLabel(final String html)
	{
		link.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent event)
			{
				try
				{
					Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
					Method openURL = fileMgr.getDeclaredMethod("openURL",
						new Class[] {String.class});

					openURL.invoke(null, new Object[] {html});
				}
				catch (Exception e) {}
			}
		});
	}

	String getSearchStr()
	{
		String str = (String) findCombo.getSelectedItem();

		if (str == null)
			return "";
		else
			return str;
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
        checkRegular = new javax.swing.JCheckBox();
        link = new javax.swing.JLabel();
        checkCase = new javax.swing.JCheckBox();
        resultLabel = new javax.swing.JLabel();
        spTable = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        searchButton = new javax.swing.JButton();
        hintLabel = new javax.swing.JLabel();

        findLabel.setDisplayedMnemonic('f');
        findLabel.setLabelFor(findCombo);
        findLabel.setText("Find what:");

        findCombo.setEditable(true);

        searchLabel.setDisplayedMnemonic('s');
        searchLabel.setLabelFor(searchCombo);
        searchLabel.setText("Search within:");

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Options:"));

        checkRegular.setMnemonic('e');
        checkRegular.setText("Use regular expression pattern matching");
        checkRegular.setDisplayedMnemonicIndex(12);

        link.setText("View information on searching using regular expressions");

        checkCase.setMnemonic('m');
        checkCase.setText("Match case");

        org.jdesktop.layout.GroupLayout panelLayout = new org.jdesktop.layout.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(checkCase)
                    .add(panelLayout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(link))
                    .add(checkRegular))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(checkCase)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(checkRegular)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(link)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        resultLabel.setDisplayedMnemonic('r');
        resultLabel.setLabelFor(table);
        resultLabel.setText("Results:");

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        spTable.setViewportView(table);

        searchButton.setText("Search");

        hintLabel.setText("Click on any result to view it within the main window");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(searchLabel)
                            .add(findLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(findCombo, 0, 174, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(searchButton))
                            .add(searchCombo, 0, 249, Short.MAX_VALUE)))
                    .add(resultLabel)
                    .add(spTable, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
                    .add(hintLabel))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(findLabel)
                    .add(searchButton)
                    .add(findCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(searchLabel)
                    .add(searchCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(resultLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spTable, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(hintLabel)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkCase;
    private javax.swing.JCheckBox checkRegular;
    javax.swing.JComboBox findCombo;
    private javax.swing.JLabel findLabel;
    private javax.swing.JLabel hintLabel;
    private javax.swing.JLabel link;
    private javax.swing.JPanel panel;
    javax.swing.JLabel resultLabel;
    javax.swing.JButton searchButton;
    private javax.swing.JComboBox searchCombo;
    private javax.swing.JLabel searchLabel;
    private javax.swing.JScrollPane spTable;
    javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}