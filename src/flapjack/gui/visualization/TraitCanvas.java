package flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import flapjack.data.*;
import flapjack.gui.*;

class TraitCanvas extends JPanel
{
	private GenotypeCanvas canvas;
	private Canvas2D traitCanvas;

	private int boxW = 10;
	private int w = boxW * 3;

	private int mouseOverIndex = -1;

	TraitCanvas(GenotypeCanvas canvas)
	{
		this.canvas = canvas;

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(1, 5, 0, 5));
		add(traitCanvas = new Canvas2D());
	}

	int getPanelWidth()
	{
		return isVisible() ? getWidth() : 0;
	}

	// Decides whether to show this panel or not, based on a) are there traits
	// that *can* be shown, and b) does the user want to see the panel
	void determineVisibility()
	{
		int traitCount = 0;
		if (canvas.viewSet != null)
			traitCount = canvas.viewSet.getDataSet().getTraits().size();

		setVisible(traitCount > 0 && Prefs.visShowTraitCanvas);
	}

	private class Canvas2D extends JPanel
	{
		Canvas2D()
		{
			MouseTracker mt = new MouseTracker();

			setPreferredSize(new Dimension(w, 0));
			addMouseListener(mt);
			addMouseMotionListener(mt);
		}

		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);
			Graphics2D g = (Graphics2D) graphics;

			// Calculate the required offset and width
			int height = (canvas.pY2-canvas.pY1);

			// Paint the background
			g.setColor(Prefs.visColorBackground);
			g.fillRect(0, 0, w, height);


			int[] tIndex = canvas.viewSet.getTraits();

			int boxH = canvas.boxH;
			int yS = canvas.pY1 / boxH;
			int yE = yS + canvas.boxCountY;
			if (yE >= canvas.boxTotalY)
				yE = canvas.boxTotalY-1;

			// Translate the drawing to give the appearance of scrolling
			g.setClip(0, 0, w, height);
			g.translate(0, 0-canvas.pY1);


			Color col1 = new Color(120, 255, 120);
			Color col2 = new Color(255, 120, 120);

			int[] c1 = new int[] { col1.getRed(), col1.getGreen(), col1.getBlue() };
			int[] c2 = new int[] { col2.getRed(), col2.getGreen(), col2.getBlue() };

			for (int i = 0; i < tIndex.length; i++)
			{
				int x = i * boxW;

				for (int yIndex = yS, y = (boxH*yS); yIndex <= yE; yIndex++, y += boxH)
				{
					// If there's no index for this location, skip it
					if (tIndex[i] == -1)
						continue;

					Line line = canvas.view.getLine(yIndex);
					TraitValue tv = line.getTraitValues().get(tIndex[i]);

					// Or if the trait is undefined, just skip it
					if (tv.isDefined() == false)
						continue;

					float f1 = (float) (1.0 - tv.getNormal());
					float f2 = (float) tv.getNormal();

          			Color color = new Color(
          				(int) (f1 * c1[0] + f2 * c2[0]),
          				(int) (f1 * c1[1] + f2 * c2[1]),
          				(int) (f1 * c1[2] + f2 * c2[2]));

					g.setColor(color);
					g.fillRect(x, y, boxW, boxH);
				}
			}
		}
	}

	private class MouseTracker extends MouseInputAdapter
	{
		public void mouseEntered(MouseEvent e)
			{ mouseOverIndex = e.getPoint().x / boxW; }

		public void mouseExited(MouseEvent e)
			{ mouseOverIndex = -1; }

		public void mouseMoved(MouseEvent e)
		{
			int y = e.getPoint().y + canvas.pY1;

			int yIndex = y / canvas.boxH;
			mouseOverIndex = e.getPoint().x / boxW;
			int tIndex = canvas.viewSet.getTraits()[mouseOverIndex];

			// Don't attempt to set a tooltip if there's no trait displayed or
			// if the mouse isn't over an actual line
			if (tIndex == -1 || yIndex > canvas.view.getLineCount()-1)
			{
				traitCanvas.setToolTipText(null);
				return;
			}

			Line line = canvas.view.getLine(yIndex);
			TraitValue tv = line.getTraitValues().get(tIndex);

			String traitName = tv.getTrait().getName();
			String tooltip = traitName;

			if (tv.isDefined())
			{
				if (tv.getTrait().traitIsNumerical())
					tooltip += ": " + tv.getValue();
				else
					tooltip += ": " + tv.getTrait().format(tv);

				tooltip += " (" + tv.getNormal() + ")";
			}

			traitCanvas.setToolTipText(tooltip);
		}

		public void mousePressed(MouseEvent e)
		{
			if (e.isPopupTrigger())
				handlePopup(e);
		}

		public void mouseReleased(MouseEvent e)
		{
			if (e.isPopupTrigger())
				handlePopup(e);
		}

		// Pops up a menu with all the current traits, allowing the user to
		// quickly select a new trait (for the column under the mouse)
		private void handlePopup(MouseEvent e)
		{
			DataSet dataSet = canvas.viewSet.getDataSet();
			Vector<Trait> traits = dataSet.getTraits();

			JPopupMenu menu = new JPopupMenu();

			menu.add(getItem(
				RB.getString("gui.visualization.TraitCanvas.undefined"),
				mouseOverIndex, -1));
			menu.addSeparator();

			for (int i = 0; i < traits.size(); i++)
			{
				String traitName = traits.get(i).getName();
				menu.add(getItem(traitName, mouseOverIndex, i));
			}

			menu.show(e.getComponent(), e.getX(), e.getY());
		}

		// Builds a menu item that will set the appropriate value for the view's
		// trait columns
		private JCheckBoxMenuItem getItem(String name, final int colIndex, final int traitIndex)
		{
			JCheckBoxMenuItem item = new JCheckBoxMenuItem(name);

			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					canvas.viewSet.getTraits()[colIndex] = traitIndex;
					repaint();
				}
			});

			if (canvas.viewSet.getTraits()[colIndex] == traitIndex)
				item.setSelected(true);

			return item;
		}
	}
}