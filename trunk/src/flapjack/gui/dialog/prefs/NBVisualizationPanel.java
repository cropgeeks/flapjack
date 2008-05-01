package flapjack.gui.dialog.prefs;

import java.awt.event.*;
import static java.awt.image.BufferedImage.*;
import javax.swing.*;

import flapjack.gui.*;

class NBVisualizationPanel extends JPanel implements IPrefsTab, ActionListener
{
	public NBVisualizationPanel()
    {
        initComponents();

		checkVisBackBuffer.setText(RB.getString("gui.dialog.prefs.NBVisualizationPanel.checkVisBackBuffer"));
        checkVisBackBuffer.addActionListener(this);
		RB.setMnemonic(checkVisBackBuffer, "gui.dialog.prefs.NBVisualizationPanel.checkVisBackBuffer");

        checkVisBackBufferType.setText(RB.getString("gui.dialog.prefs.NBVisualizationPanel.checkVisBackBufferType"));
		RB.setMnemonic(checkVisBackBufferType, "gui.dialog.prefs.NBVisualizationPanel.checkVisBackBufferType");

        initSettings();
    }

    public void actionPerformed(ActionEvent e)
    {
    	if (e.getSource() == checkVisBackBuffer)
    		checkVisBackBufferType.setEnabled(checkVisBackBuffer.isSelected());
    }

    private void initSettings()
    {
    	checkVisBackBuffer.setSelected(Prefs.visBackBuffer);
    	checkVisBackBufferType.setSelected(Prefs.visBackBufferType == TYPE_BYTE_INDEXED);
    	checkVisBackBufferType.setEnabled(checkVisBackBuffer.isSelected());
    }

	public void applySettings()
	{
		Prefs.visBackBuffer = checkVisBackBuffer.isSelected();
		Prefs.visBackBufferType = checkVisBackBufferType.isSelected() ? TYPE_BYTE_INDEXED : TYPE_INT_RGB;
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

        checkVisBackBuffer = new javax.swing.JCheckBox();
        checkVisBackBufferType = new javax.swing.JCheckBox();

        checkVisBackBuffer.setText("Attempt to back-buffer the main canvas to improve performance");

        checkVisBackBufferType.setText("Use an 8 bit colour buffer to reduce memory usage");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(checkVisBackBufferType))
                    .add(checkVisBackBuffer))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(checkVisBackBuffer)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(checkVisBackBufferType)
                .addContainerGap(126, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkVisBackBuffer;
    private javax.swing.JCheckBox checkVisBackBufferType;
    // End of variables declaration//GEN-END:variables
}