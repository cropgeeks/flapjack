// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.navpanel;

import java.awt.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

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
		Actions.viewNewView.setEnabled(true);

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