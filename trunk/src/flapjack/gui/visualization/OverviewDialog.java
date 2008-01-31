package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

public class OverviewDialog extends JDialog
{
	public OverviewDialog(WinMain winMain, OverviewCanvas canvas)
	{
		super(winMain, RB.getString("gui.visualization.OverviewDialog.title"), false);

		setLayout(new BorderLayout());
		add(canvas);

		setSize(Prefs.guiOverviewWidth, Prefs.guiOverviewHeight);

		// Work out the current screen's width and height
		int scrnW = SwingUtils.getVirtualScreenDimension().width;
		int scrnH = SwingUtils.getVirtualScreenDimension().height;

		// Determine where on screen (TODO: on which monitor?) to display
		if (Prefs.guiOverviewX > (scrnW-50) || Prefs.guiOverviewY > (scrnH-50))
			setLocationRelativeTo(Flapjack.winMain);
		else
			setLocation(Prefs.guiOverviewX, Prefs.guiOverviewY);

		addListeners();
	}

	private void addListeners()
	{
		addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				Prefs.guiOverviewWidth = getSize().width;
				Prefs.guiOverviewHeight = getSize().height;
				Prefs.guiOverviewX = getLocation().x;
				Prefs.guiOverviewY = getLocation().y;
			}

			public void componentMoved(ComponentEvent e)
			{
				Prefs.guiOverviewX = getLocation().x;
				Prefs.guiOverviewY = getLocation().y;
			}
		});
	}

/*	void removeCanvas()
	{
		removeAll();
		validate();
	}

	void addCanvas(OverviewCanvas canvas)
	{
		removeCanvas();

		add(canvas);
		validate();
	}
*/
}