package flapjack.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Flapjack
{
	public static WinMain winMain;

	public static void main(String[] args)
		throws Exception
	{
		new Flapjack();
	}

	Flapjack()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {}

		winMain = new WinMain();

		winMain.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				shutdown();
			}

			public void windowOpened(WindowEvent e)
			{

			}
		});

		winMain.setVisible(true);
	}

	private void shutdown()
	{
		System.exit(0);
	}
}