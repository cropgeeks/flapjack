// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public class TitlePanel extends JPanel
{
	private String title;

	private static Color labelForeground = (Color) UIManager.get("Label.foreground");
	private static Color panelBackground = (Color) UIManager.get("MenuBar.background");

	public TitlePanel(String title)
	{
		this.title = title;
	}

	public Dimension getPreferredSize()
	{
		return new Dimension(50, 20);
	}

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		Graphics2D g = (Graphics2D) graphics;

		g.setPaint(new GradientPaint(0, 0, Color.white, 0, getHeight(), panelBackground));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(panelBackground);
		g.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);

		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(labelForeground);

		int w = getWidth();
		int h = getHeight();

		Rectangle2D.Float r = (Rectangle2D.Float) g.getFontMetrics().getStringBounds(title, g);

		g.drawString(title, (int)(w/2-r.width/2), (int)(h/2+r.height/3));
	}
}