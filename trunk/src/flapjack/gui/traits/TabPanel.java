package flapjack.gui.traits;

import java.awt.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

public class TabPanel extends JPanel
{
	private TraitsPanel traitsPanel;
	private QTLPanel qtlPanel;

	private JTabbedPane tabs;

	public TabPanel(DataSet dataSet)
	{
		traitsPanel = new TraitsPanel(dataSet);
		qtlPanel = new QTLPanel(dataSet);

		tabs = new JTabbedPane();
		tabs.addTab(RB.getString("gui.traits.TabPanel.tab1"),
			Icons.getIcon("PHENOTYPETAB"), traitsPanel);
		tabs.addTab(RB.getString("gui.traits.TabPanel.tab2"),
			Icons.getIcon("QTLTAB"), qtlPanel);

		setLayout(new BorderLayout());
		add(new TitlePanel(RB.getString("gui.traits.TabPanel.title")),
			BorderLayout.NORTH);
		add(tabs);
	}

	public TraitsPanel getTraitsPanel()
	{
		tabs.setSelectedComponent(traitsPanel);

		return traitsPanel;
	}

	public QTLPanel getQTLPanel()
	{
		tabs.setSelectedComponent(qtlPanel);

		return qtlPanel;
	}
}