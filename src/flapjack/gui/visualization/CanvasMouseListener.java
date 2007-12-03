package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;

class CanvasMouseListener extends MouseInputAdapter
{
	private GenotypeCanvas canvas;
	private GenotypeDisplayPanel gdPanel;

	CanvasMouseListener(GenotypeCanvas canvas, GenotypeDisplayPanel gdPanel)
	{
		this.canvas = canvas;
		this.gdPanel = gdPanel;

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


	}

	public void mouseMoved(MouseEvent e)
	{
		int x = e.getPoint().x;
		int y = e.getPoint().y;

		int xIndex = x / canvas.boxW;
		int yIndex = y / canvas.boxH;

		gdPanel.overRow(yIndex);

//		System.out.println("xIndex = " + xIndex + ", yIndex = " + yIndex);
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