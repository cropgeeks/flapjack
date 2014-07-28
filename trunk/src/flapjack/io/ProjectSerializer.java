// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.zip.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import org.exolab.castor.mapping.*;
import org.exolab.castor.xml.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.io.*;

import scri.commons.gui.*;

public class ProjectSerializer
{
	public static int XMLZ = 0;
	public static int XML  = 1;

	private static Mapping mapping;

	private static boolean isDB = false;

	private static File oldFile, newFile;

	private static void initialize()
		throws Exception
	{
		mapping = new Mapping();
		mapping.loadMapping(
			new ProjectSerializer().getClass().getResource("/config/flapjack-castor.xml"));
	}

	public static boolean querySave(Project project, boolean saveAs)
	{
		FlapjackFile fjFile = project.fjFile;

		// If the project has never been saved, then we have to prompt for file
		if (fjFile == null || fjFile.isURL() || !fjFile.getFile().canWrite())
			saveAs = true;

		// Show the file selection prompt, quitting if the user goes no further
		if (saveAs && (showSaveAsDialog(project) == false))
			return false;




		// Check to see if we're writing to the same file or not. If it's the
		// latter situation, we may need to copy the old file (to the new file)
		// to ensure all the database objects not held in memory are copied too
		oldFile = newFile = null;
		if (fjFile != null)
		{
			if (fjFile.getPath().equals(project.fjFile.getPath()) == false)
			{
				oldFile = fjFile.getFile();
				newFile = project.fjFile.getFile();

				// The actual copy hapens in the save() method below
			}
		}

		return true;
	}

	public static boolean save(Project project)
	{
		FlapjackFile fjFile = project.fjFile;

		try
		{
			long s = System.currentTimeMillis();

			// Do we need to perform a low-level oldFile->newFile copy first?
			if (oldFile != null && newFile != null)
			{
				Files.copy(oldFile.toPath(), newFile.toPath(),
					StandardCopyOption.REPLACE_EXISTING,
//					StandardCopyOption.COPY_ATTRIBUTES,
					LinkOption.NOFOLLOW_LINKS);

				// If the 'old' file was read-only (which can happen if opened
				// as an attachment from Outlook for example), then we need to
				// override this otherwise the save to the new file won't work
				newFile.setWritable(true);
			}

			// Get an outputstream to the database
			ProjectSerializerDB.initConnection(fjFile, true);
			ProjectSerializerDB.initDatabase();
			OutputStream os = ProjectSerializerDB.getProjectOutputStream();

			// Dump the core "Project" (that is; the traditional xml Flapjack
			// stuff) into the database
			{
				initialize();

				BufferedWriter cOut = new BufferedWriter(new OutputStreamWriter(os));

				// And marshall it as xml
				Marshaller marshaller = new Marshaller(cOut);
				marshaller.setValidation(false);
				marshaller.setMapping(mapping);
				marshaller.marshal(project);

				cOut.close();
			}

			// Write any non-core cached-to-disk objects to the database
			long t1 = System.currentTimeMillis();
			ProjectSerializerDB.cacheToDatabase();
			long t2 = System.currentTimeMillis();
			System.out.println("DB Serialization Cache: " + (t2-t1) + "ms");

//			ProjectSerializerDB.close();
//			ProjectSerializerDB.closeAndVacuum();

			long e = System.currentTimeMillis();
			System.out.println("Project saved in " + (e-s) + "ms");

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			TaskDialog.error(
				RB.format("io.ProjectSerializer.saveError", fjFile.getName(), e),
				RB.getString("gui.text.close"));

			return false;
		}
	}

	public static FlapjackFile queryOpen(FlapjackFile file)
	{
		// Prompt for the file to open if we haven't been given one
		if (file == null)
			// And quit if the user doesn't pick one
			if ((file = showOpenDialog()) == null)
				return null;

		return file;
	}

	public static Project open(FlapjackFile file)
	{
		Project project = null;

		try
		{
			long s = System.currentTimeMillis();

			{
				XMLRoot.reset();
				initialize();

				BufferedReader in = null;

				// Compressed XML
				if (determineFormat(file) == XMLZ)
				{
					ZipInputStream zis = new ZipInputStream(getStream(file));
					zis.getNextEntry();

					in = new BufferedReader(new InputStreamReader(zis));
				}
				// Normal XML
				else
					in = new BufferedReader(new InputStreamReader(getStream(file)));

				Unmarshaller unmarshaller = new Unmarshaller(mapping);
				unmarshaller.setIgnoreExtraElements(true);
				unmarshaller.setValidation(false);

				project = (Project) unmarshaller.unmarshal(in);
				project.fjFile = file;

				in.close();
			}

			long e = System.currentTimeMillis();
			System.out.println("Project opened in " + (e-s) + "ms");

			// Validate what was loaded (it could be nonsense)
			// (especially if coming from an older project format)
			try { project.validate(); }
			catch (NullPointerException npe)
			{
//				throw new DataFormatException(
//					RB.getString("io.DataFormatException.validationError"));
			}

			return project;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			TaskDialog.error(
				RB.format("io.ProjectSerializer.openError", file.getName(), e.getMessage()),
				RB.getString("gui.text.close"));
		}

		return null;
	}

	private static FlapjackFile showOpenDialog()
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(RB.getString("io.ProjectSerializer.openDialog"));
		fc.setCurrentDirectory(new File(Prefs.guiCurrentDir));

		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("other.Filters.project"), "flapjack");
		fc.addChoosableFileFilter(filter);

		if (fc.showOpenDialog(Flapjack.winMain) == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			Prefs.guiCurrentDir = fc.getCurrentDirectory().getPath();

			return new FlapjackFile(file.getPath());
		}
		else
			return null;
	}

	private static boolean showSaveAsDialog(Project project)
	{
		FlapjackFile fjFile = project.fjFile;

		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(RB.getString("io.ProjectSerializer.saveDialog"));
		fc.setAcceptAllFileFilterUsed(false);

		// If the project has never been saved it won't have a filename object
		if (fjFile != null)
		{
			if (fjFile.isURL())
				fc.setSelectedFile(new File(Prefs.guiCurrentDir, fjFile.getName()));
			else
				fc.setSelectedFile(fjFile.getFile());
		}
		else
			fc.setSelectedFile(new File(Prefs.guiCurrentDir,
				"Flapjack " + Prefs.guiProjectCount + ".flapjack"));

		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("other.Filters.project"), "flapjack");
		fc.addChoosableFileFilter(filter);

		while (fc.showSaveDialog(Flapjack.winMain) == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();

			// Make sure it has an appropriate extension
			if (file.exists() == false)
				if (file.getName().indexOf(".") == -1)
					file = new File(file.getPath() + "." + filter.getExtensions()[0]);

			// Confirm overwrite
			if (file.exists())
			{
				String msg = RB.format("io.ProjectSerializer.confirm", file);
				String[] options = new String[] {
					RB.getString("io.ProjectSerializer.overwrite"),
					RB.getString("io.ProjectSerializer.rename"),
					RB.getString("gui.text.cancel")
				};

				int response = TaskDialog.show(msg, TaskDialog.WAR, 1, options);

				if (response == 1)
					continue;
				else if (response == -1 || response == 2)
					return false;
			}

			// Otherwise it's ok to save...
			Prefs.guiCurrentDir = fc.getCurrentDirectory().getPath();
			Prefs.guiProjectCount++;
			project.fjFile = new FlapjackFile(file.getPath());

			return true;
		}

		return false;
	}

	public static boolean okToContinue(Project project, boolean useExitMessage)
	{
		if (project != null)
		{
			if (Actions.fileSave.isEnabled())
			{
				String msg = null;

				if (useExitMessage)
					msg = RB.getString("io.ProjectSerializer.notSavedExit");
				else
					msg = RB.getString("io.ProjectSerializer.notSaved");

				String[] options = new String[] {
					RB.getString("io.ProjectSerializer.save"),
					RB.getString("io.ProjectSerializer.dontSave"),
					RB.getString("gui.text.cancel") };

				int response = TaskDialog.show(msg, TaskDialog.WAR, 0, options);

				if (response == 0)
					// TODO: This is messy - find a better way of handling
					return Flapjack.winMain.mFile.fileSave(false);
				else if (response == 1)
					return true;
				else if (response == -1 || response == 2)
					return false;
			}
		}

		return true;
	}

	private static InputStream getStream(FlapjackFile file)
		throws IOException
	{
		if (isDB)
			try
			{
				ProjectSerializerDB.initConnection(file, false);
				return ProjectSerializerDB.getProjectInputStream();
			}
			catch (SQLException e) { throw new IOException(e); }
		else
			return file.getInputStream();
	}

	// Returns true/false on a test of whether the file is compressed or not.
	// Will also throw a Flapjack-specific exception if the file doesn't appear
	// to be in any format that Flapjack should be able to read.
	private static int determineFormat(FlapjackFile file)
		throws DataFormatException, IOException
	{
		if (ProjectSerializerDB.isDatabase(file))
			isDB = true;
		else
			isDB = false;

		// See if it's zipped in a flapjack-like format?
		try
		{
			ZipInputStream zis = new ZipInputStream(getStream(file));

			ZipEntry entry = zis.getNextEntry();
			System.out.println("got entry: " + entry);

			if (entry != null && entry.getName().equals("flapjack.xml"))
				return XMLZ;
//			else
//				throw new DataFormatException(
//					RB.getString("io.DataFormatException.zipError"));
		}
		catch (ZipException e) {}
		catch (IOException e) { throw e; }

		// Failing it being a compatible zip, try a quick is-it-xml test?
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(getStream(file)));
			String str = in.readLine();
			in.close();

			if (str.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))
				return XML;
		}
		catch (IOException e) { throw e; }
		catch (Exception e)	{}

		throw new DataFormatException(
			RB.getString("io.DataFormatException.unreadable"));
	}
}