// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

import flapjack.io.*;

import scri.commons.gui.*;

class ImportSampleTabNB extends JPanel implements ActionListener, Runnable
{
	private Properties properties;
	private boolean isOK = false;

	private JButton bImport;

	public ImportSampleTabNB(JButton bImport)
	{
		initComponents();

		this.bImport = bImport;

		setBackground((Color)UIManager.get("fjDialogBG"));
		panel.setBackground((Color)UIManager.get("fjDialogBG"));

		panel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.NBImportSamplePanel.panel")));
		RB.setText(tabLabel, "gui.dialog.NBImportSamplePanel.tabLabel");

		RB.setText(serverLabel, "gui.dialog.NBImportSamplePanel.serverLabel.connecting");
		serverLabel.setIcon(Icons.getIcon("TIMERON"));

		combo.addActionListener(this);

		new Thread(this).start();
	}

	public void actionPerformed(ActionEvent e)
	{
		updateDetailsBox(combo.getSelectedIndex());
	}

	private void updateDetailsBox(int index)
	{
		ExampleProject proj = (ExampleProject) combo.getItemAt(index);

		if (proj != null)
			text.setText(proj.des);
	}

	FlapjackFile getProject()
	{
		String url = ((ExampleProject) combo.getSelectedItem()).url;

		return new FlapjackFile(url);
	}

	public void run()
	{
		try
		{
			// The folder on the server
			URL url = new URL("http://bioinf.hutton.ac.uk/flapjack/sample-data/sample.xml");

			properties = new Properties();
			properties.loadFromXML(url.openStream());

			ArrayList<ExampleProject> list = new ArrayList<>();

			// Get a list of all the projects
			Enumeration<Object> e = properties.keys();
			while (e.hasMoreElements())
			{
				ExampleProject proj = new ExampleProject();

				proj.name = (String) e.nextElement();
				String value = properties.getProperty(proj.name);
				proj.url = value.substring(0, value.indexOf(";"));
				proj.des = value.substring(value.indexOf(";")+1);
				proj.idx = Integer.parseInt(proj.name.substring(0, 1));

				list.add(proj);
				Collections.sort(list);
			}

			// Then add them to the combo box
			for (ExampleProject proj: list)
				combo.addItem(proj);

			serverLabel.setText(
				RB.format("gui.dialog.NBImportSamplePanel.serverLabel.ok",
				list.size()));

			isOK = true;
			bImport.setEnabled(true);
			combo.setEnabled(true);
		}
		catch(Exception e)
		{
			RB.setText(serverLabel, "gui.dialog.NBImportSamplePanel.serverLabel.error");
			serverLabel.setForeground(Color.red);
		}

		serverLabel.setIcon(Icons.getIcon("CHECKUPDATE"));
	}

	// Stores details on each project, along with being to sort them too
	// (based on their name containing "1: Blah..." as part of it
	private static class ExampleProject implements Comparable<ExampleProject>
	{
		String url, name, des;
		int idx;

		public String toString()
			{ return name.substring(name.indexOf(":")+1); }

		public int compareTo(ExampleProject other)
		{
			if (idx < other.idx)
				return -1;
			if (idx == other.idx)
				return 0;
			return 1;
		}
	}

	boolean isOK()
		{ return isOK; }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        combo = new javax.swing.JComboBox<ExampleProject>();
        jScrollPane1 = new javax.swing.JScrollPane();
        text = new javax.swing.JTextArea();
        serverLabel = new javax.swing.JLabel();
        tabLabel = new javax.swing.JLabel();

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Available example data:"));

        combo.setEnabled(false);

        text.setColumns(20);
        text.setEditable(false);
        text.setLineWrap(true);
        text.setRows(2);
        text.setWrapStyleWord(true);
        jScrollPane1.setViewportView(text);

        serverLabel.setText("Server message...");

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                    .addComponent(combo, javax.swing.GroupLayout.Alignment.TRAILING, 0, 251, Short.MAX_VALUE)
                    .addComponent(serverLabel))
                .addContainerGap())
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(serverLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabLabel.setText("Status message...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabLabel))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabLabel)
                .addGap(18, 18, 18)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JComboBox<ExampleProject> combo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panel;
    private javax.swing.JLabel serverLabel;
    private javax.swing.JLabel tabLabel;
    javax.swing.JTextArea text;
    // End of variables declaration//GEN-END:variables
}