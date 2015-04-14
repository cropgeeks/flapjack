// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import scri.commons.gui.*;

public class FlapjackUtils
{
	public static Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);

	private static final String INSTANCE_ID = SystemUtils.createGUID(8);

	private static File cacheDir, instanceDir;


	/**
	 * Registers a button to display Flapjack help on the specified topic. Will
	 * make both the button's actionListener and a keypress of F1 take Flapjack
	 * to the appropriate help page (on the web).
	 */
	public static void setHelp(final JButton button, String topic)
	{
		final String html = "http://ics.hutton.ac.uk/wiki/index.php/Flapjack_Help" + topic;

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
			Desktop desktop = Desktop.getDesktop();

			URI uri = new URI(html);
			desktop.browse(uri);
		}
		catch (Exception e) { System.out.println(e); }
	}

	public static void sendFeedback()
	{
		try
		{
			Desktop desktop = Desktop.getDesktop();
			desktop.mail(new URI("mailto:flapjack@hutton.ac.uk?subject=Flapjack%20Feedback"));
		}
		catch (Exception e) { System.out.println(e); }
	}

	/**
	 * Shows a SAVE file dialog, returning the path to the file selected as a
	 * string. Also prompts to ensure the user really does want to overwrite an
	 * existing file if one is chosen.
	 */
	public static String getSaveFilename(
		String title, File file, FileNameExtensionFilter filter)
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(title);
		fc.setCurrentDirectory(new File(Prefs.guiCurrentDir));
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(filter);

		if (file != null)
			fc.setSelectedFile(file);

		while (fc.showSaveDialog(Flapjack.winMain) == JFileChooser.APPROVE_OPTION)
		{
			file = fc.getSelectedFile();

			// Make sure it has an appropriate extension
			if (file.exists() == false)
				if (file.getName().indexOf(".") == -1)
					file = new File(file.getPath() + "." + filter.getExtensions()[0]);

			// Confirm overwrite
			if (file.exists())
			{
				String msg = RB.format("gui.FlapjackUtils.getSaveFilename.confirm", file);
				String[] options = new String[] {
					RB.getString("gui.FlapjackUtils.getSaveFilename.overwrite"),
					RB.getString("gui.FlapjackUtils.getSaveFilename.rename"),
					RB.getString("gui.text.cancel")
				};

				int response = TaskDialog.show(msg, TaskDialog.WAR, 1, options);

				// Rename...
				if (response == 1)
					continue;
				// Closed dialog or clicked cancel...
				else if (response == -1 || response == 2)
					return null;
			}

			Prefs.guiCurrentDir = fc.getCurrentDirectory().getPath();

			return file.getPath();
		}

		return null;
	}

	public static File getCacheDir()
	{
		if (instanceDir == null)
		{
			cacheDir = SystemUtils.getTempUserDirectory("jhi-flapjack");
			cacheDir.deleteOnExit();

			instanceDir = new File(cacheDir, INSTANCE_ID);
			instanceDir.deleteOnExit();
			instanceDir.mkdirs();
		}

		return instanceDir;
	}

	public static void initialiseSqlite()
	{
		try
		{
			// Initialize JDBC->SQLite driver
			Class.forName("org.sqlite.JDBC");
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}