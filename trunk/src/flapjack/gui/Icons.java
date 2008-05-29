package flapjack.gui;

import java.lang.reflect.*;
import javax.swing.*;

public class Icons
{
	public static ImageIcon NOICON;

	public static ImageIcon BLUEBLOB;
	public static ImageIcon CHECKUPDATE;
	public static ImageIcon CHROMOSOME;
	public static ImageIcon DELETE;
	public static ImageIcon EMPTY;
	public static ImageIcon FIND;
	public static ImageIcon FILENEW;
	public static ImageIcon FILEOPEN;
	public static ImageIcon FILESAVE;
	public static ImageIcon FILESAVEAS;
	public static ImageIcon FILEIMPORT;
	public static ImageIcon FLAPJACK;
	public static ImageIcon FOLDER;
	public static ImageIcon FOLDEROPEN;
	public static ImageIcon GREYBLOB;
	public static ImageIcon GERMINATE;
	public static ImageIcon INVERT;
	public static ImageIcon LINEMODE;
	public static ImageIcon MARKERMODE;
	public static ImageIcon NAVIGATIONMODE;
	public static ImageIcon PREFERENCES;
	public static ImageIcon REDBLOB;
	public static ImageIcon REDO;
	public static ImageIcon RENAME;
	public static ImageIcon SELECTALL;
	public static ImageIcon SELECTNONE;
	public static ImageIcon UNDO;
	public static ImageIcon VISUALIZATION;
	public static ImageIcon WEB;

	public static ImageIcon WINERROR, WININFORMATION, WINQUESTION, WINWARNING;

	private Icons()
	{
	}

	public static void initialize()
	{
		Icons icons = new Icons();
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