package flapjack.gui;

import java.text.*;
import java.util.*;

/**
 * ResourceBundle utility class that holds all the resource strings for the
 * application.
 */
public class RB
{
//	public static Locale locale = null;

	private static ResourceBundle bundle = null;

	public static void initialize()
	{
		if (Prefs.localeText.equals("en_GB"))
			Locale.setDefault(Locale.UK);
		else if (Prefs.localeText.equals("en_US"))
			Locale.setDefault(Locale.US);
		else if (Prefs.localeText.equals("de"))
			Locale.setDefault(Locale.GERMAN);
//		else
//			locale = Locale.getDefault();

		bundle = ResourceBundle.getBundle("res.text.flapjack");
	}

	/**
	 * Returns the string associated with the given key. If the key isn't found
	 * in the resource bundle, the method simply returns the key.
	 * @param key the key to search for
	 * @return the string associated with the given key
	 */
	public static String getString(String key)
	{
		try
		{
			return bundle.getString(key);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return key;
		}
	}

	public static int getIndex(String key)
		{ return Integer.parseInt(getString(key + "Index")); }

	/**
	 * Returns the string associated with the given key, formatted with the
	 * given message parameters. If the key isn't found in the resource bundle,
	 * the method simply returns the key.
	 * @param key the key to search for
	 * @param args the message's parameters
	 * @return the string associated with the given key
	 */
	public static String format(String key, Object... args)
	{
		try
		{
			String str = bundle.getString(key);

			MessageFormat msg = new MessageFormat(str);

			return msg.format(args);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return key;
		}
	}
}
