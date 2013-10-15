package flapjack.gui.dendrogram;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.io.*;

public class DendrogramPanel extends JPanel implements ActionListener, AncestorListener
{
	private Dendrogram dendrogram;

	// Controls for visualization
	private DendrogramCanvas dCanvas;
	private DendrogramPanelNB nbPanel;

	private CanvasController controller;

	public DendrogramPanel(Dendrogram dendrogram)
	{
		this.dendrogram = dendrogram;

		createControls();

		addAncestorListener(this);
	}

	private void createControls()
	{
		// Visualization setup
		dCanvas = new DendrogramCanvas(this);
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

	public void ancestorAdded(AncestorEvent event)
	{
		ProjectSerializerDB.setFromCache(dendrogram.getPng());

		try
		{
			byte[] data = dendrogram.getPng().image;
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			BufferedImage image = javax.imageio.ImageIO.read(bis);

			dCanvas.setImage(image);
		}
		catch (Exception e) {}
	}

	public void ancestorRemoved(AncestorEvent event)
	{
		dendrogram.getPng().dbClear();
		dCanvas.setImage(null);
	}

	public void ancestorMoved(AncestorEvent event)
	{
	}
}
