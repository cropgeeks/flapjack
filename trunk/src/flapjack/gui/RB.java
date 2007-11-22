package flapjack.gui;

import java.text.*;
import java.util.*;

public class RB
{
	private static ResourceBundle bundle = null;

	static
	{
//		bundle = ResourceBundle.getBundle("res.text.flapjack", Prefs.locale);
		bundle = ResourceBundle.getBundle("res.text.flapjack", Locale.getDefault());
	}

	public static String getString(String key)
		{ return bundle.getString(key); }

	public static String format(String key, Object... args)
	{
		String str = bundle.getString(key);

//		MessageFormat msg = new MessageFormat(str, Prefs.locale);
		MessageFormat msg = new MessageFormat(str, Locale.getDefault());


		return msg.format(args);
	}
}
