package flapjack.gui.visualization;

import java.text.*;
import javax.swing.*;

import flapjack.data.*;

class StatusPanel extends JPanel
{
	private DecimalFormat d = new DecimalFormat("0.0");

	private GenotypePanel gPanel;
	private GTView view;

	StatusPanel(GenotypePanel gPanel)
	{
		this.gPanel = gPanel;

		initComponents();

		lineLabel.setText("");
		markerLabel.setText("");

		sizeSlider.setMinimum(1);
		sizeSlider.setMaximum(25);
		sizeSlider.setValue(7);
		sizeSlider.addChangeListener(gPanel);
	}

	JSlider getSlider()
		{ return sizeSlider; }

	void setView(GTView view)
	{
		this.view = view;
	}

	void setLineIndex(int lineIndex)
	{
		if (lineIndex < 0 || lineIndex >= view.getLineCount())
			lineLabel.setText("");

		else
			lineLabel.setText(view.getLine(lineIndex).getName());
	}

	void setMarkerIndex(int markerIndex)
	{
		if (markerIndex < 0 || markerIndex >= view.getMarkerCount())
			markerLabel.setText("");

		else
		{
			Marker m = view.getMarker(markerIndex);
			markerLabel.setText(m.getName() + " (" + d.format(m.getPosition()) + ")");
		}
	}

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        sizeSlider = new javax.swing.JSlider();
        label2 = new javax.swing.JLabel();
        label3 = new javax.swing.JLabel();
        markerLabel = new javax.swing.JLabel();
        lineLabel = new javax.swing.JLabel();

        label2.setText("Line:");

        label3.setText("Marker:");

        markerLabel.setForeground(java.awt.Color.red);
        markerLabel.setText("textextextextextextextext");

        lineLabel.setForeground(new java.awt.Color(255, 0, 0));
        lineLabel.setText("textextextextextextextext");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label2)
                    .addComponent(label3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(markerLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lineLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                .addComponent(sizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label2)
                            .addComponent(lineLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label3)
                            .addComponent(markerLabel)))
                    .addComponent(sizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel label2;
    private javax.swing.JLabel label3;
    private javax.swing.JLabel lineLabel;
    private javax.swing.JLabel markerLabel;
    private javax.swing.JSlider sizeSlider;
    // End of variables declaration//GEN-END:variables
}
