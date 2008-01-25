package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.data.*;
import flapjack.gui.*;

public class OverviewDialog extends JDialog
{
	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;

	private Canvas2D viewCanvas = new Canvas2D();

	private int boxX, boxY, boxW, boxH;

	private BufferFactory bufferFactory = null;
	private BufferedImage image = null;

	public OverviewDialog(GenotypePanel gPanel, GenotypeCanvas canvas)
	{
		super(Flapjack.winMain, RB.getString("gui.visualization.OverviewDialog.title"), false);

		this.gPanel = gPanel;
		this.canvas = canvas;

		setLayout(new BorderLayout());
		add(viewCanvas);

		setSize(Prefs.guiOverviewWidth, Prefs.guiOverviewHeight);

		// Work out the current screen's width and height
		int scrnW = Toolkit.getDefaultToolkit().getScreenSize().width;
		int scrnH = Toolkit.getDefaultToolkit().getScreenSize().height;

		// Determine where on screen (TODO: on which monitor?) to display
		if (Prefs.guiOverviewX > (scrnW-50) || Prefs.guiOverviewY > (scrnH-50))
			setLocationRelativeTo(Flapjack.winMain);
		else
			setLocation(Prefs.guiOverviewX, Prefs.guiOverviewY);

		addListeners();
	}

	private void addListeners()
	{
		addWindowListener(new WindowAdapter()
		{
			public void windowOpened(WindowEvent e)
			{
				createImage();
			}
		});

		addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				Prefs.guiOverviewWidth = getSize().width;
				Prefs.guiOverviewHeight = getSize().height;
				Prefs.guiOverviewX = getLocation().x;
				Prefs.guiOverviewY = getLocation().y;

				createImage();
			}

			public void componentMoved(ComponentEvent e)
			{
				Prefs.guiOverviewX = getLocation().x;
				Prefs.guiOverviewY = getLocation().y;
			}
		});
	}

	public void exit()
	{
//		Prefs.gui_odialog_x = getX();
//		Prefs.gui_odialog_y = getY();
//		Prefs.gui_odialog_w = getWidth();
//		Prefs.gui_odialog_h = getHeight();
	}

/*	public void setAlignmentPanel(AlignmentPanel newPanel)
	{
		if (panel == newPanel)
			return;

		panel = newPanel;

		if (panel != null)
		{
			seqCount = panel.getSequenceSet().getSize();
			nucCount = panel.getSequenceSet().getLength();

			createImage();
		} else
			repaint();
	}
*/

	void updateOverviewSelectionBox(int xIndex, int xW, int yIndex, int yH)
	{
		if (gPanel == null || bufferFactory == null)
			return;

		// Work out the x/y position for the outline box
		boxX = (int) (bufferFactory.xScale * xIndex);
		boxY = (int) (bufferFactory.yScale * yIndex);

		// Work out the width/height for the outline box
		if (xW >= canvas.boxTotalX)
			boxW = viewCanvas.getWidth() - 1;
		else
			boxW = (int) (bufferFactory.xScale * xW);
		if (yH >= canvas.boxTotalY)
			boxH = viewCanvas.getHeight() - 1;
		else
			boxH = (int) (bufferFactory.yScale * yH + bufferFactory.yHeight);

		repaint();
	}

	public void createImage()
	{
		int w = viewCanvas.getSize().width;
		int h = viewCanvas.getSize().height;

		if (w == 0 || h == 0 || gPanel == null || isVisible() == false)
			return;

		image = null;

		// Kill off any old image generation that might still be running...
		if (bufferFactory != null)
			bufferFactory.killMe = true;
		// Before starting a new one
		bufferFactory = new BufferFactory(w, h);

		repaint();
	}

	private void bufferAvailable(BufferedImage image)
	{
		this.image = image;

		// Force the main canvas to send its view size dimensions so we can draw
		// the highlighting box on top of the new back buffer's image
		gPanel.forceOverviewUpdate();
	}

	private class Canvas2D extends JPanel
	{
		Canvas2D()
		{
			setOpaque(false);

			addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent e)
					{ processMouse(e); }

				public void mousePressed(MouseEvent e)
					{ processMouse(e); }

				public void mouseReleased(MouseEvent e)
					{ processMouse(e); }
			});

			addMouseMotionListener(new MouseMotionAdapter()
			{
				public void mouseDragged(MouseEvent e)
					{ processMouse(e); }
			});
		}

		private void processMouse(MouseEvent e)
		{
			if (gPanel == null)
				return;

			int x = e.getX() - (int) (boxW / 2f);
			int y = e.getY() - (int) (boxH / 2f);

			// Compute mouse position (and adjust by wid/hgt of rectangle)
			int xIndex = (int) (x / bufferFactory.xScale);
			int yIndex = (int) (y / bufferFactory.yScale);

			gPanel.jumpToPosition(xIndex, yIndex);
		}

		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);

			if (gPanel == null)
			{
			}

			else if (image != null)
			{
				// Paint the image of the alignment
				g.drawImage(image, 0, 0, null);

				// Then draw the tracking rectangle
				((Graphics2D) g).setPaint(new Color(50, 50, 0, 50));
				g.fillRect(boxX, boxY, boxW, boxH);
				g.setColor(Color.red);
				g.drawRect(boxX, boxY, boxW, boxH);
			}

			else
			{
				String str = RB.getString("gui.visualization.OverviewDialog.buffer");
				int strWidth = g.getFontMetrics().stringWidth(str);

				g.setColor(Color.lightGray);
				g.drawString(str, (int) (getWidth() / 2f - strWidth / 2f),
						getHeight() / 2);
			}
		}
	}

	private class BufferFactory extends Thread
	{
		// Width and height of the image to be created
		private int w, h;

		private BufferedImage buffer;

		// Give up drawing if set to true
		boolean killMe = false;

		private float xScale, yScale;

		private int xWidth, yHeight;

		BufferFactory(int w, int h)
		{
			this.w = w;
			this.h = h;

			start();
		}

		public void run()
		{
			setPriority(Thread.MIN_PRIORITY);

			buffer = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_INDEXED);
			Graphics2D g = buffer.createGraphics();

			g.setColor(Color.white);
			g.fillRect(0, 0, w, h);


			int boxTotalX = canvas.boxTotalX;
			int boxTotalY = canvas.boxTotalY;


			// Scaling factors
			xScale = w / (float) boxTotalX;
			yScale = h / (float) boxTotalY;

			// Width of each X element (SEE NOTE BELOW)
			xWidth = 1 + (int) ((xScale >= 1) ? xScale : 1);
			// Height of each Y element
			yHeight = 1 + (int) ((yScale >= 1) ? yScale : 1);

	//		StateTable table = canvas.dataSet.getStateTable();

			// What were the x and y positions of the last point drawn? If the next
			// point to be drawn ISN'T different, then we won't bother drawing it,
			// and will save a significant amount of time
			int lastX = -1;
			int lastY = -1;

			float y = 0;
			for (int yIndex = 0; yIndex < boxTotalY && !killMe; yIndex++)
			{
				GenotypeData data = canvas.genotypeLines.get(yIndex);

				float x = 0;
				for (int xIndex = 0; xIndex < boxTotalX && !killMe; xIndex++)
				{
					// This is where we save the time...
					if ((int)x != lastX || (int)y != lastY)
					{
						int state = data.getState(xIndex);

						if (state > 0)
						{
	//						g.setColor(table.getAlleleState(data.getState(xIndex)).getColor());
							g.setColor(canvas.cTable.get(state).getColor());
							g.fillRect((int)x, (int)y, xWidth, yHeight);

							lastX = (int)x;
							lastY = (int)y;
						}
					}

					x += xScale;
				}

				y += yScale;
			}

			if (!killMe)
			{
				// Once complete, let the dialog know its image is ready
				bufferAvailable(buffer);
			}
		}

		// We use (1 +) to deal with integer roundoff that results in columns
		// being skipped due to overlaps: eg with width of 1.2:
		// 1.2 (1) 2.4 (2) 3.6 (3) 4.8 (4) 6.0 (6)
		// position 5 was skipped
	}
}