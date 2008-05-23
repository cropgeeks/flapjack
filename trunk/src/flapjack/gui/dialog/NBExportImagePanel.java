package flapjack.gui.dialog;

import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.data.*;
import flapjack.gui.visualization.*;
import flapjack.gui.*;

class NBExportImagePanel extends JPanel implements ChangeListener, ActionListener
{
	private DecimalFormat d = new DecimalFormat("0.00");
	private GenotypePanel gPanel;

	private int lines, markers;
	private float lineMarkerRatio;

	SpinnerNumberModel widthModel, heightModel;

    public NBExportImagePanel(GenotypePanel gPanel, MouseAdapter dcl)
    {
		initComponents();

		this.gPanel = gPanel;

		//i18n
		panel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.NBExportImagePanel.title")));
		RB.setText(rWindow, "gui.dialog.NBExportImagePanel.rWindow");
		RB.setText(rWindowLabel, "gui.dialog.NBExportImagePanel.rWindowLabel");
		RB.setText(rView, "gui.dialog.NBExportImagePanel.rView");
		RB.setText(rViewLabel, "gui.dialog.NBExportImagePanel.rViewLabel");
		RB.setText(rOverview, "gui.dialog.NBExportImagePanel.rOverview");
		RB.setText(rOverviewLabel, "gui.dialog.NBExportImagePanel.rOverviewLabel");
		RB.setText(widthLabel, "gui.dialog.NBExportImagePanel.widthLabel");
		RB.setText(heightLabel, "gui.dialog.NBExportImagePanel.heightLabel");
		RB.setText(equalCheck, "gui.dialog.NBExportImagePanel.equalCheck");
		RB.setText(memLabel1, "gui.dialog.NBExportImagePanel.memLabel1");

		lines = gPanel.getView().getLineCount();
		markers = gPanel.getView().getMarkerCount();

		lineMarkerRatio = (float)lines / (float)markers;

		rWindow.addActionListener(this);
		rWindow.addMouseListener(dcl);
		rWindow.setSelected(Prefs.guiExportImageMethod == 0);
		rView.addActionListener(this);
		rView.addMouseListener(dcl);
		rView.setSelected(Prefs.guiExportImageMethod == 1);
		rOverview.addActionListener(this);
		rOverview.addMouseListener(dcl);
		rOverview.setSelected(Prefs.guiExportImageMethod == 2);
		equalCheck.addActionListener(this);

		widthModel = new SpinnerNumberModel(markers, 1, 5 * markers, 1);
		widthSpin.setModel(widthModel);
		widthSpin.addChangeListener(this);

		heightModel = new SpinnerNumberModel(lines, 1, 5 * lines, 1);
		heightSpin.setModel(heightModel);
		heightSpin.addChangeListener(this);

		// The slider is tied to the value of the width input field
		slider.setMinimum(1);
		slider.setMaximum(5 * markers);
		slider.setValue(markers);
		slider.addChangeListener(this);

		setMemoryText();
		setCheckBoxState();
		setEnabledStates();
    }

    public void stateChanged(ChangeEvent e)
    {
    	// Remove the listeners so setting "a" doesn't generate events on "b"
    	// which then generates on "a" (until we run out of memory)
    	widthSpin.removeChangeListener(this);
    	heightSpin.removeChangeListener(this);
    	slider.removeChangeListener(this);

		// When a spinner is changed, modify the other one by the correct ratio
		// and update the slider's value
    	if (e.getSource() instanceof JSpinner)
    	{
    		int w = widthModel.getNumber().intValue();
    		int h = heightModel.getNumber().intValue();

    		if (e.getSource() == widthSpin)
    			heightModel.setValue(Math.round(w * lineMarkerRatio));
    		else
    			widthModel.setValue(Math.round(h / lineMarkerRatio));

    		slider.setValue(w);
    	}

    	// When the slider is changed, modify the two spinners
    	else if (e.getSource() instanceof JSlider)
    	{
    		int w = slider.getValue();

    		widthModel.setValue(w);
    		heightModel.setValue(Math.round(w * lineMarkerRatio));
    	}

    	widthSpin.addChangeListener(this);
    	heightSpin.addChangeListener(this);
    	slider.addChangeListener(this);

    	setMemoryText();
    	setCheckBoxState();
    }

    private void setMemoryText()
    {
    	long memory = 0;

    	if (rWindow.isSelected())
    		memory = gPanel.computeCanvasViewPortBufferInBytes();
    	else if (rView.isSelected())
    		memory = gPanel.computeCanvasBufferInBytes();
    	else
    	{
    		long w = widthModel.getNumber().longValue();
    		long h = heightModel.getNumber().longValue();

    		memory = w * h * 3;
    	}

    	if (memory < Math.pow(1024, 2))
    		memLabel2.setText((long)(memory/1024f) + " kB");

    	else if (memory < Math.pow(1024, 3))
    		memLabel2.setText(d.format(memory/1024f/1024f) + " MB");

    	else
    		memLabel2.setText(d.format(memory/1024f/1024f/1024f) + " GB");
    }

    public void actionPerformed(ActionEvent e)
    {
    	if (e.getSource() == equalCheck && equalCheck.isSelected())
    		widthModel.setValue(markers);

    	else if (e.getSource() instanceof JRadioButton)
    	{
    		setEnabledStates();
    		setMemoryText();
    	}
    }

	private void setCheckBoxState()
	{
		long w = slider.getValue();
    	equalCheck.setSelected(w == markers);
	}

	private void setEnabledStates()
	{
		boolean state = true;

		if (rWindow.isSelected() || rView.isSelected())
			state = false;

		widthLabel.setEnabled(state);
		widthSpin.setEnabled(state);
		heightLabel.setEnabled(state);
		heightSpin.setEnabled(state);
		slider.setEnabled(state);
		equalCheck.setEnabled(state);
	}

	boolean isOK()
	{
		// Which method was picked?
		if (rWindow.isSelected())
			Prefs.guiExportImageMethod = 0;
		else if (rView.isSelected())
			Prefs.guiExportImageMethod = 1;
		else
			Prefs.guiExportImageMethod = 2;

		// What image dimension was selected?
		Prefs.guiExportImageX = widthModel.getNumber().intValue();
		Prefs.guiExportImageY = heightModel.getNumber().intValue();

		// TODO: Other checks for available memory
		return true;
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        panel = new javax.swing.JPanel();
        rWindow = new javax.swing.JRadioButton();
        rWindowLabel = new javax.swing.JLabel();
        rView = new javax.swing.JRadioButton();
        rViewLabel = new javax.swing.JLabel();
        rOverview = new javax.swing.JRadioButton();
        rOverviewLabel = new javax.swing.JLabel();
        widthLabel = new javax.swing.JLabel();
        widthSpin = new javax.swing.JSpinner();
        heightLabel = new javax.swing.JLabel();
        heightSpin = new javax.swing.JSpinner();
        slider = new javax.swing.JSlider();
        equalCheck = new javax.swing.JCheckBox();
        memLabel1 = new javax.swing.JLabel();
        memLabel2 = new javax.swing.JLabel();

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Select a method of exporting:"));

        buttonGroup1.add(rWindow);
        rWindow.setText("Export only what can currently be seen");

        rWindowLabel.setText("(creates a high-quality image showing exactly what you currently see)");

        buttonGroup1.add(rView);
        rView.setText("Export all of the current view");

        rViewLabel.setText("(creates a high-quality image showing everything Flapjack is currently rendering)");

        buttonGroup1.add(rOverview);
        rOverview.setText("Export a scaled-to-fit image of all of the data:");

        rOverviewLabel.setText("(creates an overview image using the dimensions specified below)");

        widthLabel.setLabelFor(widthSpin);
        widthLabel.setText("Width (pixels):");

        heightLabel.setLabelFor(heightSpin);
        heightLabel.setText("Height (pixels):");

        equalCheck.setMnemonic('p');
        equalCheck.setText("set dimension equal to no. of markers by no. of lines");

        memLabel1.setText("Estimated memory required for exporting:");

        memLabel2.setText("<memory>");

        org.jdesktop.layout.GroupLayout panelLayout = new org.jdesktop.layout.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(rOverview)
                    .add(panelLayout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(rViewLabel))
                    .add(rView)
                    .add(panelLayout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(rWindowLabel))
                    .add(rWindow)
                    .add(panelLayout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(panelLayout.createSequentialGroup()
                                .add(widthLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(widthSpin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, 18)
                                .add(heightLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(heightSpin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, 18)
                                .add(slider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 119, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(equalCheck)
                            .add(rOverviewLabel)))
                    .add(panelLayout.createSequentialGroup()
                        .add(memLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(memLabel2)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(rWindow)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rWindowLabel)
                .add(18, 18, 18)
                .add(rView)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rViewLabel)
                .add(18, 18, 18)
                .add(rOverview)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rOverviewLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(widthLabel)
                    .add(widthSpin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(heightSpin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(heightLabel)
                    .add(slider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(equalCheck)
                .add(18, 18, 18)
                .add(panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(memLabel2)
                    .add(memLabel1))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .add(panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox equalCheck;
    private javax.swing.JLabel heightLabel;
    private javax.swing.JSpinner heightSpin;
    private javax.swing.JLabel memLabel1;
    private javax.swing.JLabel memLabel2;
    private javax.swing.JPanel panel;
    private javax.swing.JRadioButton rOverview;
    private javax.swing.JLabel rOverviewLabel;
    private javax.swing.JRadioButton rView;
    private javax.swing.JLabel rViewLabel;
    private javax.swing.JRadioButton rWindow;
    private javax.swing.JLabel rWindowLabel;
    private javax.swing.JSlider slider;
    private javax.swing.JLabel widthLabel;
    private javax.swing.JSpinner widthSpin;
    // End of variables declaration//GEN-END:variables
}