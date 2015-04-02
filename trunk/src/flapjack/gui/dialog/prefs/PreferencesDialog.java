// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui.dialog.prefs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;

import scri.commons.gui.*;

public class PreferencesDialog extends JDialog implements ActionListener
{
	private static int lastTab = 0;

	private JButton bOK, bCancel, bDefault, bHelp;
	private boolean isOK;

	private JTabbedPane tabs;
	private GeneralTabNB generalTab;
	private VisualizationTabNB visualizationTab;
	private WebTabNB webTab;
	private WarningTabNB warningTab;

	public PreferencesDialog()
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.prefs.PreferencesDialog.title"),
			true
		);

		generalTab = new GeneralTabNB();
		webTab = new WebTabNB();
		visualizationTab = new VisualizationTabNB();
		warningTab = new WarningTabNB();

		tabs = new JTabbedPane();
		tabs.setBorder(BorderFactory.createEmptyBorder(2, 2, 10, 2));
		tabs.addTab(RB.getString("gui.dialog.prefs.PreferencesDialog.generalTab"),
			Icons.getIcon("GENERALTAB"), generalTab);
		tabs.addTab(RB.getString("gui.dialog.prefs.PreferencesDialog.visualizationTab"),
			Icons.getIcon("VISUALIZATIONTAB"), visualizationTab);
		tabs.addTab(RB.getString("gui.dialog.prefs.PreferencesDialog.webTab"),
			Icons.getIcon("CHECKUPDATE"), webTab);
		tabs.addTab(RB.getString("gui.dialog.prefs.PreferencesDialog.warningTab"),
			Icons.getIcon("WARNINGSTAB"), warningTab);
		tabs.setSelectedIndex(lastTab);

		add(tabs);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bOK);
		SwingUtils.addCloseHandler(this, bCancel);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bOK = SwingUtils.getButton(RB.getString("gui.text.ok"));
		bOK.addActionListener(this);
		bDefault = SwingUtils.getButton(RB.getString("gui.text.default"));
		bDefault.addActionListener(this);
		bCancel = SwingUtils.getButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);
		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
		FlapjackUtils.setHelp(bHelp, "_-_Preferences");

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		p1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 5));
		p1.add(bOK);
//		p1.add(bDefault);
		p1.add(bCancel);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOK)
		{
			for (int i = 0; i < tabs.getTabCount(); i++)
				((IPrefsTab)tabs.getComponentAt(i)).applySettings();

			lastTab = tabs.getSelectedIndex();

			isOK = true;
			setVisible(false);
		}

		else if (e.getSource() == bDefault)
		{
/*			String msg = //RB.format("io.ProjectSerializer.confirm", file);
				"This will reset Flapjack's preferences to their default settings. You can either "
				+ "apply the defaults\nto all the preferences or just to the ones currently visible.";
				String[] options = new String[] {
					"Reset all",
					"Reset visible only",
					RB.getString("gui.text.cancel")
				};

			int response = TaskDialog.show(msg, MsgBox.QST, 0, options);

			if (response == 0)
				return;
			else if (response == 1)
				((IPrefsTab)tabs.getSelectedComponent()).setDefaults();
			else
				for (int i = 0; i < tabs.getTabCount(); i++)
					((IPrefsTab)tabs.getComponentAt(i)).setDefaults();
*/
		}

		else if (e.getSource() == bCancel)
			setVisible(false);
	}

	public boolean isOK()
		{ return isOK; }
}

interface IPrefsTab
{
	void setDefaults();

	void applySettings();
}