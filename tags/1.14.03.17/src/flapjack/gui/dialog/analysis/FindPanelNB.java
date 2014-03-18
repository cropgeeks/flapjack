// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import flapjack.gui.*;

import scri.commons.gui.*;

class FindPanelNB extends JPanel implements ActionListener
{
	private FindDialog findDialog;
	DefaultTableModel tableModel;

	FindPanelNB(final FindDialog findDialog)
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

		findComboBox.setHistory(Prefs.guiFindHistory);
		findComboBox.addActionListener(this);

		table.getSelectionModel().addListSelectionListener(findDialog);

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
			findDialog.setTableModel(null);
			findDialog.runSearch();
		}

		else if (e.getSource() == bSearch || e.getSource() == findComboBox)
		{
			Prefs.guiFindHistory = findComboBox.getHistory();
			findDialog.runSearch();
		}
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
		String str = findComboBox.getText();

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
        searchLabel = new javax.swing.JLabel();
        searchCombo = new javax.swing.JComboBox<String>();
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
        findComboBox = new scri.commons.gui.matisse.HistoryComboBox();

        findLabel.setLabelFor(findComboBox);
        findLabel.setText("Find what:");

        searchLabel.setLabelFor(searchCombo);
        searchLabel.setText("Search within:");

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Options:"));

        checkRegular.setText("Use regular expression pattern matching");

        link.setText("View more information on searching using regular expressions");

        checkCase.setText("Match case");

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkCase)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(link))
                    .addComponent(checkRegular))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkCase)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkRegular)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(link)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(searchLabel)
                            .addComponent(findLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(findComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bHelp))
                            .addComponent(searchCombo, 0, 280, Short.MAX_VALUE)))
                    .addComponent(resultLabel)
                    .addComponent(hintLabel)
                    .addComponent(spTable, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(findLabel)
                    .addComponent(bSearch)
                    .addComponent(bHelp)
                    .addComponent(findComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchLabel)
                    .addComponent(searchCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(resultLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spTable, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hintLabel)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bHelp;
    javax.swing.JButton bSearch;
    private javax.swing.JCheckBox checkCase;
    private javax.swing.JCheckBox checkRegular;
    scri.commons.gui.matisse.HistoryComboBox findComboBox;
    private javax.swing.JLabel findLabel;
    private javax.swing.JLabel hintLabel;
    private javax.swing.JLabel link;
    private javax.swing.JPanel panel;
    javax.swing.JLabel resultLabel;
    private javax.swing.JComboBox<String> searchCombo;
    private javax.swing.JLabel searchLabel;
    private javax.swing.JScrollPane spTable;
    javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}