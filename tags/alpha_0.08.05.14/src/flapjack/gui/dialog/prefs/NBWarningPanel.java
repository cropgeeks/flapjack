package flapjack.gui.dialog.prefs;

import javax.swing.*;

import flapjack.gui.*;

class NBWarningPanel extends JPanel implements IPrefsTab
{
	public NBWarningPanel()
    {
        initComponents();
		
		panel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.prefs.NBWarningPanel.panelTitle")));

		warnDuplicateMarkers.setText(RB.getString("gui.dialog.prefs.NBWarningPanel.warnDuplicateMarkers"));
		RB.setMnemonic(warnDuplicateMarkers, "gui.dialog.prefs.NBWarningPanel.warnDuplicateMarkers");
		
		warnFindDialogResultsCleared.setText(RB.getString("gui.dialog.prefs.NBWarningPanel.warnFindDialogResultsCleared"));
		RB.setMnemonic(warnFindDialogResultsCleared, "gui.dialog.prefs.NBWarningPanel.warnFindDialogResultsCleared");

		initSettings();
    }

    private void initSettings()
    {
    	warnDuplicateMarkers.setSelected(Prefs.warnDuplicateMarkers);
		warnFindDialogResultsCleared.setSelected(Prefs.warnFindDialogResultsCleared);
    }

	public void applySettings()
	{
		Prefs.warnDuplicateMarkers = warnDuplicateMarkers.isSelected();
		Prefs.warnFindDialogResultsCleared = warnFindDialogResultsCleared.isSelected();
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

        panel = new javax.swing.JPanel();
        warnFindDialogResultsCleared = new javax.swing.JCheckBox();
        warnDuplicateMarkers = new javax.swing.JCheckBox();

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Inform me:"));

        warnFindDialogResultsCleared.setText("When the Find By Name dialog's results are cleared due to data changes");

        warnDuplicateMarkers.setText("When duplicate markers are found during data import");

        org.jdesktop.layout.GroupLayout panelLayout = new org.jdesktop.layout.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(warnDuplicateMarkers)
                    .add(warnFindDialogResultsCleared))
                .addContainerGap())
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(warnDuplicateMarkers)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(warnFindDialogResultsCleared)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(78, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel panel;
    private javax.swing.JCheckBox warnDuplicateMarkers;
    private javax.swing.JCheckBox warnFindDialogResultsCleared;
    // End of variables declaration//GEN-END:variables
}