package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.io.*;

public class SaveLoadDialog extends JDialog
{
	// If we're loading, then this is what we're trying to load
	private Project project = null;
	// If we're saving, then did it work?
	private boolean wasSaved = false;

	// Runnable object that will be active while the dialog is visible
	private Runnable runnable;

	public SaveLoadDialog(boolean isSaving)
	{
		super(Flapjack.winMain, "", true);

		if (isSaving)
			setTitle(RB.getString("gui.dialog.SaveLoadDialog.saveTitle"));
		else
			setTitle(RB.getString("gui.dialog.SaveLoadDialog.loadTitle"));

		add(new NBSaveLoadPanel(isSaving));

		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				new Thread(runnable).start();
			}
		});

		pack();
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
	}

	public Project open(File file)
	{
		final File toOpen = ProjectSerializer.queryOpen(file);

		// If the file is invalid or the user cancels the dialog, just quit
		if (toOpen == null)
			return null;

		runnable = new Runnable() {
			public void run()
			{
				project = ProjectSerializer.open(toOpen);
				setVisible(false);
			}
		};

		// Make the progress dialog visible while loading takes place
		setVisible(true);

		return project;
	}

	public boolean save(final Project project, boolean saveAs)
	{
		final boolean gz = Prefs.guiSaveCompressed;

		if (ProjectSerializer.querySave(project, saveAs, gz) == false)
			return false;

		runnable = new Runnable() {
			public void run()
			{
				wasSaved = ProjectSerializer.save(project, gz);
				setVisible(false);
			}
		};

		// Make the progress dialog visible while loading takes place
		setVisible(true);

		return wasSaved;
	}
}