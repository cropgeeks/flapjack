package flapjack.gui.navpanel;

import java.awt.*;
import javax.swing.*;

import flapjack.data.*;

public class DataSetNode extends BaseNode
{
	public DataSetNode(DataSet dataSet)
	{
		super(dataSet);
	}

	public String toString()
	{
		// TODO: Use a proper name for the dataset?
		return "DataSet " + dataSet.countLines() + "x" + dataSet.countMarkers();
	}

	public void setMenus()
	{
	}

	public JPanel getPanel()
	{
		JPanel p = new JPanel(new BorderLayout());
		p.setBackground(Color.white);
		p.add(new JLabel(toString(), SwingConstants.CENTER));

		return p;
	}
}