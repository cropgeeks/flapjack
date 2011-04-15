// Copyright 2007-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.dialog;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import flapjack.gui.*;
import flapjack.gui.visualization.*;
import flapjack.gui.visualization.colors.*;

import scri.commons.gui.*;

class NBColorPanel extends JPanel implements ActionListener
{
	private GenotypePanel gPanel;

	private DefaultComboBoxModel comboModel;
	private DefaultListModel standardModel;
	private DefaultListModel schemeModel;

	NBColorPanel(GenotypePanel gPanel)
	{
		this.gPanel = gPanel;

		initComponents();

		setBackground((Color)UIManager.get("fjDialogBG"));
		gridPanel.setBackground((Color)UIManager.get("fjDialogBG"));
		infoPanel.setBackground((Color)UIManager.get("fjDialogBG"));
		colorPanel.setBackground((Color)UIManager.get("fjDialogBG"));

		// i18n text
		RB.setText(comboLabel, "gui.dialog.NBColorPanel.comboLabel");
		infoPanel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.NBColorPanel.infoPanel.title")));
		colorPanel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.dialog.NBColorPanel.colorPanel.title")));
		RB.setText(listLabel1, "gui.dialog.NBColorPanel.listLabel1");
		RB.setText(listLabel2, "gui.dialog.NBColorPanel.listLabel2");

		// Set the cell renderers and mouse listeners on the colour lists
		standardList.setCellRenderer(new ColorListRenderer());
		schemeList.setCellRenderer(new ColorListRenderer());
		addMouseListener(standardList);
		addMouseListener(schemeList);

		// Add the various colour schemes to the combo box
		comboModel = new DefaultComboBoxModel();
		comboModel.addElement(new NucleotideColorScheme());
		comboModel.addElement(new SimpleTwoColorScheme());
		comboModel.addElement(new LineSimilarityColorScheme());
		comboModel.addElement(new MarkerSimilarityColorScheme());
		comboModel.addElement(new AlleleFrequencyColorScheme());
		comboModel.addElement(new RandomColorScheme(ColorScheme.RANDOM));
		comboModel.addElement(new RandomColorScheme(ColorScheme.RANDOM_WSP));
		schemeCombo.setModel(comboModel);
		schemeCombo.addActionListener(this);

		initializeLists();

		// Match the combo box to the current scheme
		int model = gPanel.getViewSet().getColorScheme();
		for (int i = 0; i < comboModel.getSize(); i++)
			if (((ColorScheme)comboModel.getElementAt(i)).getModel() == model)
				schemeCombo.setSelectedIndex(i);
	}

	void initializeLists()
	{
		initStandardList();
		actionPerformed(null);
	}

	private void initStandardList()
	{
		standardModel = new DefaultListModel();

		for (ColorScheme.ColorSummary summary: ColorScheme.getStandardColorSummaries())
			standardModel.addElement(summary);
		standardList.setModel(standardModel);
	}

	// Change the entries in the 'scheme specific' list control based on the
	// selected colour scheme from the combo box
	public void actionPerformed(ActionEvent e)
	{
		ColorScheme cs = (ColorScheme) schemeCombo.getSelectedItem();
		infoText.setText(cs.getDescription());
		infoText.setCaretPosition(0);

		schemeModel = new DefaultListModel();

		for (ColorScheme.ColorSummary summary: cs.getColorSummaries())
			schemeModel.addElement(summary);
		schemeList.setModel(schemeModel);
	}

	// Add mouse listeners to the lists so that a double click fires an event
	private void addMouseListener(final JList list)
	{
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					selectColor(list);
			}
		});
	}

	// Pop up a colour chooser and apply the new colour to the selected scheme
	private void selectColor(JList list)
	{
		ColorScheme.ColorSummary c = (ColorScheme.ColorSummary) list.getSelectedValue();
		if (c == null)
			return;

		Color newColor = JColorChooser.showDialog(this, "Select New Colour", c.color);
		if (newColor == null)
			return;

		c.color = newColor;

		// Determine which colour scheme needs to be updated
		if (list == standardList)
			ColorScheme.setStandardColorSummaries(getVector(standardModel));
		else
		{
			ColorScheme cs = ((ColorScheme)schemeCombo.getSelectedItem());
			cs.setColorSummaries(getVector(schemeModel));
		}

		// Refresh screen
		gPanel.setViewSet(gPanel.getViewSet());
	}

	private ArrayList<ColorScheme.ColorSummary> getVector(DefaultListModel model)
	{
		ArrayList<ColorScheme.ColorSummary> colors = new ArrayList<ColorScheme.ColorSummary>();
		for (int i = 0; i < model.size(); i++)
			colors.add((ColorScheme.ColorSummary)model.get(i));

		return colors;
	}

	void resetColors()
	{
		// Reapply the defaults
		Prefs.setColorDefaults();
		// Get the lists in the panel to update with the new colours
		initializeLists();
		// Force an update of the view as new colours may apply there too
		gPanel.setViewSet(gPanel.getViewSet());
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        colorPanel = new javax.swing.JPanel();
        gridPanel = new javax.swing.JPanel();
        javax.swing.JPanel panel1 = new javax.swing.JPanel();
        javax.swing.JScrollPane sp2 = new javax.swing.JScrollPane();
        standardList = new javax.swing.JList();
        listLabel1 = new javax.swing.JLabel();
        javax.swing.JPanel panel2 = new javax.swing.JPanel();
        listLabel2 = new javax.swing.JLabel();
        javax.swing.JScrollPane sp3 = new javax.swing.JScrollPane();
        schemeList = new javax.swing.JList();
        infoPanel = new javax.swing.JPanel();
        javax.swing.JScrollPane sp1 = new javax.swing.JScrollPane();
        infoText = new javax.swing.JTextArea();
        comboLabel = new javax.swing.JLabel();
        schemeCombo = new javax.swing.JComboBox();

        colorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Customize (double click a colour to change it):"));

        gridPanel.setLayout(new java.awt.GridLayout(1, 2, 10, 0));

        panel1.setBackground((Color)UIManager.get("fjDialogBG"));
        panel1.setLayout(new java.awt.BorderLayout(0, 5));

        sp2.setViewportView(standardList);

        panel1.add(sp2, java.awt.BorderLayout.CENTER);

        listLabel1.setLabelFor(standardList);
        listLabel1.setText("Standard colours:");
        panel1.add(listLabel1, java.awt.BorderLayout.NORTH);

        gridPanel.add(panel1);

        panel2.setBackground((Color)UIManager.get("fjDialogBG"));
        panel2.setLayout(new java.awt.BorderLayout(0, 5));

        listLabel2.setLabelFor(schemeList);
        listLabel2.setText("Scheme specific:");
        panel2.add(listLabel2, java.awt.BorderLayout.NORTH);

        sp3.setViewportView(schemeList);

        panel2.add(sp3, java.awt.BorderLayout.CENTER);

        gridPanel.add(panel2);

        javax.swing.GroupLayout colorPanelLayout = new javax.swing.GroupLayout(colorPanel);
        colorPanel.setLayout(colorPanelLayout);
        colorPanelLayout.setHorizontalGroup(
            colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(colorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(gridPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
                .addContainerGap())
        );
        colorPanelLayout.setVerticalGroup(
            colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(colorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(gridPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                .addContainerGap())
        );

        infoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Information:"));

        sp1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        infoText.setColumns(20);
        infoText.setEditable(false);
        infoText.setLineWrap(true);
        infoText.setRows(5);
        infoText.setWrapStyleWord(true);
        sp1.setViewportView(infoText);

        comboLabel.setLabelFor(schemeCombo);
        comboLabel.setText("Selected colour scheme:");

        javax.swing.GroupLayout infoPanelLayout = new javax.swing.GroupLayout(infoPanel);
        infoPanel.setLayout(infoPanelLayout);
        infoPanelLayout.setHorizontalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(infoPanelLayout.createSequentialGroup()
                        .addComponent(comboLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(schemeCombo, 0, 295, Short.MAX_VALUE))
                    .addComponent(sp1, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE))
                .addContainerGap())
        );
        infoPanelLayout.setVerticalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboLabel)
                    .addComponent(schemeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(sp1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(colorPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(infoPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(infoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel colorPanel;
    private javax.swing.JLabel comboLabel;
    private javax.swing.JPanel gridPanel;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JTextArea infoText;
    private javax.swing.JLabel listLabel1;
    private javax.swing.JLabel listLabel2;
    javax.swing.JComboBox schemeCombo;
    private javax.swing.JList schemeList;
    private javax.swing.JList standardList;
    // End of variables declaration//GEN-END:variables


	static class ColorListRenderer extends DefaultListCellRenderer
	{
		// Set the attributes of the class and return a reference
		public Component getListCellRendererComponent(JList list, Object o,
				int i, boolean iss, boolean chf)
		{
			super.getListCellRendererComponent(list, o, i, iss, chf);

			ColorScheme.ColorSummary summary = (ColorScheme.ColorSummary) o;

			// Set the text
			setText(summary.name);

			// Set the icon
			BufferedImage image = new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) image.createGraphics();

			Color c1 = summary.color.brighter();
			Color c2 = summary.color.darker();

			g.setPaint(new GradientPaint(0, 0, c1, 20, 10, c2));
			g.fillRect(0, 0, 20, 10);
			g.setColor(Color.black);
			g.drawRect(0, 0, 20, 10);
			g.dispose();

			setIcon(new ImageIcon(image));

			return this;
		}

		public Insets getInsets(Insets i)
			{ return new Insets(0, 3, 0, 0); }
	}
}