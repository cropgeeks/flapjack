package flapjack.gui;

import java.text.*;
import java.util.*;
import javax.swing.*;

/**
 * ResourceBundle utility class that holds all the resource strings for the
 * application. Also handles special cases where we use '&' to mark mnemonics.
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
			Locale.setDefault(Locale.GERMANY);

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
			return bundle.getString(key).replaceAll("&", "");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return key;
		}
	}

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
			String str = bundle.getString(key).replaceAll("&", "");
			str = str.replaceAll("'", "''");

			MessageFormat msg = new MessageFormat(str);

			return msg.format(args);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return key;
		}
	}

	/* Sets the mnemonic (and optionally its index) for a label. */
	public static void setMnemonic(JLabel label, String key)
	{
		try
		{
			String str = bundle.getString(key);

			int i = str.indexOf("&");
			char m = str.replaceAll("&", "").charAt(i);

			label.setDisplayedMnemonic(m);
			label.setDisplayedMnemonicIndex(i);
		}
		catch (IndexOutOfBoundsException e) {}
		catch (Exception e) { System.out.println(e.getMessage() + " " + key); }
	}

	/* Sets the mnemonic (and optionally its index) for a button. */
	public static void setMnemonic(AbstractButton button, String key)
	{
		try
		{
			String str = bundle.getString(key);

			int i = str.indexOf("&");
			char m = str.replaceAll("&", "").charAt(i);

			button.setMnemonic(m);
			button.setDisplayedMnemonicIndex(i);
		}
		catch (IndexOutOfBoundsException e) {}
		catch (Exception e) { System.out.println(e.getMessage() + " " + key); }
	}

	public static void setText(JLabel label, String key)
	{
		label.setText(getString(key));
		setMnemonic(label, key);
	}

	public static void setText(AbstractButton button, String key)
	{
		button.setText(getString(key));
		setMnemonic(button, key);
	}
}
