package flapjack.gui.traits;

import java.awt.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

public class TraitsTabbedPanel extends JPanel
{
	private TraitsPanel traitsPanel;

	private JTabbedPane tabs;

	public TraitsTabbedPanel(DataSet dataSet)
	{
		traitsPanel = new TraitsPanel(dataSet);

		tabs = new JTabbedPane();
		tabs.addTab(RB.getString("gui.traits.TraitsTabbedPanel.tab1"),
			Icons.getIcon("PHENOTYPETAB"), traitsPanel);
		tabs.addTab(RB.getString("gui.traits.TraitsTabbedPanel.tab2"),
			Icons.getIcon("QTLTAB"), new JPanel());

		setLayout(new BorderLayout());
		add(new TitlePanel(RB.getString("gui.traits.TraitsTabbedPanel.title")),
			BorderLayout.NORTH);
		add(tabs);
	}

	public TraitsPanel getTraitsPanel()
		{ return traitsPanel; }
}