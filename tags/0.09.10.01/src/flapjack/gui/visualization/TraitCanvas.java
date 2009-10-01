// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.data.*;
import flapjack.gui.*;

import java.awt.image.*;
import scri.commons.gui.*;

class TraitCanvas extends JPanel
{
	private NumberFormat nf = NumberFormat.getInstance();

	private GenotypePanel gPanel;
	private GenotypeCanvas canvas;
	private Canvas2D traitCanvas;

	private int boxW = 10;
	private int w = 0;

	private int mouseOverIndex = -1;

	boolean full;

	TraitCanvas(GenotypePanel gPanel, GenotypeCanvas canvas)
	{
		this.gPanel = gPanel;
		this.canvas = canvas;

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(1, 5, 0, 5));
		add(traitCanvas = new Canvas2D());
	}

	int getPanelWidth()
	{
		return isVisible() ? getWidth() : 0;
	}

	// Decides whether to show this panel or not, based on a) are there traits
	// that *can* be shown, and b) does the user want to see the panel
	void determineVisibility()
	{
		int traitCount = 0;
		if (canvas.viewSet != null)
//			traitCount = canvas.viewSet.getDataSet().getTraits().size();
			traitCount = canvas.viewSet.getTraits().length;

		w = boxW * traitCount;

		setVisible(traitCount > 0 && Prefs.visShowTraitCanvas);
	}

	BufferedImage createSavableImage(boolean full)
	{
		this.full = full;
		// Note that this *doesn't* happen in a new thread as the assumption is
		// that this will be called by a threaded process anyway
		BufferFactory tempFactory;
		if(full)
			tempFactory = new BufferFactory(w, (canvas.boxH*canvas.boxTotalY), true, 0, canvas.canvasW, 0, canvas.canvasH);
		else
			tempFactory = new BufferFactory(w, (canvas.boxH*canvas.boxTotalY), true, canvas.pX1, canvas.pX2, canvas.pY1, canvas.pY2);
		tempFactory.run();

		return tempFactory.buffer;
	}

	private class Canvas2D extends JPanel
	{
		Canvas2D()
		{
			MouseTracker mt = new MouseTracker();

			addMouseListener(mt);
			addMouseMotionListener(mt);
		}

		void drawCanvas(Graphics2D g, int height)
		{
			int[] tIndex = canvas.viewSet.getTraits();


			int boxH = canvas.boxH;
			int yS = 0 / canvas.boxH;
			int yE = yS + height / canvas.boxH;
			if (yE >= canvas.boxTotalY)
				yE = canvas.boxTotalY-1;

			// Translate the drawing to give the appearance of scrolling
			g.setClip(0, yS, w, height);
			//g.translate(0, yS-canvas.pY1);


			Color col1 = Prefs.visColorHeatmapLow;
			int[] c1 = new int[] { col1.getRed(), col1.getGreen(), col1.getBlue() };
			Color col2 = Prefs.visColorHeatmapHigh;
			int[] c2 = new int[] { col2.getRed(), col2.getGreen(), col2.getBlue() };

			for (int i = 0; i < tIndex.length; i++)
			{
				// If there's no index for this location, skip it
				if (tIndex[i] == -1)
					continue;

				for (int yIndex = yS, y = yS; yIndex <= yE; yIndex++, y += boxH)
				{
					Line line = canvas.view.getLine(yIndex);
					// Skip dummy lines (they don't have trait values)
					if (canvas.view.isDummyLine(line))
						continue;

					TraitValue tv = line.getTraitValues().get(tIndex[i]);

					// Or if the trait is undefined, just skip it
					if (tv.isDefined() == false)
						continue;

					float f1 = (float) (1.0 - tv.getNormal());
					float f2 = (float) tv.getNormal();

					g.setColor(new Color(
	          			(int) (f1 * c1[0] + f2 * c2[0]),
          				(int) (f1 * c1[1] + f2 * c2[1]),
          				(int) (f1 * c1[2] + f2 * c2[2])));

					g.fillRect(i*boxW, y, boxW, boxH);
				}
			}
		}

		public Dimension getPreferredSize()
			{ return new Dimension(w, 0); }

		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);
			Graphics2D g = (Graphics2D) graphics;

			g.translate(0, -canvas.pY1);

			drawCanvas(g, canvas.pY2);
		}
	}

	private class MouseTracker extends MouseInputAdapter
	{
		public void mouseEntered(MouseEvent e)
		{
			gPanel.statusPanel.setForHeatmapUse();
			mouseOverIndex = e.getPoint().x / boxW;
		}

		public void mouseExited(MouseEvent e)
		{
			gPanel.statusPanel.setForMainUse();
			gPanel.statusPanel.setHeatmapValues(" ", " ", " ");
			mouseOverIndex = -1;
		}

		// Works out which line/trait/value is under the mouse and displays this
		// on the main status panel
		public void mouseMoved(MouseEvent e)
		{
			int y = e.getPoint().y + canvas.pY1;

			int yIndex = y / canvas.boxH;
			mouseOverIndex = e.getPoint().x / boxW;

			int[] traits = canvas.viewSet.getTraits();

			if (mouseOverIndex < 0 || mouseOverIndex >= traits.length)
				return;

			int tIndex = traits[mouseOverIndex];

			// Don't attempt to set a tooltip if there's no trait displayed or
			// if the mouse isn't over an actual line
			if (tIndex == -1 || yIndex > canvas.view.getLineCount()-1)
			{
				gPanel.statusPanel.setHeatmapValues(" ", " ", " ");
				return;
			}

			Line line = canvas.view.getLine(yIndex);
			// Don't attempt to display information for dummy lines
			if (canvas.view.isDummyLine(line))
			{
				gPanel.statusPanel.setHeatmapValues(" ", " ", " ");
				return;
			}

			TraitValue tv = line.getTraitValues().get(tIndex);

			String trait = tv.getTrait().getName();
			String value = " ";

			if (tv.isDefined() && tv.getTrait().traitIsNumerical())
				value = nf.format(tv.getValue());
			else if (tv.isDefined())
				value = tv.getTrait().format(tv);

			gPanel.statusPanel.setHeatmapValues(line.getName(), trait, value);
		}

		public void mousePressed(MouseEvent e)
		{
			if (e.isPopupTrigger())
				handlePopup(e);
		}

		public void mouseReleased(MouseEvent e)
		{
			if (e.isPopupTrigger())
				handlePopup(e);
		}

		// Pops up a menu with all the current traits, allowing the user to
		// quickly select a new trait (for the column under the mouse)
		private void handlePopup(MouseEvent e)
		{
			JPopupMenu menu = new JPopupMenu();

			JMenuItem item = new JMenuItem();
			RB.setText(item, "gui.visualization.TraitCanvas.popup");

			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)	{
					Flapjack.winMain.mViz.vizSelectTraits();
				}
			});

			menu.add(item);
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	private class BufferFactory extends Thread
	{
		BufferedImage buffer;

		// isTempBuffer = true when a buffer is being made for saving as an image
		private boolean isTempBuffer = false;
		private int w, h, xS, xE, yS, yE;

		BufferFactory(int w, int h, boolean isTempBuffer, int x1, int x2, int y1, int y2)
		{
			this.w = w;
			this.h = h;
			this.isTempBuffer = isTempBuffer;
			this.xS = x1;
			this.xE = x2;
			this.yS = y1;
			this.yE = y2;
		}

		public void run()
		{
			// Run everything under try/catch conditions due to changes in the
			// view that may invalidate what this thread is trying to access
			try
			{
				createBuffer();
			}
			catch (Exception e)
			{
				System.out.println("MapCanvas: " + e);
			}
		}

		private void createBuffer()
			throws ArrayIndexOutOfBoundsException
		{
			try
			{
				buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			}
			catch (Throwable t) { return; }

			Graphics2D g2d = buffer.createGraphics();

			// Enable anti-aliased graphics to smooth the line jaggies
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			//int height = (canvas.pY2-canvas.pY1);
			//int height = canvas.boxH*canvas.boxTotalY;

			if (isTempBuffer)
				g2d.setColor(Color.white);
			else
				// Paint the background
				g2d.setColor(Prefs.visColorBackground);
			g2d.fillRect(0, 0, w, h);
			if(!full)
				g2d.translate(0, -canvas.pY1);
			traitCanvas.drawCanvas(g2d, (canvas.boxH*canvas.boxTotalY));
			g2d.dispose();
		}
	}
}