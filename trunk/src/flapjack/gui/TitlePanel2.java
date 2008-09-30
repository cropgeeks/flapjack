package flapjack.gui;

import java.awt.*;
import javax.swing.*;

public class TitlePanel2 extends JPanel
{
//	private static final Color c1 = SystemColor.;
	private static final Color c1 = new Color(169, 198, 246);
//	private static final Color c1 = new Color(170, 170, 170);

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
}