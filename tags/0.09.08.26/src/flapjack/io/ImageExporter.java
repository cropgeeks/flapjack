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

		if (image == null)
			throw new RuntimeException(RB.getString("io.ImageExporter.oomError"));

		ImageIO.write(image, "png", file);
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
		BufferedImage map = gPanel.getMapCanvasBuffer();
		BufferedImage lines = gPanel.getLineCanvasBuffer();

		if (image == null || map == null || lines == null)
			throw new RuntimeException(RB.getString("io.ImageExporter.noBufferError"));

		// Buffer to hold the final composite of the three images
		BufferedImage finalImage;

		int lineWidth = lines.getWidth();
		int mapWidth = map.getWidth();
		int mapHeight = map.getHeight();
		int lineHeight = image.getHeight();

		// Work out the total canvas size
		int w = lineWidth + mapWidth;
		int h = lineHeight + mapHeight;

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
		g.drawImage(map, lineWidth, 0, null);
		g.drawImage(lines, 0, mapHeight, null);
		g.drawImage(image, lineWidth, mapHeight, null);
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