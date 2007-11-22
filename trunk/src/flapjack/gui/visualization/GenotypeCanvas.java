package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;

import flapjack.data.*;

class GenotypeCanvas extends JPanel
{
	private GenotypeDisplayPanel gdPanel;

	private DataSet dataSet;
	private ChromosomeMap map;

	// For faster rendering, maintain a local cache of the data to be drawn
	private Vector<GenotypeData> genotypeLines;

	// The number of lines and the number of markers being drawn
	int nLines, nMarkers;
	// Also referred to as:
	int boxTotalX, boxTotalY;
	// Width and height of the main drawing canvas
	int canvasW, canvasH;
	// Width and height of an allele "box"
	int boxW, boxH;
	// How many boxes will fit into the current screen size?
	int boxCountX, boxCountY;

	// These are the x and y pixel positions on the canvas that currently appear
	// in the top left corner of the current view
	int pX, pY;

	// Holds the current dimensions of the canvas in an AWT friendly format
	private Dimension dimension;

	boolean renderLive = false;

	GenotypeCanvas(GenotypeDisplayPanel gdPanel, DataSet dataSet, ChromosomeMap map)
	{
		this.gdPanel = gdPanel;
		this.dataSet = dataSet;
		this.map = map;


		renderLive = true;
		setOpaque(!renderLive);


		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e)
			{
				renderLive = !renderLive;

				System.out.println("Rendering to offscreen buffer = " + (!renderLive));

				if (renderLive)
					setOpaque(false);
				else
					setOpaque(true);

				repaint();
			}
		});

/*		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e)
			{
				System.out.println(e.getPoint().x + ", " + e.getPoint().y);
			}
		});
*/

		cacheLines();
//		computeDimensions();
	}

	private void cacheLines()
	{
		genotypeLines = new Vector<GenotypeData>(dataSet.countLines());

		for (int i = 0; i < dataSet.countLines(); i++)
		{
			Line line = dataSet.getLineByIndex(i);
			GenotypeData data = line.getGenotypeDataByMap(map);

			genotypeLines.add(data);
		}
	}

	// Compute canvas related dimensions that only change if the data or the
	// box-drawing size needs to be changed
	void computeDimensions(int size)
	{
		Font font = new Font("Monospaced", Font.PLAIN, size);
		FontMetrics fm = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
			.getGraphics().getFontMetrics(font);

		boxW = size;
		boxH = fm.getHeight();

		// Once we have suitable width/height values, the scrollbars can be made
		// to lock to those so we never have to draw less than a full box
		gdPanel.computeScrollbarAdjustmentValues(boxW, boxH);

		boxTotalY = nLines = genotypeLines.size();
		boxTotalX = nMarkers = map.countLoci();

		canvasW = (boxTotalX * boxW) + (boxW);// - 1);
		canvasH = boxTotalY * boxH;

		image = null;

		System.out.println("boxW = " + boxW + ", boxH = " + boxH);
		System.out.println("canvasW = " + canvasW + ", canvasH = " + canvasH);

		int bufferSize = canvasW * canvasH;
		System.out.println("Canvas buffer requires: " + (bufferSize/1024f/1024f) + " MB");

		dimension = new Dimension(canvasW, canvasH);
		setSize(dimension);
	}

	// Compute real-time variables, that change as the viewpoint is moved across
	// the canvas
	void computeForRedraw(Dimension viewSize, Point viewPosition)
	{
		boxCountX = 1 + (int) ((float) viewSize.getWidth()  / boxW);
		boxCountY = 1 + (int) ((float) viewSize.getHeight() / boxH);

		pX = viewPosition.x;
		pY = viewPosition.y;

//		repaint();
	}

	public Dimension getSize()
		{ return dimension; }

	public Dimension getPreferredSize()
		{ return dimension; }



	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		Graphics2D g = (Graphics2D) graphics;

		long s = System.nanoTime();
		if (renderLive)
			renderRegion(g);
		else
			renderImage(g);
		long e = System.nanoTime();

		System.out.println("Render time: " + ((e-s)/1000000f) + "ms");
	}

	private void renderRegion(Graphics2D g)
	{
		// These are the index positions within the dataset that we'll start
		// drawing from
		int xIndexStart = pX / boxW;
		int yIndexStart = pY / boxH;

		// The end indices are calculated as the:
		//   (the start index) + (the number that can be drawn on screen)
		// with a check to set the end index to the last value in the array if
		// the calculated index would go out of bounds
		int xIndexEnd = xIndexStart + boxCountX;
		if (xIndexEnd >= boxTotalX)
			xIndexEnd = boxTotalX-1;

		int yIndexEnd = yIndexStart + boxCountY;
		if (yIndexEnd >= boxTotalY)
			yIndexEnd = boxTotalY-1;

		render(g, xIndexStart, xIndexEnd, yIndexStart, yIndexEnd);
	}

	private BufferedImage image;

	private void renderImage(Graphics2D g)
	{
		if (image == null)
		{
			image = new BufferedImage(canvasW, canvasH, BufferedImage.TYPE_BYTE_INDEXED);
			Graphics buffG = image.createGraphics();

			render(buffG, 0, boxTotalX-1, 0, boxTotalY-1);

			buffG.dispose();
		}

		g.drawImage(image, 0, 0, null);


//		if (img == null)
//			ImagePanel();
//		g.drawImage(img, 0, 0, null);

	}

	private void render(Graphics g, int xS, int xE, int yS, int yE)
	{
		g.setColor(Color.white);
		g.fillRect(0, 0, canvasW, canvasH);

		for (int yIndex = yS, y = (boxH*yS); yIndex <= yE; yIndex++, y += boxH)
		{
			GenotypeData data = genotypeLines.get(yIndex);
			byte[] loci = data.getLociData();

			for (int xIndex = xS, x = pX; xIndex <= xE; xIndex++, x += boxW)
			{
				if (loci[xIndex] > 0)
				{
					g.setColor(
						dataSet.getStateTable().getAlleleState(loci[xIndex]).getColor());

					g.fillRect(x, y, boxW, boxH);

//					if (dataSet.getStateTable().getAlleleState(loci[xIndex]).isHomozygous() == false)
//					{
//						g.setColor(Color.black);
//						g.drawRect(x, y, boxW, boxH);
//					}

//					g.setColor(Color.black);
//					g.drawString("" + loci[xIndex], x+2, y+boxH-3);
				}
			}
		}
	}


	BufferedImage img;
	int box = GenotypeDisplayPanel.BS;


	Color[] colors = new Color[] {Color.BLACK, Color.BLUE, Color.CYAN, Color.GREEN};

	int w;
	int h;

	void ImagePanel()
	{
		int yMax = boxTotalY;
		int xMax = boxTotalX;

		this.w = box*xMax;
		this.h = box*yMax;

		System.out.println(w + "x" + h);

//		w = 15002;
//		h = 3000;

//		System.out.println(w + "x" + h);

		setSize(w, h);

		System.out.println("Size: "+(3*yMax*xMax*box/1024/1024)+" MB");


		img = new BufferedImage(this.w, this.h, BufferedImage.TYPE_BYTE_INDEXED);
		Graphics g = img.getGraphics();


		g.setColor(Color.white);
		g.fillRect(0, 0, w, h);
		g.setColor(Color.black);

		Random r = new Random();

		for(int i=0; i<xMax; i++)
		{
			for(int j=0; j<yMax; j++) {
				int x = i*box;
				int y = j*box;

				int c = r.nextInt(3);
				g.setColor(colors[c]);

				g.fillRect(x, y, box, box);

				if (x % 50 == 0)
					g.drawString(""+ x, x, 50);

			}
			//System.out.println(i);
		}
		g.dispose();
	}

}