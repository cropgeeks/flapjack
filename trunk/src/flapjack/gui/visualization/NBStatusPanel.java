package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.data.*;
import flapjack.gui.*;

public class NBStatusPanel extends JPanel implements ActionListener, ChangeListener
{
	private DecimalFormat d1 = new DecimalFormat("0.0");
	private DecimalFormat d3 = new DecimalFormat("0.000");

	private GenotypePanel gPanel;
	private GTView view;

	NBStatusPanel(GenotypePanel gPanel)
	{
		this.gPanel = gPanel;

		initComponents();

		RB.setText(label1, "gui.visualization.StatusPanel.line");
		RB.setText(horizontalLabel, "gui.visualization.StatusPanel.horizontalLabel");
		RB.setText(checkLink, "gui.visualization.StatusPanel.checkLink");

		lineLabel.setForeground(Color.red);
		lineLabel.setText(" ");
		markerLabel.setForeground(Color.red);
		markerLabel.setText(" ");
		alleleLabel.setForeground(Color.red);
		alleleLabel.setText(" ");

		sliderX.addChangeListener(this);
		sliderX.setBackground((Color)UIManager.get("Panel.background"));
		sliderY.addChangeListener(this);
		sliderY.setBackground((Color)UIManager.get("Panel.background"));
		checkLink.addActionListener(this);
		checkLink.setBackground((Color)UIManager.get("Panel.background"));

		setControlStates();
		setForMainUse();
	}

	void setForMainUse()
	{
		RB.setText(label2, "gui.visualization.StatusPanel.marker");
		RB.setText(label3, "gui.visualization.StatusPanel.genotype");
	}

	void setForHeatmapUse()
	{
		RB.setText(label2, "gui.visualization.StatusPanel.trait");
		RB.setText(label3, "gui.visualization.StatusPanel.value");
	}

	void setForFeatureUse()
	{
		RB.setText(label1, "gui.visualization.StatusPanel.featureTrait");
		RB.setText(label2, "gui.visualization.StatusPanel.featureExperiment");
		RB.setText(label3, "gui.visualization.StatusPanel.featureData");
	}

	// Toggles the state of the slider controls based on whether or not advanced
	// zoom has been turned on or off (which hides/changes text on the controls)
	public static void setControlStates()
	{
		if (Prefs.visAdvancedZoom)
			RB.setText(verticalLabel, "gui.visualization.StatusPanel.verticalLabel");
		else
		{
			RB.setText(verticalLabel, "gui.visualization.StatusPanel.zoomLabel");
			Prefs.visLinkSliders = true;
		}

		horizontalLabel.setVisible(Prefs.visAdvancedZoom);
		sliderX.setVisible(Prefs.visAdvancedZoom);
		checkLink.setVisible(Prefs.visAdvancedZoom);
		checkLink.setSelected(Prefs.visLinkSliders);
	}

	public void actionPerformed(ActionEvent e)
	{
		Prefs.visLinkSliders = checkLink.isSelected();

		if (Prefs.visLinkSliders)
			sliderX.setValue(sliderY.getValue());
	}

	public void stateChanged(ChangeEvent e)
	{
		// Only force an update once (if linked, both sliders generate events,
		// remove the changeListener on the *other* slider first)

		// Horizontal slider events...
		if (e.getSource() == sliderX && Prefs.visLinkSliders)
		{
			sliderY.removeChangeListener(this);
			sliderY.setValue(sliderX.getValue());
			sliderY.addChangeListener(this);
		}

		// Vertical slider events...
		else if (e.getSource() == sliderY && Prefs.visLinkSliders)
		{
			sliderX.removeChangeListener(this);
			sliderX.setValue(sliderY.getValue());
			sliderX.addChangeListener(this);
		}

		gPanel.computePanelSizes();
	}

	int getZoomX()
		{ return sliderX.getValue(); }

	int getZoomY()
		{ return sliderY.getValue(); }

	void setZoomY(int value)
		{ sliderY.setValue(value); }

	void setView(GTView view)
		{ this.view = view; }

	void setIndices(int lineIndex, int markerIndex)
	{
		// Current line under the mouse
		if (lineIndex < 0 || lineIndex >= view.getLineCount())
			lineLabel.setText(" ");
		else
		{
			String position = (lineIndex+1) + "/" + view.getLineCount();
			lineLabel.setText((view.getLine(lineIndex).getName() + " (" + position + ")").trim());
		}

		// Current marker under the mouse
		if (markerIndex < 0 || markerIndex >= view.getMarkerCount())
			markerLabel.setText(" ");
		else
		{
			Marker m = view.getMarker(markerIndex);
			markerLabel.setText(m.getName() + " (" + d1.format(m.getPosition()) + ")");
		}

		// Current allele under the mouse
		if (lineIndex < 0 || lineIndex >= view.getLineCount() ||
			markerIndex < 0 || markerIndex >= view.getMarkerCount())
		{
			alleleLabel.setText(" ");
		}
		else
		{
			int stateCode = view.getState(lineIndex, markerIndex);
			if (stateCode == 0)
				alleleLabel.setText(" ");
			else
			{
				StateTable st = view.getViewSet().getDataSet().getStateTable();
				AlleleState state = st.getAlleleState(stateCode);

				alleleLabel.setText(state.getRawData());
				alleleLabel.setText(state.format());
			}
		}
	}

	void setHeatmapValues(String line, String trait, String value)
	{
		lineLabel.setText(line);
		markerLabel.setText(trait);
		alleleLabel.setText(value);
	}

	// Sets the display text for a QTL
	void setQTLDetails(QTL qtl)
	{
		if (qtl != null)
		{
			// The QTL's trait and experiment values
			lineLabel.setText(qtl.getTrait());
			markerLabel.setText(qtl.getExperiment());

			// Build a string containing all the additional data (if any)
			String data = "";
			for (int i = 0; i < qtl.getVNames().length; i++)
			{
				if (i > 0)
					data += ", ";
				data += qtl.getVNames()[i]
					+ " (" + d3.format(qtl.getValues()[i]) + ")";
			}

			alleleLabel.setText(data);
		}
		else
		{
			lineLabel.setText(" ");
			markerLabel.setText(" ");
			alleleLabel.setText(" ");
		}
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        label1 = new javax.swing.JLabel();
        lineLabel = new javax.swing.JLabel();
        label2 = new javax.swing.JLabel();
        markerLabel = new javax.swing.JLabel();
        label3 = new javax.swing.JLabel();
        alleleLabel = new javax.swing.JLabel();
        horizontalLabel = new javax.swing.JLabel();
        sliderX = new javax.swing.JSlider();
        checkLink = new javax.swing.JCheckBox();
        verticalLabel = new javax.swing.JLabel();
        sliderY = new javax.swing.JSlider();

        label1.setText("Line:");

        lineLabel.setText("<>");

        label2.setText("Marker:");

        markerLabel.setText("<>");

        label3.setText("Genotype:");

        alleleLabel.setText("<>");

        horizontalLabel.setLabelFor(sliderX);
        horizontalLabel.setText("Horizontal zoom:");

        sliderX.setMaximum(25);
        sliderX.setMinimum(1);
        sliderX.setPaintTicks(true);
        sliderX.setSnapToTicks(true);
        sliderX.setValue(7);

        checkLink.setText("Link");

        verticalLabel.setLabelFor(sliderY);
        verticalLabel.setText("Vertical zoom:");

        sliderY.setMaximum(25);
        sliderY.setMinimum(1);
        sliderY.setPaintTicks(true);
        sliderY.setSnapToTicks(true);
        sliderY.setValue(7);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(label3)
                    .add(label1)
                    .add(label2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(alleleLabel)
                    .add(markerLabel)
                    .add(lineLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 99, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(horizontalLabel)
                    .add(sliderX, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 155, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(checkLink)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(sliderY, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 155, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(verticalLabel))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(layout.createSequentialGroup()
                            .add(label1)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(label2)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(label3))
                        .add(layout.createSequentialGroup()
                            .add(lineLabel)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(markerLabel)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(alleleLabel)))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(horizontalLabel)
                            .add(verticalLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(sliderX, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(sliderY, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(checkLink)))))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel alleleLabel;
    private static javax.swing.JCheckBox checkLink;
    private static javax.swing.JLabel horizontalLabel;
    private javax.swing.JLabel label1;
    private javax.swing.JLabel label2;
    private javax.swing.JLabel label3;
    private javax.swing.JLabel lineLabel;
    private javax.swing.JLabel markerLabel;
    private static javax.swing.JSlider sliderX;
    private static javax.swing.JSlider sliderY;
    private static javax.swing.JLabel verticalLabel;
    // End of variables declaration//GEN-END:variables
}