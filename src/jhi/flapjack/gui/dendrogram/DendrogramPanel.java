// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dendrogram;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;

import jhi.flapjack.data.results.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.io.*;

public class DendrogramPanel extends JPanel implements AncestorListener
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
		nbPanel = new DendrogramPanelNB(dendrogram, dCanvas);
		controller = new CanvasController(this, nbPanel.sp);

		setLayout(new BorderLayout(0, 0));
		add(new TitlePanel(dendrogram.getTitle()), BorderLayout.NORTH);
		add(nbPanel);
	}

	DendrogramCanvas getDendrogramCanvas()
	{
		return dCanvas;
	}

	public void ancestorAdded(AncestorEvent event)
	{
		ProjectSerializerDB.setFromCache(dendrogram.getPng());
		ProjectSerializerDB.setFromCache(dendrogram.getPdf());

		try
		{
			// Convert the PNG from its byte[] array into a BufferedImage
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
		dendrogram.getPdf().dbClear();

		dCanvas.setImage(null);
	}

	public void ancestorMoved(AncestorEvent event)
	{
	}
}