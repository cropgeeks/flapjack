// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

import jhi.flapjack.gui.*;
import jhi.flapjack.gui.visualization.*;

import scri.commons.gui.*;

public class ImageExporter extends SimpleJob
{
	private GenotypePanel gPanel;
	private File file;

	private BufferedImage image;

	public ImageExporter(GenotypePanel gPanel, File file)
	{
		this.gPanel = gPanel;
		this.file = file;
	}

	public void runJob(int index)
		throws Exception
	{
		switch (Prefs.guiExportImageMethod)
		{
			case 0: exportCurrentWindow();
				break;

			case 1: exportEntireView();
				break;

			case 2: exportOverview();
				break;
		}
	}

	private void exportCurrentWindow()
		throws Error, Exception
	{
		image = gPanel.getCanvasBuffer(false);
		exportView(image, false);
	}

	private void exportOverview()
		throws Exception
	{
		int w = Prefs.guiExportImageX;
		int h = Prefs.guiExportImageY;

		image = OverviewManager.getExportableImage(w, h);

		if (image == null)
			throw new RuntimeException(RB.getString("io.ImageExporter.oomError"));

		ImageIO.write(image, "png", file);
	}

	private void exportEntireView()
		throws Error, Exception
	{
		image = gPanel.getCanvasBuffer(true);
		exportView(image, true);
	}

	private void exportView(BufferedImage image, Boolean full)
		throws Exception
	{
		BufferedImage map = gPanel.getMapCanvasBuffer(full);
		BufferedImage lines = gPanel.getLineCanvasBuffer(full);
		BufferedImage traits = gPanel.getTraitCanvasBuffer(full);
		BufferedImage qtls = gPanel.getQTLCanvasBuffer(full);
		BufferedImage[] graphs = gPanel.getGraphCanvasBuffers(full);

		if (image == null)
			throw new RuntimeException(RB.getString("io.ImageExporter.noBufferError"));

		// Buffer to hold the final composite of the three images
		BufferedImage finalImage;

		int lineWidth = 0;
		if (Prefs.visShowLinePanel)
			lineWidth = lines.getWidth();

		int mapHeight = 0;
		if (Prefs.visShowMapCanvas)
			mapHeight = map.getHeight();

		int traitsWidth = 0;
		if (traits != null && Prefs.visShowTraitCanvas)
			traitsWidth = traits.getWidth() +5;

		int qtlsHeight = 0;
		if (qtls != null && Prefs.visShowQTLCanvas)
			qtlsHeight = qtls.getHeight();

		int graphHeight = 0;
		if (Prefs.visShowGraphCanvas)
			for (int i = 0; i < graphs.length; i++)
				if (graphs[i] != null)
					graphHeight += graphs[i].getHeight() + 2;


		// Work out the total canvas size
		int w = lineWidth + image.getWidth() + traitsWidth;
		int h = qtlsHeight + image.getHeight() + mapHeight + graphHeight;

		try
		{
			finalImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		}
		catch (Throwable t)
		{
			throw new RuntimeException(RB.getString("io.ImageExporter.oomError"));
		}

		// Now draw the composite
		Graphics2D g = finalImage.createGraphics();

		g.setColor(Color.white);
		g.fillRect(0, 0, w, h);

		if (Prefs.visShowQTLCanvas)
			g.drawImage(qtls, lineWidth+traitsWidth, 0, null);
		if (Prefs.visShowMapCanvas)
			g.drawImage(map, lineWidth+traitsWidth, qtlsHeight, null);
		if (Prefs.visShowTraitCanvas)
			g.drawImage(traits, 0, qtlsHeight+mapHeight, null);
		if (Prefs.visShowLinePanel)
			g.drawImage(lines, traitsWidth, qtlsHeight+mapHeight, null);

		g.drawImage(image, lineWidth+traitsWidth, qtlsHeight+mapHeight, null);

		if (Prefs.visShowGraphCanvas)
		{
			int gh = 0;
			for (int i = 0; i < graphs.length; i++)
			{
				g.drawImage(graphs[i], lineWidth+traitsWidth, gh+qtlsHeight+mapHeight+image.getHeight()+2, null);
				gh += (graphs[i] != null) ? graphs[i].getHeight()+2 : 0;
			}

			g.fillRect(0, qtlsHeight+mapHeight+image.getHeight(), lineWidth+traitsWidth, graphHeight);
		}

		g.dispose();

		ImageIO.write(finalImage, "png", file);
	}

	public boolean isIndeterminate()
		{ return true; }

	public String getMessage()
	{
		return RB.format("io.ImageExporter.message", file.getName());
	}
}