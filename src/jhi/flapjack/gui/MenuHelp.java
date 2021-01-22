// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui;

import java.io.*;
import java.text.*;
import javax.swing.*;

import jhi.flapjack.gui.dialog.*;
import jhi.flapjack.gui.dialog.prefs.*;
import jhi.flapjack.gui.visualization.*;

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
		FlapjackUtils.setHelp(bHelp, "");
		bHelp.doClick();
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