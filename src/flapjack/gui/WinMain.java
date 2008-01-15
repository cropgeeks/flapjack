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

		setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}

	void importFile()
	{
		DataImportDialog dialog = new DataImportDialog();

		if (dialog.isOK())
		{
			File mapFile  = dialog.getMapFile();
			File genoFile = dialog.getGenotypeFile();

			DataSet dataSet = new DataLoadingDialog(mapFile, genoFile).getDataSet();

			if (dataSet != null)
			{
				project.addDataSet(dataSet);

				gPanel.setData(dataSet);

				int location = splitPane.getDividerLocation();
				splitPane.setRightComponent(gPanel);
				splitPane.setDividerLocation(location);
			}
		}
	}
}