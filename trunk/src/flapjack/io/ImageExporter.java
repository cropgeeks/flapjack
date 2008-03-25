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
			throw new RuntimeException("Unable to create allocated image: "
				+ "it may be too large to fit in available memory");

		ImageIO.write(image, "png", file);
	}

	private void exportEntireView()
		throws Exception
	{
		image = gPanel.getCanvasBuffer();

		if (image == null)
			throw new RuntimeException("Unable to create allocated image: "
				+ "it may be too large to fit in available memory");

		ImageIO.write(image, "png", file);
	}

	private void exportOverview()
		throws Exception
	{
		int w = Prefs.guiExportImageX;
		int h = Prefs.guiExportImageY;

		image = OverviewManager.getExportableImage(w, h);

		if (image == null)
			throw new RuntimeException("Unable to create allocated image: "
				+ "it may be too large to fit in available memory");

		ImageIO.write(image, "png", file);
	}
}