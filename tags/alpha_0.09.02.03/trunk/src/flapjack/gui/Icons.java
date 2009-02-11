package flapjack.gui;

import java.util.*;
import javax.swing.*;

/**
 * An icon manager for the entire application. When asked to retrieve an icon
 * it first attempts to see if it's been loaded already (and stored in its
 * hash lookup table). If it is, it returns it. If not, it is loaded from disk
 * and then added to the hash table.
 */
public class Icons
{
	private static Hashtable<String, ImageIcon> hashtable =
		new Hashtable<String, ImageIcon>();

	public static ImageIcon getIcon(String name)
	{
		ImageIcon icon = hashtable.get(name);

		if (icon == null)
		{
			Icons icons = new Icons();
			Class c = icons.getClass();

			String filename = name.toLowerCase() + ".png";
			icon = new ImageIcon(c.getResource("/res/icons/" + filename));

			hashtable.put(name, icon);
		}

		return icon;
	}
}