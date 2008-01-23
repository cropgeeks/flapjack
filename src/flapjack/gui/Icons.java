package flapjack.gui;

import java.lang.reflect.*;
import javax.swing.*;

public class Icons
{
	public static ImageIcon NOICON;

	public static ImageIcon FILEIMPORT;
	public static ImageIcon FILESAVE;
	public static ImageIcon FILESAVEAS;

	public static ImageIcon WINERROR, WININFORMATION, WINQUESTION, WINWARNING;

	private Icons()
	{
	}

	public static void initialize()
	{
		Icons icons = new Icons();
//		Class<? extends Icons> c = icons.getClass();
		Class c = icons.getClass();

		try
		{
			long s = System.currentTimeMillis();
			Field[] fields = c.getFields();
			for (Field field : fields)
			{
				if (field.getType() == ImageIcon.class)
				{
					String name = field.getName().toLowerCase() + ".png";

					ImageIcon icon = new ImageIcon(c.getResource("/res/icons/" + name));

					field.set(null, icon);
				}
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException("Unable to load one or more required icons.", e);
		}
	}
}