// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.io;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

import flapjack.gui.*;
import flapjack.gui.visualization.*;

import scri.commons.gui.*;

public class ImageExporter implements ITrackableJob
{
	private GenotypePanel gPanel;
	private File file;

	private BufferedImage image;

	public ImageExporter(GenotypePanel gPanel, File file)
	{
		this.gPanel = gPanel;
		this.file = file;
	}

	public void runJob()
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
		throws Exception
	{
		image = gPanel.getCanvasViewPortBuffer();
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
		throws Exception
	{
		image = gPanel.getCanvasBuffer();
		exportView(image, true);
	}

	private void exportView(BufferedImage image, Boolean full)
		throws Exception
	{
		BufferedImage map = gPanel.getMapCanvasBuffer(full);
		BufferedImage lines = gPanel.getLineCanvasBuffer(full);
		BufferedImage traits = gPanel.getTraitCanvasBuffer(full);
		BufferedImage qtls = gPanel.getQTLCanvasBuffer(full);

		if (image == null)
			throw new RuntimeException(RB.getString("io.ImageExporter.noBufferError"));

		// Buffer to hold the final composite of the three images
		BufferedImage finalImage;

		int lineWidth = 0;
		if(Prefs.visShowLinePanel)
			lineWidth = lines.getWidth();

		int mapHeight = 0;
		if(Prefs.visShowMapCanvas)
			mapHeight = map.getHeight();

		int traitsWidth = 0;
		if(traits != null && Prefs.visShowTraitCanvas)
			traitsWidth = traits.getWidth() +5;

		int qtlsHeight = 0;
		if(qtls != null && Prefs.visShowQTLCanvas)
			qtlsHeight = qtls.getHeight();

		// Work out the total canvas size
		int w = lineWidth + image.getWidth() + traitsWidth;
		int h = qtlsHeight + image.getHeight() + mapHeight;

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
		if(Prefs.visShowQTLCanvas)
			g.drawImage(qtls, lineWidth+traitsWidth, 0, null);
		if(Prefs.visShowMapCanvas)
			g.drawImage(map, lineWidth+traitsWidth, qtlsHeight, null);
		if(Prefs.visShowTraitCanvas)
			g.drawImage(traits, 0, qtlsHeight+mapHeight, null);
		if(Prefs.visShowLinePanel)
			g.drawImage(lines, traitsWidth, qtlsHeight+mapHeight, null);
		g.drawImage(image, lineWidth+traitsWidth, qtlsHeight+mapHeight, null);
		g.dispose();

		ImageIO.write(finalImage, "png", file);
	}

	public boolean isIndeterminate()
		{ return true; }

	public int getMaximum()
		{ return 0; }

	public int getValue()
		{ return 0; }

	public void cancelJob()
	{
	}
}