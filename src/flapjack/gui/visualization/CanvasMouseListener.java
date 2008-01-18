package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;

class CanvasMouseListener extends MouseInputAdapter
{
	private GenotypeCanvas canvas;
	private GenotypePanel gPanel;

	CanvasMouseListener(GenotypeCanvas canvas, GenotypePanel gPanel)
	{
		this.canvas = canvas;
		this.gPanel = gPanel;

		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
	}

	public void mouseClicked(MouseEvent e)
	{
		if (e.isControlDown() && e.getClickCount() == 2)// && canvas.boxH == 16)
		{
			canvas.removeMouseListener(this);
			new MineSweeper(canvas);
		}

		else if (e.getClickCount() == 2)
		{
			if (canvas.renderMode == 0)
				canvas.renderMode = 1;
			else
				canvas.renderMode = 0;

			System.out.println("Drawing to buffer = " + (canvas.renderMode == 1));

//				if (canvas.renderLive)
//					canvas.setOpaque(true);
//				else
//					canvas.setOpaque(false);

			// setOpaque false is the default in JComponent

			canvas.repaint();
		}

		else if (e.getClickCount() == 1)
		{
			canvas.locked = !canvas.locked;
		}
	}

	public void mouseMoved(MouseEvent e)
	{
		try
		{
			int x = e.getPoint().x;
			int y = e.getPoint().y;

			int xIndex = x / canvas.boxW;
			int yIndex = y / canvas.boxH;

			gPanel.overRow(xIndex, yIndex);


//			System.out.println("xIndex = " + xIndex + ", yIndex = " + yIndex);
		}
		// Catching divide-by-zero if the canvas has no data (and hence size)
		catch (ArithmeticException ae) {}
	}

	public void mouseExited(MouseEvent e)
	{
		gPanel.overRow(-1, -1);
	}
}

class CanvasToolTip extends JToolTip
{
	Color bgColor = (Color) UIManager.get("ToolTip.background");// new JLabel().createToolTip().getBackground();

	int w, h;

	CanvasToolTip()
	{
		w = 100;
		h = 25;
	}

//	public Dimension getPreferredSize()
//	{
//		return new Dimension(w, h);
//	}

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		Graphics2D g = (Graphics2D) graphics;

		g.setPaint(new GradientPaint(0, 0, Color.red.brighter(), w, h, Color.red.darker()));

		Rectangle2D.Float r = null;

//		RoundRectangle2D.Float r = new RoundRectangle2D.Float(1, 1, w-1, h-1, 7, 7);
//		Rectangle2D.Float r = new Rectangle2D.Float(0, 0, w, h);

		r = new Rectangle2D.Float(0, 0, w, h);

//		g.fill(r);
	}
}