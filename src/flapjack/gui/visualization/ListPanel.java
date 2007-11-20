package flapjack.gui.visualization;

import java.awt.*;
import javax.swing.*;

import flapjack.data.*;

class ListPanel extends JPanel
{
	private DataSet dataSet;

	private JList lineList;
	private DefaultListModel model;
	private static Font font;

	ListPanel(DataSet dataSet)
	{
		this.dataSet = dataSet;

		createControls();
		computeDimensions();

		setLayout(new BorderLayout());
		add(lineList);
	}

	private void createControls()
	{
		model = new DefaultListModel();
		lineList = new JList(model);
		lineList.setCellRenderer(new ListRenderer());

		for (int i = 0; i < dataSet.countLines(); i++)
			model.add(i, (i+1) + " " + dataSet.getLineByIndex(i));
	}

	void computeDimensions()
	{
		font = new Font("Monospaced", Font.PLAIN, 11);
	}

	static class ListRenderer extends JLabel implements ListCellRenderer
	{
		public ListRenderer()
		{
			setOpaque(true);
		}

		// Set the attributes of the class and return a reference
		public Component getListCellRendererComponent(JList list, Object obj,
				int i, boolean iss, boolean chf)
		{
			setFont(font);
			setText(obj.toString());

			// Set background/foreground colours
			if (iss)
			{
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else
			{
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			return this;
		}
	}
}