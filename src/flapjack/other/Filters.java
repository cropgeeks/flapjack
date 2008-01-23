package flapjack.other;

import java.io.*;
import java.util.*;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import flapjack.gui.RB;

public class Filters extends FileFilter
{
	public static final int PNG = 1;
	public static final int XML = 2;
	public static final int TXT = 3;

	private Hashtable<String, Object> filters = new Hashtable<String, Object>();

	private String description = null;

	private String fullDescription = null;

	private boolean useExtensionsInDescription = true;

	private String extStr;

	private int extInt;

	// Creates a new filter that filters on the given type
	public static Filters getFileFilter(int type)
	{
		Filters filter = new Filters();

		switch (type)
		{
			case PNG:
				filter.addExtension("png", PNG);
				filter.setDescription(RB.getString("other.Filters.png"));
				break;

			case XML:
				filter.addExtension("xml", XML);
				filter.setDescription(RB.getString("other.Filters.xml"));
				break;

			case TXT:
				filter.addExtension("txt", TXT);
				filter.setDescription(RB.getString("other.Filters.txt"));
				break;
		}

		return filter;
	}

	// Modifies the JFileChooser so that it contains file filters for the given
	// array of file types. Also sets the chooser so that the "selected" filter
	// is picked as the default
	public static void setFilters(JFileChooser fc, int selected,
			Object... index)
	{
		Filters[] filters = new Filters[index.length];

		FileFilter f = fc.getFileFilter();

		int toSelect = -1;
		for (int i = 0; i < index.length; i++)
		{
			int filterIndex = (Integer) index[i];

			filters[i] = Filters.getFileFilter(filterIndex);
			fc.addChoosableFileFilter(filters[i]);

			if (filterIndex == selected)
				toSelect = i;
		}

		if (toSelect >= 0)
			fc.setFileFilter(filters[toSelect]);
		else
			fc.setFileFilter(f);
	}

	// Returns the (last) extension set on this Filters object. Used to append
	// an extension onto filenames that were named without one.
	public String getExtStr()
	{
		return extStr;
	}

	public int getExtInt()
	{
		return extInt;
	}

	// Pulls back the selected file from the file chooser, then renames it if it
	// doesn't have a suitable extension. Then returns the file.
	public static File getSelectedFileForSaving(JFileChooser fc)
	{
		File file = fc.getSelectedFile();
		Filters filter = (Filters) fc.getFileFilter();

		// Make sure it has an appropriate extension
		if (file.exists() == false)
			if (file.getName().indexOf(".") == -1)
				file = new File(file.getPath() + "." + filter.getExtStr());

		return file;
	}

	// ////////////////////////////

	/**
	 * Return true if this file should be shown in the directory pane, false if
	 * it shouldn't.
	 *
	 * Files that begin with "." are ignored.
	 *
	 * @see #getExtension
	 * @see FileFilter#accepts
	 */
	@Override
	public boolean accept(File f)
	{
		if (f != null)
		{
			if (f.isDirectory())
			{
				return true;
			}
			String extension = getExtension(f);
			if (extension != null && filters.get(getExtension(f)) != null)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Return the extension portion of the file's name .
	 *
	 * @see #getExtension
	 * @see FileFilter#accept
	 */
	public String getExtension(File f)
	{
		if (f != null)
		{
			String filename = f.getName();
			int i = filename.lastIndexOf('.');
			if (i > 0 && i < filename.length() - 1)
			{
				return filename.substring(i + 1).toLowerCase();
			}
			;
		}
		return null;
	}

	/**
	 * Adds a filetype "dot" extension to filter against.
	 *
	 * For example: the following code will create a filter that filters out all
	 * files except those that end in ".jpg" and ".tif":
	 *
	 * ExampleFileFilter filter = new ExampleFileFilter();
	 * filter.addExtension("jpg"); filter.addExtension("tif");
	 *
	 * Note that the "." before the extension is not needed and will be ignored.
	 */
	public void addExtension(String extension, int extInteger)
	{
		if (filters == null)
		{
			filters = new Hashtable<String, Object>(5);
		}
		filters.put(extension.toLowerCase(), this);
		fullDescription = null;

		extStr = extension.toLowerCase();
		extInt = extInteger;
	}

	/**
	 * Returns the human readable description of this filter. For example: "JPEG
	 * and GIF Image Files (*.jpg, *.gif)"
	 *
	 * @see setDescription
	 * @see setExtensionListInDescription
	 * @see isExtensionListInDescription
	 * @see FileFilter#getDescription
	 */
	@Override
	public String getDescription()
	{
		if (fullDescription == null)
		{
			if (description == null || isExtensionListInDescription())
			{
				fullDescription = description == null ? "(" : description
						+ " (";
				// build the description from the extension list
				Enumeration extensions = filters.keys();
				if (extensions != null)
				{
					fullDescription += "." + (String) extensions.nextElement();
					while (extensions.hasMoreElements())
					{
						fullDescription += ", "
								+ (String) extensions.nextElement();
					}
				}
				fullDescription += ")";
			} else
			{
				fullDescription = description;
			}
		}
		return fullDescription;
	}

	/**
	 * Sets the human readable description of this filter. For example:
	 * filter.setDescription("Gif and JPG Images");
	 *
	 * @see setDescription
	 * @see setExtensionListInDescription
	 * @see isExtensionListInDescription
	 */
	public void setDescription(String description)
	{
		this.description = description;
		fullDescription = null;
	}

	/**
	 * Determines whether the extension list (.jpg, .gif, etc) should show up in
	 * the human readable description.
	 *
	 * Only relevent if a description was provided in the constructor or using
	 * setDescription();
	 *
	 * @see getDescription
	 * @see setDescription
	 * @see isExtensionListInDescription
	 */
	public void setExtensionListInDescription(boolean b)
	{
		useExtensionsInDescription = b;
		fullDescription = null;
	}

	/**
	 * Returns whether the extension list (.jpg, .gif, etc) should show up in
	 * the human readable description.
	 *
	 * Only relevent if a description was provided in the constructor or using
	 * setDescription();
	 *
	 * @see getDescription
	 * @see setDescription
	 * @see setExtensionListInDescription
	 */
	public boolean isExtensionListInDescription()
	{
		return useExtensionsInDescription;
	}
}