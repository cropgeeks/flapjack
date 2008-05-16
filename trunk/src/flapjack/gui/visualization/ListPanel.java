package flapjack.gui.visualization;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

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
		lineList.setEnabled(false);
	}

	int getPanelWidth()
		{ return isVisible() ? getWidth() : 0; }

	void setView(GTView view)
	{
		this.view = view;
		populateList();
	}

	private void populateList()
	{
		if (view == null)
			return;

		model.clear();

		for (int i = 0; i < view.getLineCount(); i++)
			model.add(i, view.getLine(i));
	}

	void computeDimensions(int size)
	{
		font = new Font("Monospaced", Font.PLAIN, size);
		lineList.setFont(font);
//		populateList();
	}

	void moveLine(int fromIndex, int toIndex)
	{
		// TODO: this is a slow operation on very large lists. Not sure why as
		// the same operation performed on a raw Vector is very fast, so
		// something in the way JList handles changes to its data must be the
		// reason why it's slow.

		Line line = (Line) model.get(fromIndex);
		model.set(fromIndex, model.get(toIndex));
		model.set(toIndex, line);
	}

	BufferedImage createSavableImage()
	{
		Dimension size = getPreferredSize();
		BufferedImage image = new BufferedImage(size.width, size.height,
			BufferedImage.TYPE_INT_RGB);

		// Paint a copy of this panel (forcing its background to white too)
		Color background = lineList.getBackground();
		lineList.setBackground(Color.white);
		Graphics2D g = image.createGraphics();
		paint(g);
		lineList.setBackground(background);

		return image;
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