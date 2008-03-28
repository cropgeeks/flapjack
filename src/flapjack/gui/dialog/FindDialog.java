package flapjack.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.regex.*;
import javax.swing.*;

import flapjack.analysis.*;
import flapjack.data.*;
import flapjack.gui.*;
import flapjack.gui.visualization.*;

import scri.commons.gui.*;

public class FindDialog extends JDialog implements ActionListener
{
	private JButton bFindNext, bFindPrev, bClose;

	private NBFindPanel nbPanel = new NBFindPanel();
	private GenotypePanel gPanel;

	private Finder finder = new Finder();

	public FindDialog(JFrame parent, GenotypePanel gPanel)
	{
		super(parent, RB.getString("gui.dialog.FindDialog.title"), false);

		this.gPanel = gPanel;

		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);
		addListeners();

		getRootPane().setDefaultButton(bFindNext);
		SwingUtils.addCloseHandler(this, bClose);

		pack();
		setResizable(false);

		// Work out the current screen's width and height
		int scrnW = SwingUtils.getVirtualScreenDimension().width;
		int scrnH = SwingUtils.getVirtualScreenDimension().height;

		// Determine where on screen to display
		if (Prefs.guiFindDialogShown == false ||
			Prefs.guiFindDialogX > (scrnW-50) || Prefs.guiFindDialogY > (scrnH-50))
			setLocationRelativeTo(Flapjack.winMain);
		else
			setLocation(Prefs.guiFindDialogX, Prefs.guiFindDialogY);
	}

	private void addListeners()
	{
		addComponentListener(new ComponentAdapter()
		{
			public void componentMoved(ComponentEvent e)
			{
				Prefs.guiFindDialogX = getLocation().x;
				Prefs.guiFindDialogY = getLocation().y;
			}
		});
	}

	private JPanel createButtons()
	{
		bFindNext = SwingUtils.getButton(RB.getString("gui.dialog.FindDialog.findNext"));
		bFindNext.addActionListener(this);
		bFindNext.setMnemonic(KeyEvent.VK_N);
		bFindNext.setDisplayedMnemonicIndex(5);
		bFindPrev = SwingUtils.getButton(RB.getString("gui.dialog.FindDialog.findPrev"));
		bFindPrev.addActionListener(this);
		bFindPrev.setMnemonic(KeyEvent.VK_P);
		bClose = SwingUtils.getButton(RB.getString("gui.text.close"));
		bClose.addActionListener(this);

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		p1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 5));
		p1.add(bFindNext);
		p1.add(bFindPrev);
		p1.add(bClose);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		// Search forwards...
		if (e.getSource() == bFindNext && finder.isSearching == false)
		{
			finder.findNext = true;
			new Thread(finder).start();
		}
		// Search backwards...
		else if (e.getSource() == bFindPrev && finder.isSearching == false)
		{
			finder.findNext = false;
			new Thread(finder).start();
		}

		else if (e.getSource() == bClose)
			setVisible(false);
	}

	private class Finder implements Runnable
	{
		// Are we searching, and in which direction?
		boolean isSearching = false;
		boolean findNext = true;

		// Objects used to perform the searching
		FindLine lineFinder;

		// The viewset/view currently in focus
		GTViewSet viewSet = null;
		GTView view = null;

		// This method deals with changes to the GenotypePanel that may affect
		// the FindDialog. If the user has switched between chromosomes or even
		// changed to a completely different dataset (viewset) then the objects
		// used for searching that data must be updated to reflect this.
		private void initFinder()
		{
			boolean reset = false;

			// Has the main viewset changed?
			if (viewSet != gPanel.getViewSet())
			{
				reset = true;
				viewSet = gPanel.getViewSet();
			}

			// Has the current view within the viewset changed?
			if (view != gPanel.getView())
			{
				reset = true;
				view = gPanel.getView();
			}

			if (reset)
			{
				// Create a new line finder, that searches for lines in "view"
				lineFinder = new FindLine(view,
					findNext, Prefs.guiFindMatchCase, Prefs.guiFindUseRegex);

				// TODO: markerFinder = new .....
			}
		}

		public void run()
		{
			initFinder();

			// Check that any regex is valid
			if (Prefs.guiFindUseRegex)
			{
				try { Pattern.compile(nbPanel.getSearchStr()); }
				catch (PatternSyntaxException e)
				{
					TaskDialog.error(
						RB.format("gui.dialog.FindDialog.regexError", e),
						RB.getString("gui.text.close"));
					return;
				}
			}

			// Update the finder's settings before starting
			lineFinder.setFindNext(findNext);
			lineFinder.setMatchCase(Prefs.guiFindMatchCase);
			lineFinder.setUseRegex(Prefs.guiFindUseRegex);

			isSearching = true;
			setLabel("", null);

			if (Prefs.guiFindMethod == 0)
				findLine();
			else
				findMarker();
		}

		private void findLine()
		{
			int index = lineFinder.getIndex(nbPanel.getSearchStr());

			if (index == -1)
				setLabel(RB.getString("gui.dialog.FindDialog.notFound"), Color.red);
			else
			{
				Line line = gPanel.getView().getLine(index);
				setLabel(RB.format("gui.dialog.FindDialog.foundLine", line.getName(), (index+1)), null);

				jumpToPosition(index, -1);
			}

			isSearching = false;
		}

		private void findMarker()
		{
			isSearching = false;
		}


		// EDT methods run within the runnable non EDT thread

		private void setLabel(final String txt, final Color color)
		{
			Runnable r = new Runnable() {
				public void run()
				{
					nbPanel.foundLabel2.setText(txt);

					if (color == null)
						nbPanel.foundLabel2.setForeground((Color)UIManager.get("Label.color"));
					else
						nbPanel.foundLabel2.setForeground(color);
				}
			};

			try { SwingUtilities.invokeAndWait(r); }
			catch (Exception e) {}
		}

		private void jumpToPosition(final int line, final int marker)
		{
			Runnable r = new Runnable() {
				public void run() {
					gPanel.jumpToPosition(line, marker);
				}
			};

			try { SwingUtilities.invokeAndWait(r); }
			catch (Exception e) {}
		}
	}
}