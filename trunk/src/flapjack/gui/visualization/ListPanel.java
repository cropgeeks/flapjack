// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.text.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

class ListPanel extends JPanel
{
	private GTViewSet viewSet;
	private GTView view;

	private JList<LineInfo> lineList;
	private DefaultListModel<LineInfo> model;
	private static Font font;

	ListPanel()
	{
		createControls();

		setLayout(new BorderLayout());
		add(lineList);
	}

	private void createControls()
	{
		model = new DefaultListModel<LineInfo>();
		lineList = new JList<LineInfo>(model);
		lineList.setCellRenderer(new ListRenderer());
		lineList.setEnabled(false);

		lineList.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				handlePopup(e);
			}
			public void mouseReleased(MouseEvent e) {
				handlePopup(e);
			}
		});
	}

	int getPanelWidth()
		{ return Prefs.visShowLinePanel ? getWidth() : 0; }

	void setView(GTView view)
	{
		this.view = view;
		viewSet = view.getViewSet();

		populateList();
	}

	private void populateList()
	{
		if (view == null)
			return;

		model.clear();

		for (int i = 0; i < view.getLineCount(); i++)
			model.add(i, view.getLineInfo(i));
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

		if (fromIndex >= model.size() || toIndex >= model.size())
			return;

		LineInfo li = (LineInfo) model.get(fromIndex);
		model.set(fromIndex, model.get(toIndex));
		model.set(toIndex, li);
	}

	BufferedImage createSavableImage(boolean full, int yPos)
	{
		Dimension size = getPreferredSize();
		BufferedImage image;
		image = new BufferedImage(size.width, size.height,
				BufferedImage.TYPE_INT_RGB);

		// Paint a copy of this panel (forcing its background to white too)
		Color background = lineList.getBackground();
		lineList.setBackground(Color.white);
		Graphics2D g = image.createGraphics();
		lineList.setBackground(background);

		if(!full)
		{
			g.translate(0, -yPos);
		}

		paint(g);

		return image;
	}

	private void handlePopup(MouseEvent e)
	{
		if (e.isPopupTrigger() == false)
			return;

		JPopupMenu menu = new JPopupMenu();

		final JCheckBoxMenuItem mShowScores = new JCheckBoxMenuItem();
		RB.setText(mShowScores, "gui.visualization.ListPanel.showScores");
		mShowScores.setSelected(viewSet.getDisplayLineScores());
		mShowScores.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewSet.setDisplayLineScores(mShowScores.isSelected());
				populateList();
			}
		});

		menu.add(mShowScores);
		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	class ListRenderer extends DefaultListCellRenderer
	{
		private DecimalFormat df = new DecimalFormat("0.000");

		private Color selectedBG = new Color(240, 240, 240);
		private Color selectedFG = new Color(255, 0, 0);

		public ListRenderer()
		{
			setOpaque(true);
		}

		// Set the attributes of the class and return a reference
		public JLabel getListCellRendererComponent(JList list, Object obj,
				int i, boolean iss, boolean chf)
		{
			JLabel label = (JLabel) super.getListCellRendererComponent(list, obj, i, iss, chf);

			label.setFont(font);
			label.setOpaque(true);

			LineInfo li = (LineInfo) obj;

			if (view.isSplitter(li.getLine()))
				label.setText(" ");
			else
			{
				if (viewSet.getDisplayLineScores())
					label.setText(" " + df.format(li.getScore()) + " " + li);
				else
					label.setText(" " + li.toString());
			}

			// Highlight the line "under" the mouse
			if (i == view.mouseOverLine)
			{
				label.setBackground(selectedBG);
				label.setForeground(selectedFG);
			}
			else
			{
				label.setBackground(list.getBackground());
				label.setForeground(list.getForeground());
			}

			return label;
		}
	}
}