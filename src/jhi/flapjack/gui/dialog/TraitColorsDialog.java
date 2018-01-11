// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class TraitColorsDialog extends JDialog
{
	private DataSet dataSet;

	// Tracks the currently active trait's list of categories and colors
	private Trait selectedTrait;
	private static Hashtable<String,Color> selectedColors = new Hashtable<>();

	private DefaultComboBoxModel<Trait> comboModel = new DefaultComboBoxModel<>();
	private DefaultListModel<String> highlowAllModel = new DefaultListModel<>();
	private DefaultListModel<String> highlowModel = new DefaultListModel<>();
	private DefaultListModel<String> categoriesModel = new DefaultListModel<>();

	public TraitColorsDialog(DataSet dataSet)
	{
		super(
			Flapjack.winMain,
			"Trait Colours",
			true
		);

		this.dataSet = dataSet;

		initComponents();
		initComponents2();

		FlapjackUtils.initDialog(this, bClose, bClose, true, getContentPane());
	}

	private void initComponents2()
	{
		getContentPane().setBackground(Color.WHITE);
		allPanel.setBackground(Color.WHITE);
		traitPanel.setBackground(Color.WHITE);

		bClose.addActionListener(e -> { setVisible(false); });
		bReset.addActionListener(e -> { resetColors(); });

		// Set up the traits selection combo box
		traitsCombo.setModel(comboModel);
		traitsCombo.addItemListener(e -> { selectTrait(); });

		for (Trait trait: dataSet.getTraits())
			comboModel.addElement(trait);

		// Set up the list controls
		initList(listHighLowAll, highlowAllModel);
		initList(listHighLow, highlowModel);
		initList(listCategories, categoriesModel);

		// Deal with the initally selected trait (if any)
		selectTrait();
	}

	private void initList(JList<String> list, DefaultListModel<String> model)
	{
		list.setModel(model);
		list.setCellRenderer(new ColorListRenderer());
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					pickNewColor(list);
			}
		});

		if (list != listCategories)
		{
			model.addElement("Low");
			model.addElement("High");
		}
	}

	// Resets all colours
	public void resetColors()
	{
		String msg = "This will reset all colours back to their default values. Are you sure?";

		String[] options = new String[] {
			"Reset",
			RB.getString("gui.text.cancel") };

		if (TaskDialog.show(msg, TaskDialog.QST, 1, options) == 0)
		{
			// Reset global values
			Prefs.visColorHeatmapLow = new Color(120, 255, 120);
			Prefs.visColorHeatmapHigh = new Color(255, 120, 120);

			// Reset all overriden values
			for (Trait trait: dataSet.getTraits())
				trait.getColors().clear();

			refreshScreen();
		}
	}

	private void refreshScreen()
	{
		// Refresh screen
		Flapjack.winMain.repaint();
		Actions.projectModified();

		listHighLowAll.repaint();
		listHighLow.repaint();
		selectTrait();
	}

	private void selectTrait()
	{
		categoriesModel.clear();

		int index = traitsCombo.getSelectedIndex();
		selectedTrait = comboModel.getElementAt(index);

		// For categorical traits, fill in the 3rd list box
		if (selectedTrait.traitIsNumerical() == false)
		{
			listCategories.setEnabled(true);

			for (Line line: dataSet.getLines())
			{
				// Get the display name and colour for this instance
				TraitValue tv = line.getTraitValues().get(index);
				selectedColors.put(selectedTrait.format(tv), tv.displayColor());
			}

			for (String category: selectedTrait.getCategories())
				categoriesModel.addElement(category);
		}
		// But just disable it for numerical traits
		else
			listCategories.setEnabled(false);
	}

	private void pickNewColor(JList<String> list)
	{
		String key = list.getSelectedValue();
		if (key == null)
			return;

		Color c = getColor(list, key);

		Color newColor = JColorChooser.showDialog(this, "Select New Colour", c);
		if (newColor == null)
			return;

		if (list == listHighLowAll)
		{
			if (key.equals("Low"))
				Prefs.visColorHeatmapLow = newColor;
			else
				Prefs.visColorHeatmapHigh = newColor;
		}
		else if (list == listHighLow)
		{
			if (key.equals("Low"))
				selectedTrait.getColors().put("FLAPJACK_LW", newColor);
			else
				selectedTrait.getColors().put("FLAPJACK_HG", newColor);
		}
		else
		{
			selectedTrait.getColors().put(key, newColor);
			selectedColors.put(key, newColor);
		}

		refreshScreen();
	}


	class ColorListRenderer extends DefaultListCellRenderer
	{
		// Set the attributes of the class and return a reference
		public Component getListCellRendererComponent(JList list, Object o,
				int i, boolean iss, boolean chf)
		{
			super.getListCellRendererComponent(list, o, i, iss, chf);

			String category = (String) o;

			// Set the text
			setText(category);

			// Set the icon
			BufferedImage image = new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) image.createGraphics();

			Color c1 = getColor(list, category).brighter();
			Color c2 = getColor(list, category).darker();

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

	private Color getColor(JList list, String key)
	{
		if (list == listHighLowAll)
		{
			if (key.equals("Low"))
				return Prefs.visColorHeatmapLow;
			else
				return Prefs.visColorHeatmapHigh;
		}

		else if (list == listHighLow)
		{
			if (key.equals("Low"))
				return selectedTrait.getColors().queryLow();
			else
				return selectedTrait.getColors().queryHigh();
		}

		else
			return selectedColors.get(key);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dialogPanel1 = new scri.commons.gui.matisse.DialogPanel();
        bClose = new javax.swing.JButton();
        bReset = new javax.swing.JButton();
        bHelp = new javax.swing.JButton();
        allPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listHighLowAll = new javax.swing.JList<String>();
        traitPanel = new javax.swing.JPanel();
        traitsCombo = new javax.swing.JComboBox<Trait>();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listHighLow = new javax.swing.JList<String>();
        jScrollPane3 = new javax.swing.JScrollPane();
        listCategories = new javax.swing.JList<String>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        bClose.setText("Close");
        dialogPanel1.add(bClose);

        bReset.setText("Reset");
        dialogPanel1.add(bReset);

        bHelp.setText("Help");
        dialogPanel1.add(bHelp);

        allPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Applies to all traits:"));

        jScrollPane1.setViewportView(listHighLowAll);

        javax.swing.GroupLayout allPanelLayout = new javax.swing.GroupLayout(allPanel);
        allPanel.setLayout(allPanelLayout);
        allPanelLayout.setHorizontalGroup(
            allPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(allPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                .addContainerGap())
        );
        allPanelLayout.setVerticalGroup(
            allPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(allPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                .addContainerGap())
        );

        traitPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected trait only:"));

        jPanel3.setLayout(new java.awt.GridLayout(1, 2, 5, 5));

        jScrollPane2.setViewportView(listHighLow);

        jPanel3.add(jScrollPane2);

        jScrollPane3.setViewportView(listCategories);

        jPanel3.add(jScrollPane3);

        javax.swing.GroupLayout traitPanelLayout = new javax.swing.GroupLayout(traitPanel);
        traitPanel.setLayout(traitPanelLayout);
        traitPanelLayout.setHorizontalGroup(
            traitPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, traitPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(traitPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(traitsCombo, 0, 286, Short.MAX_VALUE))
                .addContainerGap())
        );
        traitPanelLayout.setVerticalGroup(
            traitPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(traitPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(traitsCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dialogPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(allPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(traitPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(allPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(traitPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dialogPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel allPanel;
    private javax.swing.JButton bClose;
    private javax.swing.JButton bHelp;
    private javax.swing.JButton bReset;
    private scri.commons.gui.matisse.DialogPanel dialogPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList<String> listCategories;
    private javax.swing.JList<String> listHighLow;
    private javax.swing.JList<String> listHighLowAll;
    private javax.swing.JPanel traitPanel;
    private javax.swing.JComboBox<Trait> traitsCombo;
    // End of variables declaration//GEN-END:variables
}