// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

class NewViewPanelNB extends javax.swing.JPanel implements ActionListener
{
	private DefaultComboBoxModel<GTViewSet> model = new DefaultComboBoxModel<>();

	public NewViewPanelNB(DataSet dataSet, GTViewSet currentViewSet)
	{
		initComponents();

		setBackground((Color)UIManager.get("fjDialogBG"));
		panel.setBackground((Color)UIManager.get("fjDialogBG"));

		// i18n
		panel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.NBNewViewPanel.panelTitle")));
		RB.setText(rNewView, "gui.dialog.NBNewViewPanel.rNewView");
		RB.setText(rCloneView, "gui.dialog.NBNewViewPanel.rCloneView");
		RB.setText(cloneLabel, "gui.dialog.NBNewViewPanel.cloneLabel");
		RB.setText(cloneDetails, "gui.dialog.NBNewViewPanel.cloneDetails");
		RB.setText(nameLabel, "gui.dialog.NBNewViewPanel.nameLabel");

		// Add existing view sets to the combo box model
		for (GTViewSet viewSet: dataSet.getViewSets())
			model.addElement(viewSet);
		// Then set the model and select a default value (if one is suitable)
		cloneCombo.setModel(model);
		if (currentViewSet != null)
			cloneCombo.setSelectedItem(currentViewSet);

		// Come up with a default name for the new view: "Custom View [n]"
		int viewCount = dataSet.getViewSets().size();
		nameText.setText(RB.format("gui.navpanel.VisualizationNode.customView",
			viewCount > 0 ? viewCount : 1));
		// Disable the clone view option if there are no view sets to clone from
		if (viewCount == 0)
			rCloneView.setEnabled(false);

		rNewView.addActionListener(this);
		rCloneView.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e)
	{
		boolean state = rCloneView.isSelected();
		cloneLabel.setEnabled(state);
		cloneCombo.setEnabled(state);
	}

	boolean createNewView()
	{
		return rNewView.isSelected();
	}

	void isOK()
	{

	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        panel = new javax.swing.JPanel();
        rNewView = new javax.swing.JRadioButton();
        rCloneView = new javax.swing.JRadioButton();
        cloneCombo = new javax.swing.JComboBox<GTViewSet>();
        nameLabel = new javax.swing.JLabel();
        nameText = new javax.swing.JTextField();
        cloneLabel = new javax.swing.JLabel();
        cloneDetails = new javax.swing.JLabel();

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("New view options:"));

        buttonGroup1.add(rNewView);
        rNewView.setSelected(true);
        rNewView.setText("Create a new view using all of the original data set");

        buttonGroup1.add(rCloneView);
        rCloneView.setText("Create a new view that is a clone of an existing view");

        cloneCombo.setEnabled(false);

        nameLabel.setLabelFor(nameText);
        nameLabel.setText("Name for this new view:");

        cloneLabel.setLabelFor(cloneCombo);
        cloneLabel.setText("Clone from:");
        cloneLabel.setEnabled(false);

        cloneDetails.setText("Only the genotype view is cloned: matrices, dendrograms, etc are not copied over");

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(rNewView)
                        .addComponent(rCloneView)
                        .addGroup(panelLayout.createSequentialGroup()
                            .addGap(21, 21, 21)
                            .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(cloneDetails)
                                .addGroup(panelLayout.createSequentialGroup()
                                    .addComponent(cloneLabel)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cloneCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(panelLayout.createSequentialGroup()
                        .addComponent(nameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nameText)))
                .addContainerGap())
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rNewView)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rCloneView)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cloneLabel)
                    .addComponent(cloneCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cloneDetails)
                .addGap(23, 23, 23)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    javax.swing.JComboBox<GTViewSet> cloneCombo;
    private javax.swing.JLabel cloneDetails;
    private javax.swing.JLabel cloneLabel;
    private javax.swing.JLabel nameLabel;
    javax.swing.JTextField nameText;
    private javax.swing.JPanel panel;
    private javax.swing.JRadioButton rCloneView;
    private javax.swing.JRadioButton rNewView;
    // End of variables declaration//GEN-END:variables
}