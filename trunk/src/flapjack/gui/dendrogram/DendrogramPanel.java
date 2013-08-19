package flapjack.gui.dendrogram;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

public class DendrogramPanel extends JPanel implements ActionListener
{
	// Controls for visualization
	private DendrogramCanvas dCanvas;
	private DendrogramPanelNB nbPanel;

	private CanvasController controller;

	public DendrogramPanel(Dendrogram dendrogram)
	{
		createControls(dendrogram.getImage());
	}

	private void createControls(BufferedImage image)
	{
		// Visualization setup
		dCanvas = new DendrogramCanvas(this, image);
		nbPanel = new DendrogramPanelNB(dCanvas);
		controller = new CanvasController(this, nbPanel.sp);

		setLayout(new BorderLayout(0, 0));
		add(new TitlePanel("Dendrogram"), BorderLayout.NORTH);
		add(nbPanel);
	}

	DendrogramCanvas getDendrogramCanvas()
	{
		return dCanvas;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == nbPanel.button)
		{
			System.out.println("Do something");
		}
	}
}
