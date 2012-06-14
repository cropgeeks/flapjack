// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui;

import java.awt.*;
import javax.swing.*;

import scri.commons.gui.*;

public class TitlePanel2 extends JPanel
{
	private static final Color c1 = getColor();
	private static final int h = 22;

	public Dimension getPreferredSize()
	{
		return new Dimension(50, h);
	}

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		Graphics2D g = (Graphics2D) graphics;

		g.setPaint(new GradientPaint(0, 0, c1, 0, h, Color.white));
		g.fillRect(0, 0, getWidth(), h);

//		g.drawImage(Icons.SWIRL.getImage(), 0, 0, null);
	}

	private static Color getColor()
	{
		String style = (String)
			Toolkit.getDefaultToolkit().getDesktopProperty("win.xpstyle.colorName");

		// Non-windows systems (let's assume grey for now)
		if (SystemUtils.isWindows() == false)
			return new Color(170, 170, 170);

		// Windows "classic"
		else if (style == null)
			return new Color(212, 208, 200);
		// Blue
		else if (style.equals("NormalColor"))
			return new Color(160, 191, 255);
		// Silver
		else if (style.equals("Metallic"))
			return new Color(216, 216, 230);
		// Olive
		else if (style.equals("HomeStead"))
			return new Color(218, 218, 170);

		return new Color(212, 208, 200);
	}
}