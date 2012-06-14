// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui;

import java.io.*;
import java.text.*;
import javax.swing.*;

import flapjack.gui.dialog.*;
import flapjack.gui.dialog.prefs.*;
import flapjack.gui.visualization.*;

import scri.commons.gui.*;

class MenuHelp
{
	private GenotypePanel gPanel;

	void setComponents(GenotypePanel gPanel)
	{
		this.gPanel = gPanel;
	}

	void helpContents()
	{
		JButton bHelp = new JButton();
		FlapjackUtils.setHelp(bHelp, "index");
		bHelp.doClick();
	}

	void helpLicence()
	{
		// This will fail if not running in an Install4j environment
		try
		{
			File licence = Install4j.getLicenceFile();
			FlapjackUtils.visitURL(licence.toURI().toString());
		}
		catch (Exception e) {}
	}

	void helpPrefs()
	{
		if (new PreferencesDialog().isOK())
			gPanel.refreshView();
	}

	void helpUpdate()
	{
		Install4j.checkForUpdate(false);
	}

	void helpAbout()
	{
		new AboutDialog();
	}
}