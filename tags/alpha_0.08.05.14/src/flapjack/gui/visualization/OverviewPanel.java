package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.data.*;
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