// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

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
	public static Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);

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
			if (SystemUtils.jreVersion() >= 1.6)
				visitURL6(html);
			else
				visitURL5(html);
		}
		catch (Exception e) { System.out.println(e); }
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

	public static void sendFeedback()
	{
		try
		{
			if (SystemUtils.jreVersion() >= 1.6)
				openMailClient();
		}
		catch (Exception e) { System.out.println(e); }
	}

	private static void openMailClient()
		throws Exception
	{
		Desktop desktop = Desktop.getDesktop();
		desktop.mail(new URI("mailto:flapjack@scri.ac.uk?subject=Flapjack%20Feedback"));
	}

	public static JPanel getButtonPanel()
	{
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

		p1.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(219, 219, 219)),
			BorderFactory.createEmptyBorder(10, 0, 10, 5)));

		return p1;
	}
}