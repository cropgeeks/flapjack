// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog;

import java.awt.event.*;
import java.io.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

import jhi.flapjack.gui.*;
import jhi.flapjack.gui.visualization.*;
import jhi.flapjack.io.*;

import scri.commons.gui.*;

public class ExportImageDialog extends JDialog implements ActionListener, ChangeListener
{
	private File file = null;
	private DecimalFormat d = new DecimalFormat("0.00");
	private DblClickListener dcl;

	private GenotypePanel gPanel;

	private int lines, markers;
	private float lineMarkerRatio;

	private SpinnerNumberModel widthModel, heightModel;

	public ExportImageDialog(GenotypePanel gPanel)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.ExportImageDialog.title"),
			true
		);

		this.gPanel = gPanel;
		dcl = new DblClickListener();

		initComponents();
		initComponents2();

		FlapjackUtils.initDialog(this, bExport, bCancel, true, getContentPane(), panel);
	}

	private void initComponents2()
	{
		RB.setText(bExport, "gui.dialog.ExportImageDialog.bExport");
		bExport.addActionListener(this);
		RB.setText(bCancel, "gui.text.cancel");
		bCancel.addActionListener(this);
		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "export_image.html");

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

		lines = gPanel.getView().lineCount();
		markers = gPanel.getView().markerCount();

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

		ButtonGroup bGroup = new ButtonGroup();
		bGroup.add(rWindow);
		bGroup.add(rView);
		bGroup.add(rOverview);

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

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bExport && isOK() && promptForFilename())
			export();

		else if (e.getSource() == bCancel)
			setVisible(false);

		else if (e.getSource() == equalCheck && equalCheck.isSelected())
    		widthModel.setValue(markers);

    	else if (e.getSource() instanceof JRadioButton)
    	{
    		setEnabledStates();
    		setMemoryText();
    	}
	}

	private class DblClickListener extends MouseAdapter
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount() != 2)
				return;

			bExport.doClick();
		}
	}

	@Override
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

	private void export()
	{
		ImageExporter exporter = new ImageExporter(gPanel, file);

		ProgressDialog dialog = new ProgressDialog(exporter,
			 RB.format("gui.dialog.ExportImageDialog.title"),
			 RB.format("gui.dialog.ExportImageDialog.label"),
			 Flapjack.winMain);

		// If the operation failed or was cancelled...
		if (dialog.failed("gui.error"))
			return;

		TaskDialog.info(
			RB.format("gui.dialog.ExportImageDialog.success", file),
			RB.getString("gui.text.close"));
	}

	private boolean promptForFilename()
	{
		File basename = new File(Prefs.guiCurrentDir, "Image.png");

		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("other.Filters.png"), "png");

		String filename = FlapjackUtils.getSaveFilename(
			RB.getString("gui.dialog.ExportImageDialog.saveDialog"), basename, filter);

		if (filename != null)
			file = new File(filename);

		return file != null;
	}

    private void setMemoryText()
    {
    	long memory = 0;

    	if (rWindow.isSelected())
    		memory = gPanel.getController().computeCanvasViewPortBufferInBytes();
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

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        dialogPanel1 = new scri.commons.gui.matisse.DialogPanel();
        bExport = new javax.swing.JButton();
        bCancel = new javax.swing.JButton();
        bHelp = new javax.swing.JButton();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        bExport.setText("Export...");
        dialogPanel1.add(bExport);

        bCancel.setText("Cancel");
        dialogPanel1.add(bCancel);

        bHelp.setText("Help");
        dialogPanel1.add(bHelp);

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Select a method of exporting:"));

        rWindow.setText("Export only what can currently be seen");

        rWindowLabel.setText("(creates a high-quality image showing exactly what you currently see)");

        rView.setText("Export all of the current view");

        rViewLabel.setText("(creates a high-quality image showing everything Flapjack is currently rendering)");

        rOverview.setText("Export a scaled-to-fit image of all of the data:");

        rOverviewLabel.setText("(creates an overview image using the dimensions specified below)");

        widthLabel.setText("Width (pixels):");

        heightLabel.setText("Height (pixels):");

        equalCheck.setMnemonic('p');
        equalCheck.setText("set dimension equal to no. of markers by no. of lines");

        memLabel1.setText("Estimated memory required for exporting:");

        memLabel2.setText("<memory>");

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rOverview)
                    .addComponent(rView)
                    .addComponent(rWindow)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addComponent(memLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(memLabel2))
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rViewLabel)
                            .addComponent(rWindowLabel)
                            .addGroup(panelLayout.createSequentialGroup()
                                .addComponent(widthLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(widthSpin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(heightLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(heightSpin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(slider, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(equalCheck)
                            .addComponent(rOverviewLabel))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rWindow)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rWindowLabel)
                .addGap(18, 18, 18)
                .addComponent(rView)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rViewLabel)
                .addGap(18, 18, 18)
                .addComponent(rOverview)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rOverviewLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(widthLabel)
                    .addComponent(widthSpin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(heightSpin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(heightLabel)
                    .addComponent(slider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(equalCheck)
                .addGap(18, 18, 18)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(memLabel2)
                    .addComponent(memLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dialogPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(4, 4, 4)
                .addComponent(dialogPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bCancel;
    private javax.swing.JButton bExport;
    private javax.swing.JButton bHelp;
    private scri.commons.gui.matisse.DialogPanel dialogPanel1;
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