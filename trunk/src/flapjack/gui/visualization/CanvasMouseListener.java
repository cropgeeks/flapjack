package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;

class CanvasMouseListener extends MouseInputAdapter
{
	private GenotypeCanvas canvas;

	CanvasMouseListener(GenotypeCanvas canvas)
	{
		this.canvas = canvas;
	}

	public void mouseMoved(MouseEvent e)
	{
		int x = e.getPoint().x;
		int y = e.getPoint().y;

		int xIndex = x / canvas.boxW;
		int yIndex = y / canvas.boxH;

//		System.out.println("xIndex = " + xIndex + ", yIndex = " + yIndex);
	}
}

class CanvasToolTip extends JToolTip
{
	Color bgColor = new JLabel().createToolTip().getBackground();

	int w, h;

	CanvasToolTip()
	{
		w = 100;
		h = 100;
	}

	public Dimension getPreferredSize()
	{
		return new Dimension(w, h);
	}

	public void paintComponent(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics;

		g.setPaint(new GradientPaint(0, 0, Color.red.brighter(), w, h, Color.red.darker()));

		Rectangle2D.Float r = null;

//		RoundRectangle2D.Float r = new RoundRectangle2D.Float(1, 1, w-1, h-1, 7, 7);
//		Rectangle2D.Float r = new Rectangle2D.Float(0, 0, w, h);

		r = new Rectangle2D.Float(0, 0, w, h);

		g.fill(r);
	}
}