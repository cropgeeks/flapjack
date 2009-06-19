package flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import flapjack.gui.*;

import scri.commons.gui.*;

class NBFindPanel extends JPanel implements ActionListener
{
	private FindDialog findDialog;
	DefaultTableModel tableModel;
	DefaultComboBoxModel findModel;

	NBFindPanel(final FindDialog findDialog)
	{
		this.findDialog = findDialog;

		initComponents();

		setBackground((Color)UIManager.get("fjDialogBG"));
		panel.setBackground((Color)UIManager.get("fjDialogBG"));

		link.setIcon(Icons.getIcon("WEB"));

		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "gui.dialog.analysis.FindDialog");

		RB.setText(findLabel, "gui.dialog.NBFindPanel.findLabel");
		RB.setText(searchLabel, "gui.dialog.NBFindPanel.searchLabel");
		RB.setText(bSearch, "gui.dialog.NBFindPanel.searchButton");
		panel.setBorder(BorderFactory.createTitledBorder(
			RB.getString("gui.dialog.NBFindPanel.panelTitle")));
		resultLabel.setText(RB.format("gui.dialog.NBFindPanel.resultLabel2", 0));
		RB.setMnemonic(resultLabel, "gui.dialog.NBFindPanel.resultLabel2");
		RB.setText(hintLabel, "gui.dialog.NBFindPanel.hintLabel");

		RB.setText(checkCase, "gui.dialog.NBFindPanel.checkCase");
		checkCase.setSelected(Prefs.guiFindMatchCase);
		checkCase.addActionListener(this);
		RB.setText(checkRegular, "gui.dialog.NBFindPanel.checkRegular");
		checkRegular.setSelected(Prefs.guiFindUseRegex);
		checkRegular.addActionListener(this);

		searchCombo.addItem(RB.getString("gui.dialog.NBFindPanel.lines"));
		searchCombo.addItem(RB.getString("gui.dialog.NBFindPanel.markers"));
		searchCombo.addItem(RB.getString("gui.dialog.NBFindPanel.markersAll"));
		searchCombo.setSelectedIndex(Prefs.guiFindMethod);
		searchCombo.addActionListener(this);

		bSearch.addActionListener(this);

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

		else if (e.getSource() == bSearch || e.getSource() == findCombo)
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
		final String html = "http://java.sun.com/javase/6/docs/api/java/util/regex/Pattern.html";

		// Turns the label into a blue mouse-over clickable link to a website
		link.setText(RB.getString("gui.dialog.NBFindPanel.link"));
		link.setForeground(Color.blue);
		link.setCursor(new Cursor(Cursor.HAND_CURSOR));

		link.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event)
			{
				FlapjackUtils.visitURL(html);
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
        bSearch = new javax.swing.JButton();
        hintLabel = new javax.swing.JLabel();
        bHelp = new javax.swing.JButton();

        findLabel.setLabelFor(findCombo);
        findLabel.setText("Find what:");

        findCombo.setEditable(true);

        searchLabel.setLabelFor(searchCombo);
        searchLabel.setText("Search within:");

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Options:"));

        checkRegular.setText("Use regular expression pattern matching");

        link.setText("View more information on searching using regular expressions");

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

        bSearch.setText("Search");

        hintLabel.setText("Click on any result to view it within the main window");

        bHelp.setText("Help");

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
                                .add(findCombo, 0, 146, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(bSearch)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(bHelp))
                            .add(searchCombo, 0, 276, Short.MAX_VALUE)))
                    .add(resultLabel)
                    .add(hintLabel)
                    .add(spTable, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(findLabel)
                    .add(findCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(bSearch)
                    .add(bHelp))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(searchLabel)
                    .add(searchCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(resultLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spTable, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(hintLabel)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bHelp;
    javax.swing.JButton bSearch;
    private javax.swing.JCheckBox checkCase;
    private javax.swing.JCheckBox checkRegular;
    javax.swing.JComboBox findCombo;
    private javax.swing.JLabel findLabel;
    private javax.swing.JLabel hintLabel;
    private javax.swing.JLabel link;
    private javax.swing.JPanel panel;
    javax.swing.JLabel resultLabel;
    private javax.swing.JComboBox searchCombo;
    private javax.swing.JLabel searchLabel;
    private javax.swing.JScrollPane spTable;
    javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}