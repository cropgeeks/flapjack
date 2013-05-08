package flapjack.gui.dendogram;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import flapjack.gui.*;

public class DendogramPanel extends JPanel
{
	// Controls for visualization
	private DendogramCanvas dCanvas;
	private JScrollPane sp;

	private CanvasController controller;

	public DendogramPanel(BufferedImage image)
	{
		createControls(image);
	}

	private void createControls(BufferedImage image)
	{
		// Visualization setup
		dCanvas = new DendogramCanvas(this, image);

		sp = new JScrollPane();
		sp.setViewportView(dCanvas);
		sp.setWheelScrollingEnabled(false);

		controller = new CanvasController(this, sp);

		setLayout(new BorderLayout(0, 0));
		add(new TitlePanel("Dendogram"), BorderLayout.NORTH);
		add(sp);
	}

	DendogramCanvas getDendogramCanvas()
	{
		return dCanvas;
	}
}
