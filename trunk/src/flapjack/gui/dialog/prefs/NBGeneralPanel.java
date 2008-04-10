package flapjack.gui.dialog.prefs;

import javax.swing.*;

import flapjack.gui.*;

class NBGeneralPanel extends JPanel implements IPrefsTab
{
	private DefaultComboBoxModel displayModel;
	private DefaultComboBoxModel updateModel;

    public NBGeneralPanel()
    {
        initComponents();

		// Interface settings
        displayLabel.setText(RB.getString("gui.dialog.prefs.NBGeneralPanel.displayLabel"));
        displayHint.setText(RB.getString("gui.dialog.prefs.NBGeneralPanel.displayHint"));

        displayModel = new DefaultComboBoxModel();
        displayModel.addElement(RB.getString("gui.dialog.prefs.NBGeneralPanel.auto"));
        displayModel.addElement(RB.getString("gui.dialog.prefs.NBGeneralPanel.en_GB"));
        displayModel.addElement(RB.getString("gui.dialog.prefs.NBGeneralPanel.en_US"));
        displayModel.addElement(RB.getString("gui.dialog.prefs.NBGeneralPanel.de"));
        displayCombo.setModel(displayModel);


        // Update settings
        updateLabel.setText(RB.getString("gui.dialog.prefs.NBGeneralPanel.updateLabel"));

        updateModel = new DefaultComboBoxModel();
        updateModel.addElement(RB.getString("gui.dialog.prefs.NBGeneralPanel.updateNever"));
        updateModel.addElement(RB.getString("gui.dialog.prefs.NBGeneralPanel.updateStartup"));
        updateModel.addElement(RB.getString("gui.dialog.prefs.NBGeneralPanel.updateDaily"));
        updateModel.addElement(RB.getString("gui.dialog.prefs.NBGeneralPanel.updateWeekly"));
        updateModel.addElement(RB.getString("gui.dialog.prefs.NBGeneralPanel.updateMonthly"));
        updateCombo.setModel(updateModel);


        initSettings();
    }

    private int getLocaleIndex()
	{
		if (Prefs.localeText.equals("en_GB"))
			return 1;
		else if (Prefs.localeText.equals("en_US"))
			return 2;
		else if (Prefs.localeText.equals("de"))
			return 3;
		else
			return 0;
	}

	private void initSettings()
    {
    	displayCombo.setSelectedIndex(getLocaleIndex());
    	updateCombo.setSelectedIndex(Prefs.guiUpdateSchedule);
    }

	public void applySettings()
	{
		switch (displayCombo.getSelectedIndex())
		{
			case 1:  Prefs.localeText = "en_GB"; break;
			case 2:  Prefs.localeText = "en_US"; break;
			case 3:  Prefs.localeText = "de";    break;
			default: Prefs.localeText = "auto";
		}

		Prefs.guiUpdateSchedule = updateCombo.getSelectedIndex();
	}

	public void setDefaults()
	{
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        displayLabel = new javax.swing.JLabel();
        displayCombo = new javax.swing.JComboBox();
        displayHint = new javax.swing.JLabel();
        updateLabel = new javax.swing.JLabel();
        updateCombo = new javax.swing.JComboBox();

        displayLabel.setDisplayedMnemonic('i');
        displayLabel.setLabelFor(displayCombo);
        displayLabel.setText("Interface display language:");

        displayHint.setText("(Restart Flapjack to apply)");

        updateLabel.setDisplayedMnemonic('c');
        updateLabel.setText("Check for updates at startup:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(displayLabel)
                    .add(updateLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(displayCombo, 0, 181, Short.MAX_VALUE)
                    .add(displayHint)
                    .add(updateCombo, 0, 181, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(displayLabel)
                    .add(displayCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(displayHint)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(updateLabel)
                    .add(updateCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(90, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox displayCombo;
    private javax.swing.JLabel displayHint;
    private javax.swing.JLabel displayLabel;
    private javax.swing.JComboBox updateCombo;
    private javax.swing.JLabel updateLabel;
    // End of variables declaration//GEN-END:variables
}