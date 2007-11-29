package flapjack.gui;

import java.text.*;
import java.util.*;

public class RB
{
	static Locale locale = null;

	private static ResourceBundle bundle = null;

	public static void init()
	{
		if (locale == null)
			locale = Locale.getDefault();

//		bundle = ResourceBundle.getBundle("res.text.flapjack", Prefs.locale);
		bundle = ResourceBundle.getBundle("res.text.flapjack", locale);
	}

	public static String getString(String key)
		{ return bundle.getString(key); }

	public static String format(String key, Object... args)
	{
		String str = bundle.getString(key);

//		MessageFormat msg = new MessageFormat(str, Prefs.locale);
		MessageFormat msg = new MessageFormat(str, locale);

		return msg.format(args);
	}
}
