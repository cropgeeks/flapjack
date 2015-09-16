// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui;

import java.awt.*;
import javax.swing.*;

import scri.commons.gui.*;

public class TitlePanel3 extends JPanel
{
	private static final Color lineColor = new Color(207, 219, 234);
	private static final Color textColor = new Color(75, 105, 150);

	private Font font;
	private String title;

	public TitlePanel3(String title)
	{
		this.title = title;
		setOpaque(false);

		font = UIScaler.getFont(new Font("Dialog", Font.BOLD, 13));
	}

	public Dimension getPreferredSize()
	{
		return new Dimension(50, UIScaler.scale(30));
	}

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		Graphics2D g = (Graphics2D) graphics;

		int w = getWidth();

		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(font);
		g.setColor(textColor);
		g.drawString(title, 10, UIScaler.scale(18));

		g.setPaint(new GradientPaint(0, 0, lineColor, w, 0, Color.white));
		g.setStroke(new BasicStroke(UIScaler.scale(3)));
		g.drawLine(10, UIScaler.scale(26), w-10, UIScaler.scale(26));
	}
}