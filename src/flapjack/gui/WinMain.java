package flapjack.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.dialog.*;
import flapjack.gui.visualization.*;
import flapjack.io.*;

public class WinMain extends JFrame
{
	private WinMainMenuBar menubar;

	private JSplitPane splitPane;

	// We maintain just one GenotypePanel that is used to display any dataset
	// as it would require too much memory to assign one per dataset
	private GenotypePanel gPanel;

	// The user's project
	private Project project = new Project();


	WinMain()
	{
		setTitle(RB.getString("gui.WinMain.title"));

		menubar = new WinMainMenuBar(this);
		setJMenuBar(menubar);

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerLocation(150);
		splitPane.setLeftComponent(new JLabel("left", JLabel.CENTER));
		splitPane.setRightComponent(new JLabel("right", JLabel.CENTER));

		gPanel = new GenotypePanel(this);

		add(splitPane);


		setSize(Prefs.guiWinMainWidth, Prefs.guiWinMainHeight);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);


		// Work out the current screen's width and height
		int scrnW = Toolkit.getDefaultToolkit().getScreenSize().width;
		int scrnH = Toolkit.getDefaultToolkit().getScreenSize().height;

		// Determine where on screen (TODO: on which monitor?) to display
		if (Prefs.isFirstRun || Prefs.guiWinMainX > (scrnW-50) || Prefs.guiWinMainY > (scrnH-50))
			setLocationRelativeTo(null);
		else
			setLocation(Prefs.guiWinMainX, Prefs.guiWinMainY);

		// Maximize the frame if neccassary
		if (Prefs.guiWinMainMaximized)
			setExtendedState(Frame.MAXIMIZED_BOTH);

		// Window listeners are added last so they don't interfere with the
		// maximization from above
		addListeners();
	}

	private void addListeners()
	{
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e)
			{
				if (getExtendedState() != Frame.MAXIMIZED_BOTH)
				{
					Prefs.guiWinMainWidth  = getSize().width;
					Prefs.guiWinMainHeight = getSize().height;
					Prefs.guiWinMainX = getLocation().x;
					Prefs.guiWinMainY = getLocation().y;

					Prefs.guiWinMainMaximized = false;
				}
				else
					Prefs.guiWinMainMaximized = true;
			}

			public void componentMoved(ComponentEvent e)
			{
				if (getExtendedState() != Frame.MAXIMIZED_BOTH)
				{
					Prefs.guiWinMainX = getLocation().x;
					Prefs.guiWinMainY = getLocation().y;
				}
			}
		});
	}

	void fileImport()
	{
		DataImportDialog dialog = new DataImportDialog();

		if (dialog.isOK())
		{
			File mapFile  = dialog.getMapFile();
			File genoFile = dialog.getGenotypeFile();

			DataSet dataSet = new DataLoadingDialog(mapFile, genoFile).getDataSet();

			System.out.println("RAW STATE TABLE");
			for (int i = 0; i < dataSet.getStateTable().size(); i++)
				System.out.println("  " + i + ": " + dataSet.getStateTable().getAlleleState(i));

			if (dataSet != null)
			{
				project.addDataSet(dataSet);

				try
				{
					ProjectSerializer.save(project);

					project = ProjectSerializer.load();
				}
				catch (Exception e) { e.printStackTrace(); }

				dataSet = project.getDataSets().lastElement();


				System.out.println("AFTER XML STATE TABLE");
				for (int i = 0; i < dataSet.getStateTable().size(); i++)
					System.out.println("  " + i + ": " + dataSet.getStateTable().getAlleleState(i));


				gPanel.setData(dataSet);

				int location = splitPane.getDividerLocation();
				splitPane.setRightComponent(gPanel);
				splitPane.setDividerLocation(location);
			}
		}
	}

	void fileExit()
	{
		WindowEvent evt = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		processWindowEvent(evt);
	}
}