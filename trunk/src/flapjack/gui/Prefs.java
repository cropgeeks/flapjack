package flapjack.gui;

import java.awt.*;

import scri.commons.gui.*;

public class Prefs extends XMLPreferences
{
	// Is this the first time the program has ever been run (by this user)?
	public static boolean isFirstRun = true;

	// The last used directory location in file chooser dialogs
	public static String guiCurrentDir = "";

	// How many projects have been created
	public static int guiProjectCount = 1;

	// The locations of the last four accessed projects
	public static String guiRecentProject1;
	public static String guiRecentProject2;
	public static String guiRecentProject3;
	public static String guiRecentProject4;

	// The width, height, location and maximized status of the main window
	public static int guiWinMainWidth = 800;
	public static int guiWinMainHeight = 600;
	public static int guiWinMainX = 0;
	public static int guiWinMainY = 0;
	public static boolean guiWinMainMaximized = false;

	// The location of the main splits pane divider
	public static int guiNavSplitsLocation = 150;

	// The width, height and location of the overview dialog/panel
	public static boolean guiOverviewDialog = false;
	public static int guiOverviewWidth = 300;
	public static int guiOverviewHeight = 300;
	public static int guiOverviewX = 0;
	public static int guiOverviewY = 0;
	public static int guiOverviewSplitsLocation = 375;

	// The type of back-buffer we want to use for faster rendering
	public static int guiBackBufferType = java.awt.image.BufferedImage.TYPE_INT_RGB;

	// Method to use when importing data
	public static int guiImportMethod = 0;

	// Warning messages
	public static boolean warnDuplicateMarkers = true;

	// String matches for missing data and heterozygous values when importing
	public static String ioMissingData = "-";
	public static String ioHeteroSeparator = "/";

	// Should the visualization canvas overlay the raw data?
	public static boolean visShowGenotypes = false;

	// Method used when exporting images
	public static int guiExportImageMethod = 0;
	// Image size to use when exporting
	public static int guiExportImageX;
	public static int guiExportImageY;
}