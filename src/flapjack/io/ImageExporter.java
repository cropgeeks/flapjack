package flapjack.io;

import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

import flapjack.gui.*;
import flapjack.gui.visualization.*;

public class ImageExporter
{
	private GenotypePanel gPanel;
	private File file;

	private BufferedImage image;

	public ImageExporter(GenotypePanel gPanel, File file)
	{
		this.gPanel = gPanel;
		this.file = file;
	}

	public void doExport()
		throws IOException
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
	{
	}

	private void exportEntireView()
	{
	}

	private void exportOverview()
		throws IOException
	{
		int w = Prefs.guiExportImageX;
		int h = Prefs.guiExportImageY;

		image = OverviewManager.getExportableImage(w, h);

		ImageIO.write(image, "png", file);
	}
}