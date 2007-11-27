package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.gui.*;

public class OverviewDialog extends JDialog
{
	private OverviewCanvas canvas = new OverviewCanvas();

	private GenotypeDisplayPanel gdPanel;

	private int seqCount, nucCount;
	private int boxTotalX, boxTotalY;

	// Tracks the MOST RECENT thread that is generating an image
	private BufferedImage image = null;

	private OverviewGenerator imager = null;

	public OverviewDialog(GenotypeDisplayPanel gdPanel)
	{
		super(Flapjack.winMain, "Overview", false);

		this.gdPanel = gdPanel;

		setLayout(new BorderLayout());
		add(canvas);


		setSize(300, 300);

		addListeners();


//		setSize(Prefs.gui_odialog_w, Prefs.gui_odialog_h);
//		if (Prefs.gui_odialog_x == -1)
//			setLocationRelativeTo(winMain);
//		else
//			setLocation(Prefs.gui_odialog_x, Prefs.gui_odialog_y);
	}

	private void addListeners()
	{
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowOpened(WindowEvent e)
			{
				createImage();
			}
		});

		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				createImage();
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
		if (gdPanel == null || imager == null)
			return;

		// Work out the x/y position for the outline box
		canvas.boxX = (int) (imager.xScale * xIndex);
		canvas.boxY = (int) (imager.yScale * yIndex);

		// Work out the width/height for the outline box
		if (xW >= gdPanel.canvas.boxTotalX)
			canvas.boxW = canvas.getWidth() - 1;
		else
			canvas.boxW = (int) (imager.xScale * xW);
		if (yH >= gdPanel.canvas.boxTotalY)
			canvas.boxH = canvas.getHeight() - 1;
		else
			canvas.boxH = (int) (imager.yScale * yH + imager.yHeight);

		canvas.repaint();
	}

	public void createImage()
	{
		int w = canvas.getSize().width;
		int h = canvas.getSize().height;

		if (w == 0 || h == 0 || gdPanel == null || isVisible() == false)
			return;

		image = null;

		// Kill off any old image generation that might still be running...
		if (imager != null)
			imager.killMe = true;
		// Before starting a new one
		imager = new OverviewGenerator(this, gdPanel, w, h);

		canvas.repaint();
	}

	void imageAvailable(OverviewGenerator generator)
	{
		// We need this check because multiple user resizes of the window,
		// *before* the image has finished, will kick off additional threads.
		// There's no way around it, but we just make sure the image shown is
		// from the last thread started, as it will match the window size.
		if (generator == imager)
		{
			image = imager.getImage();
			gdPanel.forceOverviewUpdate();
			repaint();
		}
	}

	class OverviewCanvas extends JPanel
	{
		int boxX, boxY, boxW, boxH;

		OverviewCanvas()
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
			if (gdPanel == null)
				return;

			int x = e.getX() - (int) (boxW / 2f);
			int y = e.getY() - (int) (boxH / 2f);

			// Compute mouse position (and adjust by wid/hgt of rectangle)
			int xIndex = (int) (x / imager.xScale);
			int yIndex = (int) (y / imager.yScale);

			gdPanel.jumpToPosition(xIndex, yIndex);
		}

		@Override
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);

			if (gdPanel == null)
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
				String s = "generating overview...please be patient";
				int length = g.getFontMetrics().stringWidth(s);

				g.setColor(Color.lightGray);
				g.drawString(s, (int) (getWidth() / 2f - length / 2f),
						getHeight() / 2);
			}
		}
	}
}