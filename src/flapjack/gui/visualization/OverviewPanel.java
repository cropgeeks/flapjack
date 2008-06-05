package flapjack.gui.visualization;

import java.awt.*;
import javax.swing.*;

import flapjack.gui.*;

public class OverviewPanel extends JPanel
{
	public OverviewPanel()
	{
		setBackground(Prefs.visColorBackground);
		setLayout(new BorderLayout());
	}

	void removeCanvas()
	{
		removeAll();
		validate();
	}

	void addCanvas(OverviewCanvas canvas)
	{
		removeCanvas();

		add(new TitlePanel(
			RB.getString("gui.visualization.OverviewDialog.title")),
			BorderLayout.NORTH);

		add(canvas);
		validate();
	}
}