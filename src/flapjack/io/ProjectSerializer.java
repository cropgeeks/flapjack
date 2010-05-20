// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.io;

import java.io.*;
import java.util.zip.*;
import javax.swing.*;

import org.exolab.castor.mapping.*;
import org.exolab.castor.xml.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.other.*;

import scri.commons.gui.*;

public class ProjectSerializer
{
	private static Mapping mapping;

	private static boolean initialize()
	{
		try
		{
			mapping = new Mapping();
			mapping.loadMapping(
				new ProjectSerializer().getClass().getResource("/config/flapjack-castor.xml"));

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			TaskDialog.error(
				RB.format("io.ProjectSerializer.xml", e),
				RB.format("gui.text.close"));

			return false;
		}
	}

	public static boolean querySave(Project project, boolean saveAs, boolean compress)
	{
		// If the project has never been saved, then we have to prompt for file
		if (project.filename == null)
			saveAs = true;

		// Show the file selection prompt, quitting if the user goes no further
		if (saveAs && (showSaveAsDialog(project) == false))
			return false;

		return true;
	}

	public static boolean save(Project project, boolean compress)
	{
		try
		{
			if (initialize() == false)
				return false;

			long s = System.currentTimeMillis();


			BufferedWriter cOut = null;

			if (compress)
			{
				// Open an output stream to the zip...
				ZipOutputStream zOut = new ZipOutputStream(new BufferedOutputStream(
					new FileOutputStream(project.filename)));
				// And another for Castor to write to within the zip...
				cOut = new BufferedWriter(new OutputStreamWriter(zOut));

				// Write a single "flapjack.xml" entry to the zip file
				zOut.putNextEntry(new ZipEntry("flapjack.xml"));
			}
			else
				cOut = new BufferedWriter(new FileWriter(project.filename));

			// And marshall it as xml
			Marshaller marshaller = new Marshaller(cOut);
			marshaller.setMapping(mapping);
			marshaller.marshal(project);

			cOut.close();


			long e = System.currentTimeMillis();
			System.out.println("Project serialized in " + (e-s) + "ms");

			return true;
		}
		catch (IOException e)
		{
			TaskDialog.error(
				RB.format("io.ProjectSerializer.ioException", project.filename, e.getMessage()),
				RB.getString("gui.text.close"));
		}
		catch (MappingException e)
		{
			e.printStackTrace();
			TaskDialog.error(
				RB.format("io.ProjectSerializer.xml", e.getMessage()),
				RB.getString("gui.text.close"));
		}
		catch (XMLException e)
		{
			e.printStackTrace();
			TaskDialog.error(
				RB.format("io.ProjectSerializer.xmlWriteException", e.getMessage()),
				RB.getString("gui.text.close"));
		}

		return false;
	}

	public static File queryOpen(File file)
	{
		// Prompt for the file to open if we haven't been given one
		if (file == null)
			// And quit if the user doesn't pick one
			if ((file = showOpenDialog()) == null)
				return null;

		return file;
	}

	public static Project open(File file)
	{
		try
		{
			if (initialize() == false)
				return null;

			long s = System.currentTimeMillis();

			Project project = null;
			BufferedReader in = null;

			if (isFileCompressed(file))
			{
				ZipFile zipFile = new ZipFile(file);
				InputStream zin = zipFile.getInputStream(
					new ZipEntry("flapjack.xml"));
				in = new BufferedReader(new InputStreamReader(zin));
			}
			else
				in = new BufferedReader(new FileReader(file));

			Unmarshaller unmarshaller = new Unmarshaller(mapping);
			unmarshaller.setIgnoreExtraElements(true);

			XMLRoot.reset();
			project = (Project) unmarshaller.unmarshal(in);
			project.filename = file;

			in.close();

			// Validate what was loaded (it could be nonsense)
			// (especially if coming from an older project format)
			try { project.validate(); }
			catch (NullPointerException e)
			{
				throw new DataFormatException(
					RB.getString("io.DataFormatException.validationError"));
			}


			long e = System.currentTimeMillis();
			System.out.println("Project deserialized in " + (e-s) + "ms");

			return project;
		}
		catch (DataFormatException e)
		{
			TaskDialog.error(
				RB.format("io.ProjectSerializer.flapjackException", file, e.getMessage()),
				RB.getString("gui.text.close"));
		}
		catch (IOException e)
		{
			TaskDialog.error(
				RB.format("io.ProjectSerializer.ioException", file, e.getMessage()),
				RB.getString("gui.text.close"));
		}
		catch (MappingException e)
		{
			e.printStackTrace();
			TaskDialog.error(
				RB.format("io.ProjectSerializer.xml", e.getMessage()),
				RB.getString("gui.text.close"));
		}
		catch (XMLException e)
		{
			e.printStackTrace();
			TaskDialog.error(
				RB.format("io.ProjectSerializer.xmlReadException", e.getMessage()),
				RB.getString("gui.text.close"));
		}

		return null;
	}

	private static File showOpenDialog()
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

			return file;
		}
		else
			return null;
	}

	private static boolean showSaveAsDialog(Project project)
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(RB.getString("io.ProjectSerializer.saveDialog"));
		fc.setAcceptAllFileFilterUsed(false);

		// If the project has never been saved it won't have a filename object
		if (project.filename != null)
			fc.setSelectedFile(project.filename);
		else
			fc.setSelectedFile(new File(Prefs.guiCurrentDir,
				"Flapjack " + Prefs.guiProjectCount + ".flapjack"));

		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("other.Filters.project"), "flapjack");
		fc.addChoosableFileFilter(filter);

		while (fc.showSaveDialog(Flapjack.winMain) == JFileChooser.APPROVE_OPTION)
		{
			File file = FileNameExtensionFilter.getSelectedFileForSaving(fc);

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
			project.filename = file;

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

	// Returns true/false on a test of whether the file is compressed or not.
	// Will also throw a Flapjack-specific exception if the file doesn't appear
	// to be in any format that Flapjack should be able to read.
	private static boolean isFileCompressed(File file)
		throws DataFormatException, IOException
	{
		// Is the file zipped in a flapjack-like format?
		try
		{
			ZipFile zipFile = new ZipFile(file);
			if (zipFile.getEntry("flapjack.xml") != null)
				return true;
			else
				throw new DataFormatException(
					RB.getString("io.DataFormatException.zipError"));
		}
		catch (ZipException e) {}
		catch (IOException e) { throw e; }

		// Failing it being a compatible zip, try a quick is-it-xml test?
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str = in.readLine();
			in.close();

			if (str.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))
				return false;
		}
		catch (IOException e) { throw e; }
		catch (Exception e)	{}

		throw new DataFormatException(
			RB.getString("io.DataFormatException.xmlError"));
	}
}