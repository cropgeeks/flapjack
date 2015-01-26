// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.dialog.pedigrees;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.gui.*;

import scri.commons.gui.*;

class PedigreePanel extends JPanel
{
	private BufferedImage image;
	private JScrollPane sp;

	PedigreePanel(BufferedImage sImage)
	{
		// Copy the image from the server onto a new buffer (which will do any
		// image format conversions, making final rendering faster). Deals with
		// the PNG image type = TYPE_CUSTOM problems.
		// TODO: Check for OOM error
		image = new BufferedImage(sImage.getWidth(), sImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.drawImage(sImage, 0, 0, null);
		g.dispose();

		MouseHandler mh = new MouseHandler();
		addMouseListener(mh);
		addMouseMotionListener(mh);

		setBackground(Color.white);
	}

	void setScrollPane(JScrollPane sp)
		{ this.sp = sp; }

	public Dimension getPreferredSize()
	{
		return new Dimension(image.getWidth(), image.getHeight());
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		Dimension vs = sp.getViewport().getViewSize();
		Point vp = sp.getViewport().getViewPosition();

		g.drawImage(image, vp.x, vp.y, vp.x+vs.width, vp.y+vs.height,
			vp.x, vp.y, vp.x+vs.width, vp.y+vs.height, null);

//		g.drawImage(image, 0, 0, getWidth(), getHeight(),
//			0, 0, image.getWidth(), image.getHeight(), null);
	}

	private class MouseHandler extends MouseInputAdapter
	{
		private Point dragPoint;

		public void mousePressed(MouseEvent e)
		{
			dragPoint = e.getPoint();
		}

		public void mouseReleased(MouseEvent e)
		{
			// Reset any dragging variables
			dragPoint = null;
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}

		public void mouseDragged(MouseEvent e)
		{
			// Dragging the canvas...
			if (dragPoint != null)
			{
				setCursor(new Cursor(Cursor.HAND_CURSOR));

				int diffX = dragPoint.x - e.getPoint().x;
				int diffY = dragPoint.y - e.getPoint().y;

				sp.getHorizontalScrollBar().setValue(
					sp.getHorizontalScrollBar().getValue() + diffX);
				sp.getVerticalScrollBar().setValue(
					sp.getVerticalScrollBar().getValue() + diffY);
			}
		}
	}
}