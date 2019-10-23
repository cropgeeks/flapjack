// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;

import jhi.flapjack.gui.*;
import jhi.flapjack.gui.visualization.*;
import jhi.flapjack.gui.visualization.colors.*;

import scri.commons.gui.*;

public class ColorDialog extends JDialog implements ActionListener
{
	private WinMain winMain;

	private GenotypePanel gPanel;

	private DefaultComboBoxModel<ColorScheme> comboModel;
	private DefaultListModel<ColorScheme.ColorSummary> standardModel;
	private DefaultListModel<ColorScheme.ColorSummary> schemeModel;

	public ColorDialog(WinMain winMain, GenotypePanel gPanel)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.ColorDialog.title"),
			true
		);

		this.winMain = winMain;
		this.gPanel = gPanel;

		initComponents();
		initComponents2();

		FlapjackUtils.initDialog(this, bClose, bClose, true, getContentPane(),
			gridPanel, infoPanel, colorPanel);
	}

	private void initComponents2()
	{
		RB.setText(bClose, "gui.text.close");
		bClose.addActionListener(this);
		RB.setText(bDefaults, "gui.dialog.ColorDialog.bDefaults");
		RB.setMnemonic(bDefaults, "gui.dialog.ColorDialog.bDefaults");
		bDefaults.addActionListener(this);
		RB.setText(bApply, "gui.dialog.ColorDialog.bApply");
		RB.setMnemonic(bApply, "gui.dialog.ColorDialog.bApply");
		bApply.addActionListener(this);
		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "customize_colours.html");

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
		comboModel = new DefaultComboBoxModel<ColorScheme>();
		comboModel.addElement(new NucleotideColorScheme());
		comboModel.addElement(new Nucleotide01ColorScheme());
//		comboModel.addElement(new ABHDataColorScheme());
		comboModel.addElement(new SimpleTwoColorScheme());
		comboModel.addElement(new LineSimilarityColorScheme());
		comboModel.addElement(new LineSimilarityExactColorScheme());
		comboModel.addElement(new LineSimilarityAnyColorScheme());
		comboModel.addElement(new MarkerSimilarityColorScheme());
		comboModel.addElement(new SimilarityToEachParentColorScheme());
		comboModel.addElement(new SimilarityToEitherParentColorScheme());
		comboModel.addElement(new FavAlleleColorScheme());
		comboModel.addElement(new AlleleFrequencyColorScheme());
		comboModel.addElement(new MagicColorScheme());
		comboModel.addElement(new BinnedColorScheme());
		comboModel.addElement(new RandomColorScheme(ColorScheme.RANDOM));
		comboModel.addElement(new RandomColorScheme(ColorScheme.RANDOM_WSP));
		schemeCombo.setModel(comboModel);
		schemeCombo.addActionListener(this);

		initializeLists();

		// Match the combo box to the current scheme
		int model = gPanel.getViewSet().getColorScheme();
		for (int i = 0; i < comboModel.getSize(); i++)
			if (comboModel.getElementAt(i).getModel() == model)
				schemeCombo.setSelectedIndex(i);

		checkHetAsH.setSelected(Prefs.visShowHetsAsH);
		checkHetAsH.addActionListener(e -> { toggleHets(); });
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bClose)
			setVisible(false);

		else if (e.getSource() == bApply)
		{
			ColorScheme cs = (ColorScheme) schemeCombo.getSelectedItem();
			winMain.mViz.vizColor(cs.getModel());

			// Sillyness...if a shift/click was detected
			if (((e.getModifiers() & ActionEvent.SHIFT_MASK) != 0) && cs instanceof RandomColorScheme)
				new RandomDemo().start();
		}

		else if (e.getSource() == bDefaults)
		{
			String msg = RB.getString("gui.dialog.ColorDialog.defaultsMsg");

			String[] options = new String[] {
				RB.getString("gui.dialog.ColorDialog.bDefaults"),
				RB.getString("gui.text.cancel") };

			if (TaskDialog.show(msg, TaskDialog.WAR, 1, options) == 0)
				resetColors();
		}

		else if (e.getSource() == schemeCombo)
		{
			updateSchemeList();
		}
	}

	private class RandomDemo extends Thread
	{
		// Sillyness - on a Shift/Click of the button if the random scheme has
		// been selected, we loop 50 times, selecting a new scheme each time
		@Override
		public void run()
		{
			Runnable r = () -> { winMain.mViz.vizColor(ColorScheme.RANDOM); };

			for (int i = 0; i < 50; i++)
			{
				try
				{
					SwingUtilities.invokeAndWait(r);
					Thread.sleep(100);
				}
				catch (Exception e) {}
			}
		}
	}

	void initializeLists()
	{
		initStandardList();
		updateSchemeList();
	}

	private void initStandardList()
	{
		standardModel = new DefaultListModel<ColorScheme.ColorSummary>();

		for (ColorScheme.ColorSummary summary: ColorScheme.getStandardColorSummaries())
			standardModel.addElement(summary);
		standardList.setModel(standardModel);
	}

	private void updateSchemeList()
	{
			ColorScheme cs = (ColorScheme) schemeCombo.getSelectedItem();
			infoText.setText(cs.getDescription());
			infoText.setCaretPosition(0);

			schemeModel = new DefaultListModel<ColorScheme.ColorSummary>();

			for (ColorScheme.ColorSummary summary: cs.getColorSummaries())
				schemeModel.addElement(summary);
			schemeList.setModel(schemeModel);
	}

	private void toggleHets()
	{
		Prefs.visShowHetsAsH = checkHetAsH.isSelected();
		gPanel.setViewSet(gPanel.getViewSet());
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
		ArrayList<ColorScheme.ColorSummary> colors = new ArrayList<>();
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

	static class ColorListRenderer extends DefaultListCellRenderer
	{
		// Set the attributes of the class and return a reference
		@Override
		public Component getListCellRendererComponent(JList list, Object o,
				int i, boolean iss, boolean chf)
		{
			super.getListCellRendererComponent(list, o, i, iss, chf);

			ColorScheme.ColorSummary summary = (ColorScheme.ColorSummary) o;

			// Set the text
			setText(summary.name);

			// Set the icon
			BufferedImage image = new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();

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

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        infoPanel = new javax.swing.JPanel();
        javax.swing.JScrollPane sp1 = new javax.swing.JScrollPane();
        infoText = new javax.swing.JTextArea();
        comboLabel = new javax.swing.JLabel();
        schemeCombo = new javax.swing.JComboBox<ColorScheme>();
        colorPanel = new javax.swing.JPanel();
        gridPanel = new javax.swing.JPanel();
        javax.swing.JPanel panel1 = new javax.swing.JPanel();
        javax.swing.JScrollPane sp2 = new javax.swing.JScrollPane();
        standardList = new javax.swing.JList<ColorScheme.ColorSummary>();
        listLabel1 = new javax.swing.JLabel();
        javax.swing.JPanel panel2 = new javax.swing.JPanel();
        listLabel2 = new javax.swing.JLabel();
        javax.swing.JScrollPane sp3 = new javax.swing.JScrollPane();
        schemeList = new javax.swing.JList<ColorScheme.ColorSummary>();
        checkHetAsH = new javax.swing.JCheckBox();
        dialogPanel1 = new scri.commons.gui.matisse.DialogPanel();
        bApply = new javax.swing.JButton();
        bDefaults = new javax.swing.JButton();
        bClose = new javax.swing.JButton();
        bHelp = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        infoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Information:"));

        sp1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        infoText.setColumns(20);
        infoText.setEditable(false);
        infoText.setLineWrap(true);
        infoText.setRows(5);
        infoText.setWrapStyleWord(true);
        sp1.setViewportView(infoText);

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
                        .addComponent(schemeCombo, 0, 372, Short.MAX_VALUE))
                    .addComponent(sp1, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE))
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

        colorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Customize (double click a colour to change it):"));

        gridPanel.setLayout(new java.awt.GridLayout(1, 2, 10, 0));

        panel1.setBackground((Color)UIManager.get("fjDialogBG"));
        panel1.setLayout(new java.awt.BorderLayout(0, 5));

        sp2.setViewportView(standardList);

        panel1.add(sp2, java.awt.BorderLayout.CENTER);

        listLabel1.setText("Standard colours:");
        panel1.add(listLabel1, java.awt.BorderLayout.NORTH);

        gridPanel.add(panel1);

        panel2.setBackground((Color)UIManager.get("fjDialogBG"));
        panel2.setLayout(new java.awt.BorderLayout(0, 5));

        listLabel2.setText("Scheme specific:");
        panel2.add(listLabel2, java.awt.BorderLayout.NORTH);

        sp3.setViewportView(schemeList);

        panel2.add(sp3, java.awt.BorderLayout.CENTER);

        gridPanel.add(panel2);

        checkHetAsH.setText("Always render heterozygotes as single-colour 'H' blocks, regardless of the scheme selected");

        javax.swing.GroupLayout colorPanelLayout = new javax.swing.GroupLayout(colorPanel);
        colorPanel.setLayout(colorPanelLayout);
        colorPanelLayout.setHorizontalGroup(
            colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(colorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(gridPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
                    .addGroup(colorPanelLayout.createSequentialGroup()
                        .addComponent(checkHetAsH)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        colorPanelLayout.setVerticalGroup(
            colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(colorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(gridPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkHetAsH)
                .addContainerGap())
        );

        bApply.setText("Apply to current view");
        dialogPanel1.add(bApply);

        bDefaults.setText("Reset colours");
        dialogPanel1.add(bDefaults);

        bClose.setText("Close");
        dialogPanel1.add(bClose);

        bHelp.setText("Help");
        dialogPanel1.add(bHelp);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(colorPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(infoPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(dialogPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(infoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dialogPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bApply;
    private javax.swing.JButton bClose;
    private javax.swing.JButton bDefaults;
    private javax.swing.JButton bHelp;
    private javax.swing.JCheckBox checkHetAsH;
    private javax.swing.JPanel colorPanel;
    private javax.swing.JLabel comboLabel;
    private scri.commons.gui.matisse.DialogPanel dialogPanel1;
    private javax.swing.JPanel gridPanel;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JTextArea infoText;
    private javax.swing.JLabel listLabel1;
    private javax.swing.JLabel listLabel2;
    javax.swing.JComboBox<ColorScheme> schemeCombo;
    private javax.swing.JList<ColorScheme.ColorSummary> schemeList;
    private javax.swing.JList<ColorScheme.ColorSummary> standardList;
    // End of variables declaration//GEN-END:variables
}