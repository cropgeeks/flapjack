package flapjack.gui;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;

import scri.commons.gui.*;

public class FlapjackUtils
{
	// Checks to see if the IP address of the current user is an SCRI one
	static boolean isSCRIUser()
	{
		try
		{
			// Need to check over all network interfaces (LAN/wireless/etc) to
			// try and find a match...
			Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();

			while (e != null & e.hasMoreElements())
			{
				// And each interface can have multiple IPs...
				Enumeration<InetAddress> e2 = e.nextElement().getInetAddresses();
				while (e2.hasMoreElements())
				{
					String addr = e2.nextElement().getHostAddress();

					if (addr.startsWith("143.234.96.")  || addr.startsWith("143.234.97.") ||
						addr.startsWith("143.234.98.")  || addr.startsWith("143.234.99.") ||
						addr.startsWith("143.234.100.") || addr.startsWith("143.234.101."))
						return true;
				}
			}
		}
		catch (Exception e) {}

		return false;
	}

	/**
	 * Registers a button to display Flapjack help on the specified topic. Will
	 * make both the button's actionListener and a keypress of F1 take Flapjack
	 * to the appropriate help page (on the web).
	 */
	public static void setHelp(final JButton button, String topic)
	{
		final String html = "http://bioinf.scri.ac.uk/flapjack/help/" + topic + ".shtml";

		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				visitURL(html);
			}
		});

		// TODO: is there a better way of doing this that doesn't rely on having
		// an actionListener AND an AbstractAction both doing the same thing
		AbstractAction helpAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				visitURL(html);
			}
		};

		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
		button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, "help");
		button.getActionMap().put("help", helpAction);
	}

	public static void visitURL(String html)
	{
		try
		{
			if (SystemUtils.isMacOS() == false)
				visitURL6(html);
			else
				visitURL5(html);
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}

	// Java6 method for visiting a URL
	private static void visitURL6(String html)
		throws Exception
	{
		Desktop desktop = Desktop.getDesktop();

		URI uri = new URI(html);
		desktop.browse(uri);
	}

	// Java5 (OS X only) method for visiting a URL
	private static void visitURL5(String html)
		throws Exception
	{
		// See: http://www.centerkey.com/java/browser/

		Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
		Method openURL = fileMgr.getDeclaredMethod("openURL",
			new Class[] {String.class});

		openURL.invoke(null, new Object[] {html});
	}
}