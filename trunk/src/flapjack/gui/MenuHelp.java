package flapjack.gui;

import java.lang.management.*;
import java.text.*;
import javax.swing.*;

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
		JButton bHelp = new JButton();
		FlapjackUtils.setHelp(bHelp, "licence");
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
		String javaVer = System.getProperty("java.version");
		long freeMem = (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax()
				- ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed());

		NumberFormat nf = NumberFormat.getInstance();

		TaskDialog.info("Flapjack - Version " + Install4j.VERSION
			+ "\n\nCopyright \u00A9 2007-2008, Plant Bioinformatics Group, SCRI"
			+ "\n\nIain Milne, Micha Bayer, Paul Shaw, Linda Cardle, David Marshall"
			+ "\n\n\nJava version: " + javaVer
			+ "\nMemory available to JVM: " + nf.format((long)(freeMem/1024f/1024f)) + "MB"
			+ "\nCurrent Locale: " + java.util.Locale.getDefault()
			+ "\nFlapjack ID: " + Prefs.flapjackID,
			RB.getString("gui.text.close"));
	}
}