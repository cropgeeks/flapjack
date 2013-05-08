package flapjack.gui.navpanel;

import java.awt.image.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.dendogram.*;

public class DendogramNode extends BaseNode
{
	private DendogramPanel panel;

	public DendogramNode(DataSet dataSet, BufferedImage image)
	{
		super(dataSet);

		panel = new DendogramPanel(image);
	}

	public String toString()
	{
		return "Dendogram Panel";
	}

	public void setActions()
	{

	}

	public JPanel getPanel()
	{
		return panel;
	}
}
