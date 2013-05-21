package flapjack.gui.dendrogram;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import flapjack.gui.*;

public class DendrogramPanel extends JPanel
{
	// Controls for visualization
	private DendrogramCanvas dCanvas;
	private JScrollPane sp;

	private CanvasController controller;

	public DendrogramPanel(BufferedImage image)
	{
		createControls(image);
	}

	private void createControls(BufferedImage image)
	{
		// Visualization setup
		dCanvas = new DendrogramCanvas(this, image);

		sp = new JScrollPane();
		sp.setViewportView(dCanvas);
		sp.setWheelScrollingEnabled(false);

		controller = new CanvasController(this, sp);

		setLayout(new BorderLayout(0, 0));
		add(new TitlePanel("Dendogram"), BorderLayout.NORTH);
		add(sp);
	}

	DendrogramCanvas getDendogramCanvas()
	{
		return dCanvas;
	}
}
