package flapjack.gui.dialog;

import java.awt.event.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

class NBNewViewPanel extends javax.swing.JPanel implements ActionListener
{
	private DefaultComboBoxModel model = new DefaultComboBoxModel();

	public NBNewViewPanel(DataSet dataSet, GTViewSet currentViewSet)
	{
		initComponents();

		// i18n
		panel.setBorder(BorderFactory.createTitledBorder(
			RB.getString("gui.dialog.NBNewViewPanel.panelTitle")));
		rNewView.setText(RB.getString("gui.dialog.NBNewViewPanel.rNewView"));
		RB.setMnemonic(rNewView, "gui.dialog.NBNewViewPanel.rNewView");
		rCloneView.setText(RB.getString("gui.dialog.NBNewViewPanel.rCloneView"));
		RB.setMnemonic(rCloneView, "gui.dialog.NBNewViewPanel.rCloneView");
		cloneLabel.setText(RB.getString("gui.dialog.NBNewViewPanel.cloneLabel"));
		RB.setMnemonic(cloneLabel, "gui.dialog.NBNewViewPanel.cloneLabel");
		nameLabel.setText(RB.getString("gui.dialog.NBNewViewPanel.nameLabel"));
		RB.setMnemonic(nameLabel, "gui.dialog.NBNewViewPanel.nameLabel");

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

		rCloneView.setMnemonic(KeyEvent.VK_C);
		rCloneView.setDisplayedMnemonicIndex(28);
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
        cloneCombo = new javax.swing.JComboBox();
        nameLabel = new javax.swing.JLabel();
        nameText = new javax.swing.JTextField();
        cloneLabel = new javax.swing.JLabel();

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

        org.jdesktop.layout.GroupLayout panelLayout = new org.jdesktop.layout.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(rNewView)
                    .add(panelLayout.createSequentialGroup()
                        .add(4, 4, 4)
                        .add(nameLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(nameText))
                    .add(panelLayout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(cloneLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cloneCombo, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(rCloneView))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(rNewView)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rCloneView)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(cloneLabel)
                    .add(cloneCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(21, 21, 21)
                .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(nameLabel)
                    .add(nameText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    javax.swing.JComboBox cloneCombo;
    private javax.swing.JLabel cloneLabel;
    private javax.swing.JLabel nameLabel;
    javax.swing.JTextField nameText;
    private javax.swing.JPanel panel;
    private javax.swing.JRadioButton rCloneView;
    private javax.swing.JRadioButton rNewView;
    // End of variables declaration//GEN-END:variables
}