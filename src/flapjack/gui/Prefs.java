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

	// Should projects be saved in a compressed format?
	public static boolean guiSaveCompressed = true;

	// The locations of the last four accessed projects
	public static String guiRecentProject1;
	public static String guiRecentProject2;
	public static String guiRecentProject3;
	public static String guiRecentProject4;

	// Display localised text in...
	public static String localeText = "auto";

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

	// Method to use when importing data
	public static int guiImportMethod = 0;

	// When to check for updates
	public static int guiUpdateSchedule = Install4j.STARTUP;

	// Warning messages
	public static boolean warnDuplicateMarkers = true;

	// String matches for missing data and heterozygous values when importing
	public static String ioMissingData = "-";
	public static String ioHeteroSeparator = "/";

	// Method used when exporting images
	public static int guiExportImageMethod = 0;
	// Image size to use when exporting
	public static int guiExportImageX;
	public static int guiExportImageY;

	// Finding stuff...
	public static boolean guiFindDialogShown = false;
	public static int guiFindDialogX = 0;
	public static int guiFindDialogY = 0;
	public static int guiFindMethod = 0;
	public static boolean guiFindAllChromo = true;
	public static boolean guiFindMatchCase = false;
	public static boolean guiFindUseRegex = false;

	// Attempt to use back-buffering to improve performance?
	public static boolean visBackBuffer = true;
	// The type of back-buffer we want to use for faster rendering
	public static int visBackBufferType = java.awt.image.BufferedImage.TYPE_INT_RGB;

	// Should the visualization canvas overlay the raw data?
	public static boolean visShowGenotypes = false;
}