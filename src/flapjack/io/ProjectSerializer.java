package flapjack.io;

import java.io.*;
import java.util.zip.*;
import javax.swing.*;

import org.exolab.castor.mapping.*;
import org.exolab.castor.xml.*;

import flapjack.data.*;
import flapjack.gui.*;

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
			MsgBox.msg(RB.format("io.ProjectSerializer.xml", e), MsgBox.ERR);

			return false;
		}
	}

	public static boolean save(Project project, boolean saveAs)
	{
		// If the project has never been saved, then we have to prompt for file
		if (project.filename == null)
			saveAs = true;

		// Show the file selection prompt, quitting if the user goes no further
		if ((saveAs && showSaveAsDialog(project)) == false)
			return false;

		return save(project);
	}

	public static boolean save(Project project)
	{
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
			MsgBox.msg(RB.format("io.ProjectSerializer.io", project.filename,
				e.getMessage()), MsgBox.ERR);

		}
		catch (MappingException e)
		{
			e.printStackTrace();
			MsgBox.msg(RB.format("io.ProjectSerializer.xml", e.getMessage()),
				MsgBox.ERR);
		}
		catch (XMLException e)
		{
			e.printStackTrace();
			MsgBox.msg(RB.format("io.ProjectSerializer.xml", e.getMessage()),
				MsgBox.ERR);
		}

		return false;
	}

	public static Project load()
		throws Exception
	{
		initialize();

		long s = System.currentTimeMillis();


		Reader reader = new FileReader("test.xml");

		Unmarshaller unmarshaller = new Unmarshaller(mapping);

		Project p = (Project) unmarshaller.unmarshal(reader);
		reader.close();

		long e = System.currentTimeMillis();
		System.out.println("Project deserialized in " + (e-s) + "ms");

		return p;
	}

	private static boolean showSaveAsDialog(Project project)
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Save Project As");
//		fc.setAcceptAllFileFilterUsed(false);

		// If the project has never been saved it won't have a filename object
		if (project.filename != null)
			fc.setSelectedFile(project.filename);
		else
			fc.setSelectedFile(new File(Prefs.guiCurrentDir));
//		else
//			fc.setSelectedFile(new File(Prefs.gui_dir, "project "
//					+ Prefs.gui_project_count + ".topali"));

//		Filters.setFilters(fc, TOP, TOP);

		while (fc.showSaveDialog(Flapjack.winMain) == JFileChooser.APPROVE_OPTION)
		{
//			File file = Filters.getSelectedFileForSaving(fc);
			File file = fc.getSelectedFile();

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
				else if (response == 2 || response == JOptionPane.CLOSED_OPTION)
					return false;
			}

			// Otherwise it's ok to save...
			Prefs.guiCurrentDir = "" + fc.getCurrentDirectory();
//			Prefs.gui_project_count++;
			project.filename = file;

			return true;
		}

		return false;
	}
}