package flapjack.io;

import java.io.*;
import java.util.zip.*;
import javax.swing.*;

import org.exolab.castor.mapping.*;
import org.exolab.castor.xml.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.other.Filters;
import static flapjack.other.Filters.*;

import scri.commons.gui.*;

public class ProjectSerializer
{
	private static Mapping mapping;

	private static boolean initialize()
	{
		org.exolab.castor.util.LocalConfiguration.getInstance().getProperties()
			.setProperty("org.exolab.castor.parser", "org.xml.sax.helpers.XMLReaderAdapter");

//		org.exolab.castor.util.LocalConfiguration.getInstance().getProperties()
//			.setProperty("org.exolab.castor.indent", "true");

		org.exolab.castor.util.LocalConfiguration.getInstance().getProperties()
			.setProperty("org.exolab.castor.xml.serializer.factory", "org.exolab.castor.xml.XercesJDK5XMLSerializerFactory");

		try
		{
			mapping = new Mapping();
			mapping.loadMapping(
				new ProjectSerializer().getClass().getResource("/res/flapjack-castor.xml"));

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

	public static boolean save(Project project, boolean saveAs)
	{
		// If the project has never been saved, then we have to prompt for file
		if (project.filename == null)
			saveAs = true;

		// Show the file selection prompt, quitting if the user goes no further
		if (saveAs && (showSaveAsDialog(project) == false))
			return false;


		try
		{
			if (initialize() == false)
				return false;

			long s = System.currentTimeMillis();


/*			// Open an output stream to the zip...
			ZipOutputStream zOut = new ZipOutputStream(new BufferedOutputStream(
				new FileOutputStream(project.filename)));
			// And another for Castor to write to within the zip...
			BufferedWriter cOut = new BufferedWriter(new OutputStreamWriter(zOut));

			// Write a single "flapjack.xml" entry to the zip file
			zOut.putNextEntry(new ZipEntry("flapjack.xml"));
*/
			BufferedWriter cOut = new BufferedWriter(new FileWriter(project.filename));

			// And marshall it as xml
			Marshaller marshaller = new Marshaller(cOut);
			marshaller.setMapping(mapping);
			marshaller.marshal(project);

//			zOut.close();
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
				RB.format("io.ProjectSerializer.xml", e.getMessage()),
				RB.getString("gui.text.close"));
		}

		return false;
	}

	public static Project open(File file)
	{
		// Prompt for the file to open if we haven't been given one
		if (file == null)
			// And quit if the user doesn't pick one
			if ((file = showOpenDialog()) == null)
				return null;


		try
		{
			if (initialize() == false)
				return null;

			long s = System.currentTimeMillis();


			BufferedReader in = new BufferedReader(new FileReader(file));

			Unmarshaller unmarshaller = new Unmarshaller(mapping);

			Project project = (Project) unmarshaller.unmarshal(in);
			project.filename = file;
			in.close();

			long e = System.currentTimeMillis();
			System.out.println("Project deserialized in " + (e-s) + "ms");

			return project;
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
				RB.format("io.ProjectSerializer.xml", e.getMessage()),
				RB.getString("gui.text.close"));
		}

		return null;
	}

	private static File showOpenDialog()
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(RB.getString("io.ProjectSerializer.openDialog"));
		fc.setCurrentDirectory(new File(Prefs.guiCurrentDir));

		Filters.setFilters(fc, XML, XML);

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
				"Flapjack " + Prefs.guiProjectCount + ".xml"));

		Filters.setFilters(fc, XML, XML);

		while (fc.showSaveDialog(Flapjack.winMain) == JFileChooser.APPROVE_OPTION)
		{
			File file = Filters.getSelectedFileForSaving(fc);

			// Confirm overwrite
			if (file.exists())
			{
				String msg = RB.format("io.ProjectSerializer.confirm", file);
				String[] options = new String[] {
					RB.getString("io.ProjectSerializer.overwrite"),
					RB.getString("io.ProjectSerializer.rename"),
					RB.getString("gui.text.cancel")
				};

				int response = TaskDialog.show(msg, MsgBox.WAR, 0, options);

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

				int response = TaskDialog.show(msg, MsgBox.WAR, 0, options);

				if (response == 0)
					return save(project, false);
				else if (response == 1)
					return true;
				else if (response == -1 || response == 2)
					return false;
			}
		}

		return true;
	}
}