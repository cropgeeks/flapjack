package flapjack.gui.traits;

import java.awt.*;
import javax.swing.*;

import flapjack.data.*;

public class TraitsPanel extends JPanel
{
	public TraitsPanel(DataSet dataSet)
	{
		setLayout(new BorderLayout());
		add(new JLabel("TraitsPanel", JLabel.CENTER));
	}
}