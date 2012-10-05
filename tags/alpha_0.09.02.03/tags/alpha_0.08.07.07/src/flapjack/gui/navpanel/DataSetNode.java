package flapjack.gui.navpanel;

import java.awt.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

public class DataSetNode extends BaseNode
{
	public DataSetNode(DataSet dataSet)
	{
		super(dataSet);
	}

	public String toString()
	{
		// TODO: Use a proper name for the dataset?
		return dataSet.getName();
	}

	public void setActions()
	{
		Actions.vizNewView.setEnabled(true);

		Actions.dataRenameDataSet.setEnabled(true);
		Actions.dataDeleteDataSet.setEnabled(true);
	}

	public JPanel getPanel()
	{
		JPanel p = new JPanel(new BorderLayout());
		p.setBackground(Color.white);
		p.add(new JLabel(toString(), SwingConstants.CENTER));

		return p;
	}
}