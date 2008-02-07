package flapjack.gui.visualization;

import java.awt.*;
import javax.swing.*;

import flapjack.data.*;

class ListPanel extends JPanel
{
	private GTView view;

	private JList lineList;
	private DefaultListModel model;
	private static Font font;

	ListPanel()
	{
		createControls();

		setLayout(new BorderLayout());
		add(lineList);
	}

	private void createControls()
	{
		model = new DefaultListModel();
		lineList = new JList(model);
		lineList.setCellRenderer(new ListRenderer());
	}

	void setView(GTView view)
	{
		this.view = view;
		populateList();
	}

	private void populateList()
	{
//		if (view == null)
//			return;

		model.clear();

		for (int i = 0; i < view.getLineCount(); i++)
			model.add(i, view.getLine(i));
	}

	void computeDimensions(int size)
	{
		font = new Font("Monospaced", Font.PLAIN, size);

		populateList();
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