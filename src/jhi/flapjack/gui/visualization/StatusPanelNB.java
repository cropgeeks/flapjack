// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.util.stream.*;
import javax.swing.*;
import javax.swing.event.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.pedigree.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.visualization.colors.*;

import scri.commons.gui.*;

public class StatusPanelNB extends JPanel implements ActionListener, ChangeListener
{
	private NumberFormat nf = NumberFormat.getInstance();

	private GenotypePanel gPanel;
	private GTView view;

	StatusPanelNB(GenotypePanel gPanel)
	{
		this.gPanel = gPanel;

		initComponents();

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

		createKeyboardShortcuts();
	}

	private void createKeyboardShortcuts()
	{
		// CTRL (or CMD) keyboard shortcut
		int shortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		Action zoomIn = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				sliderY.setValue(sliderY.getValue()+1);
			}
		};

		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, shortcut);
		sliderY.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, "zoomInMain");
		sliderY.getActionMap().put("zoomInMain", zoomIn);
		ks = KeyStroke.getKeyStroke(KeyEvent.VK_ADD, shortcut);
		sliderY.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, "zoomInNumPad");
		sliderY.getActionMap().put("zoomInNumPad", zoomIn);

		Action zoomOut = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				sliderY.setValue(sliderY.getValue()-1);
			}
		};

		ks = KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, shortcut);
		sliderY.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, "zoomOutMain");
		sliderY.getActionMap().put("zoomOutMain", zoomOut);
		ks = KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, shortcut);
		sliderY.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, "zoomOutNumPad");
		sliderY.getActionMap().put("zoomOutNumPad", zoomOut);

		Action zoomReset = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				sliderY.setValue(7);
			}
		};

		ks = KeyStroke.getKeyStroke(KeyEvent.VK_0, shortcut);
		sliderY.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, "zoomResetMain");
		sliderY.getActionMap().put("zoomResetMain", zoomReset);
		ks = KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0, shortcut);
		sliderY.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, "zoomResetNumPad");
		sliderY.getActionMap().put("zoomResetNumPad", zoomReset);
	}

	void setForMainUse()
	{
		RB.setText(label1, "gui.visualization.StatusPanel.line");
		RB.setText(label2, "gui.visualization.StatusPanel.marker");
		if (dataSetBinned(view))
			RB.setText(label3, "gui.visualization.StatusPanel.bin");
		else
			RB.setText(label3, "gui.visualization.StatusPanel.genotype");
	}

	void setForHeatmapUse()
	{
		RB.setText(label1, "gui.visualization.StatusPanel.line");
		RB.setText(label2, "gui.visualization.StatusPanel.trait");
		RB.setText(label3, "gui.visualization.StatusPanel.value");
	}

	void setForFeatureUse()
	{
		RB.setText(label1, "gui.visualization.StatusPanel.featureTrait");
		RB.setText(label2, "gui.visualization.StatusPanel.featureExperiment");
		RB.setText(label3, "gui.visualization.StatusPanel.featureData");
	}

	void setForGraphUse()
	{
		RB.setText(label1, "gui.visualization.StatusPanel.graph");
		RB.setText(label2, "gui.visualization.StatusPanel.marker");
		RB.setText(label3, "gui.visualization.StatusPanel.value");
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

		gPanel.getController().doZoom();
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
		if (lineIndex < 0 || lineIndex >= view.lineCount())
			lineLabel.setText(" ");
		else
		{
			String position = (lineIndex+1) + "/" + view.lineCount();
			LineInfo lineInfo = view.getLineInfo(lineIndex);
			Line line = lineInfo.getLine();
			String text = (lineInfo.name() + " (" + position + ")").trim();
//			PedigreeManager pedMan = view.getViewSet().getDataSet().getPedigreeManager();
//			if (pedMan.getChildrenToParents().containsKey(line))
//			{
//				text += " has parents: " + view.getViewSet().getDataSet().getPedigreeManager().getChildrenToParents().get(line).stream().map(Line::getName).collect(Collectors.joining(", "));
//			}

			PedManager pm = view.getViewSet().getDataSet().getPedManager();
			ArrayList<PedLineInfo> parents = pm.getParentsForLine(lineInfo);
//			for (PedLineInfo pInfo: parents)
//				text += pInfo.getParent().getName() + ", ";

			lineLabel.setText(text);
		}

		// Current marker under the mouse
		if (markerIndex < 0 || markerIndex >= view.markerCount())
			markerLabel.setText(" ");
		else
		{
			Marker m = view.getMarker(markerIndex);
			if (m.dummyMarker() == false)
			{
				FavAlleleManager fm = view.getViewSet().getDataSet().getFavAlleleManager();

				// Does this marker have an alternative name?
				String altName = fm.getAltNames().get(m.getName());

				// NAME / ALTNAME (POSITION)
				String str = m.getName()
					+ (altName != null ? " / " + altName : "")
					+ " (" + nf.format(m.getRealPosition()) + ")";

				// Parse and determine fav/unfav info (if any)
				ArrayList<Integer> favAlleles = fm.getFavAlleles().get(m.getName());
				ArrayList<Integer> unfavAlleles = fm.getUnfavAlleles().get(m.getName());

				StateTable st = view.getViewSet().getDataSet().getStateTable();

				if (favAlleles != null && favAlleles.size() > 0)
				{
					str += " - Favourable Alleles: " + favAlleles.stream()
						.map(stateCode -> st.getAlleleState(stateCode).toString())
						.collect(Collectors.joining(","));
				}

				if (unfavAlleles != null && unfavAlleles.size() > 0)
				{
					str += " - Unfavourable Alleles: " + unfavAlleles.stream()
						.map(stateCode -> st.getAlleleState(stateCode).toString())
						.collect(Collectors.joining(","));
				}

				markerLabel.setText(str);
			}
			else
				markerLabel.setText(" ");
		}

		// Current allele under the mouse
		if (lineIndex < 0 || lineIndex >= view.lineCount() ||
			markerIndex < 0 || markerIndex >= view.markerCount())
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

				// Get the BinnedData object so we can get the range for a bin
				// if we are viewing binned data
				BinnedData binData = view.getViewSet().getDataSet().getBinnedData();

				alleleLabel.setText(state.toString() + " " + binData.getBinForState(state.toString()));
			}
		}
	}

	void setHeatmapValues(String line, String trait, String value)
	{
		lineLabel.setText(line);
		markerLabel.setText(trait);
		alleleLabel.setText(value);
	}

	void setGraphDetails(String graph, String marker, String value)
	{
		lineLabel.setText(graph);
		markerLabel.setText(marker);
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

				try
				{
					// Can we format as a number?
					float value = nf.parse(qtl.getValues()[i]).floatValue();
					data += qtl.getVNames()[i] + " (" + nf.format(value) + ")";
				}
				catch (Exception e)
				{
					// If not, format as a string
					data += qtl.getVNames()[i]
						+ " (" + qtl.getValues()[i] + ")";
				}
			}

			if (data.length() > 0)
				alleleLabel.setText(data);
			else
				alleleLabel.setText(" ");

			// TODO: Temporary hack for GOBii annual meeting. Remove or tidy up later.
			FavAlleleManager favAlleleManager = view.getViewSet().getDataSet().getFavAlleleManager();
			Map<String, MarkerFavAlleles> haplotypes = favAlleleManager.getHaplotypeAlleleManager();
			MarkerFavAlleles allelesForHaplotype = haplotypes.get(qtl.getName());
			if (allelesForHaplotype != null)
			{
				StateTable stateTable = view.getViewSet().getDataSet().getStateTable();
				StringBuilder builder = new StringBuilder();

				Map<String, ArrayList<Integer>> allelesByMarker = allelesForHaplotype.getMarkerToAlleles();
				for (String markerName : allelesByMarker.keySet())
					builder.append(stateTable.getAlleleState(allelesByMarker.get(markerName).get(0)).toString());

				alleleLabel.setText(builder.toString());
			}
		}
		else
		{
			lineLabel.setText(" ");
			markerLabel.setText(" ");
			alleleLabel.setText(" ");
		}
	}

	// Rather than call a setForResultsUse method, just setup the labels
	// in this set values method as they change on mouseover within the
	// component.
	void setResultsValues(String line, String result, String value)
	{
		RB.setText(label1, "gui.visualization.StatusPanel.line");
		label2.setText(result);
		label3.setText(" ");

		lineLabel.setText(line);
		markerLabel.setText(value);
		alleleLabel.setText(" ");

		// We need to set the text to an empty string so the component lays out
		// correctly
		if (result == null || result.isEmpty())
			label2.setText(" ");

		if (value == null || value.isEmpty())
			markerLabel.setText(" ");
	}

	private boolean dataSetBinned(GTView view)
	{
		return view != null &&
			(view.getViewSet().getDataSet().getBinnedData().containsBins()
			|| view.getViewSet().getColorScheme() == ColorScheme.BINNED_10);
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label3)
                    .addComponent(label1)
                    .addComponent(label2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(alleleLabel)
                    .addComponent(markerLabel)
                    .addComponent(lineLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 99, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(horizontalLabel)
                    .addComponent(sliderX, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkLink)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(sliderY, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(verticalLabel))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(label1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(label2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(label3))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(lineLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(markerLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(alleleLabel)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(horizontalLabel)
                            .addComponent(verticalLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(sliderX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(sliderY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(checkLink)))))
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