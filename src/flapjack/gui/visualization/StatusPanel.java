package flapjack.gui.visualization;

import java.awt.*;
import java.text.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

class StatusPanel extends JPanel
{
	private DecimalFormat d = new DecimalFormat("0.0");

	private GenotypePanel gPanel;
	private GTView view;

	private JLabel label1, label2, label3;
	private JLabel lineLabel, markerLabel, alleleLabel;
    private JSlider sizeSlider;

	StatusPanel(GenotypePanel gPanel)
	{
		this.gPanel = gPanel;

		createControls();
		layoutControls();
	}

/*	void computeDimensions(int w1)
	{
		setBorder(BorderFactory.createEmptyBorder(5, w1, 5, 0));
	}
*/

	private void createControls()
	{
		label1 = new JLabel(RB.getString("gui.visualization.StatusPanel.line"));
		label2 = new JLabel(RB.getString("gui.visualization.StatusPanel.marker"));
		label3 = new JLabel("Genotype:");

		lineLabel = new JLabel();
		lineLabel.setForeground(Color.red);
		markerLabel = new JLabel();
		markerLabel.setForeground(Color.red);
		alleleLabel = new JLabel();
		alleleLabel.setForeground(Color.red);

		sizeSlider = new JSlider(1, 25, 7);
		sizeSlider.addChangeListener(gPanel);
	}

	private void layoutControls()
	{
		// Left hand side labels
		JPanel p1 = new JPanel(new GridLayout(2, 1, 2, 2));
		p1.add(label1);
		p1.add(label2);

		JPanel p2 = new JPanel(new GridLayout(2, 1, 2, 2));
		p2.add(lineLabel);
		p2.add(markerLabel);

		JPanel f1 = new JPanel(new BorderLayout(10, 0));
		f1.add(p1, BorderLayout.WEST);
		f1.add(p2);
		f1.setPreferredSize(new Dimension(200, f1.getPreferredSize().height));


		// Right hand side labels
		JPanel p3 = new JPanel(new GridLayout(2, 1, 2, 2));
		p3.add(label3);

		JPanel p4 = new JPanel(new GridLayout(2, 1, 2, 2));
		p4.add(alleleLabel);

		JPanel f2 = new JPanel(new BorderLayout(10, 0));
		f2.add(p3, BorderLayout.WEST);
		f2.add(p4);


		// Panel to hold the left/right hand sides
		JPanel g1 = new JPanel(new FlowLayout());
		g1.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
		g1.add(f1);
		g1.add(f2);


		// Panel for the slider
		JPanel sliderPanel = new JPanel(new FlowLayout());
		sliderPanel.add(sizeSlider);


		// Final layout
		setLayout(new BorderLayout());
		add(g1, BorderLayout.WEST);
		add(sliderPanel, BorderLayout.EAST);
	}

	JSlider getSlider()
		{ return sizeSlider; }

	void setView(GTView view)
	{
		this.view = view;
	}

	void setIndices(int lineIndex, int markerIndex)
	{
		// Current line under the mouse
		if (lineIndex < 0 || lineIndex >= view.getLineCount())
			lineLabel.setText("");
		else
		{
			String position = (lineIndex+1) + "/" + view.getLineCount();
			lineLabel.setText(view.getLine(lineIndex).getName() + " (" + position + ")");
		}

		// Current marker under the mouse
		if (markerIndex < 0 || markerIndex >= view.getMarkerCount())
			markerLabel.setText("");
		else
		{
			Marker m = view.getMarker(markerIndex);
			markerLabel.setText(m.getName() + " (" + d.format(m.getPosition()) + ")");
		}

		// Current allele under the mouse
		if (lineIndex < 0 || lineIndex >= view.getLineCount() ||
			markerIndex < 0 || markerIndex >= view.getMarkerCount())
		{
			alleleLabel.setText("");
		}
		else
		{
			int stateCode = view.getState(lineIndex, markerIndex);
			AlleleState state = view.getStateTable().getAlleleState(stateCode);

			alleleLabel.setText(state.getRawData());
		}
	}
}
