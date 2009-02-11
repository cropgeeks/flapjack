package flapjack.gui.dialog.prefs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import flapjack.gui.*;

import scri.commons.gui.*;

public class PreferencesDialog extends JDialog implements ActionListener
{
	private static int lastTab = 0;

	private JButton bOK, bCancel, bDefault;
	private boolean isOK;

	private JTabbedPane tabs;
	private NBGeneralPanel generalPanel;
	private NBVisualizationPanel visualizationPanel;
	private NBWarningPanel warningPanel;

	public PreferencesDialog()
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.prefs.PreferencesDialog.title"),
			true
		);

		generalPanel = new NBGeneralPanel();
		visualizationPanel = new NBVisualizationPanel();
		warningPanel = new NBWarningPanel();

		tabs = new JTabbedPane();
		tabs.setBorder(BorderFactory.createEmptyBorder(2, 2, 10, 2));
		tabs.addTab(RB.getString("gui.dialog.prefs.PreferencesDialog.generalTab"), generalPanel);
		tabs.addTab(RB.getString("gui.dialog.prefs.PreferencesDialog.visualizationTab"), visualizationPanel);
		tabs.addTab(RB.getString("gui.dialog.prefs.PreferencesDialog.warningTab"), warningPanel);
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

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		p1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 5));
		p1.add(bOK);
//		p1.add(bDefault);
		p1.add(bCancel);

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