// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.gui;

import java.awt.*;

import scri.commons.gui.*;

public class Prefs extends XMLPreferences
{
	// Is this the first time the program has ever been run (by this user)?
	public static boolean isFirstRun = true;
	public static boolean isSCRIUser = false;
	public static int rating = 0;

	public static String visColorSeed = "" + (System.currentTimeMillis() -
		1987200000L); // First appearance after 7 days (now-23 days)

	// Unique Flapjack ID for this user
	public static String flapjackID = SystemUtils.createGUID(32);

	// The last used directory location in file chooser dialogs
	public static String guiCurrentDir = "";
	//replacements for guiCurrentMap and guiCurrentGeno
	public static String guiMapList = "";
	public static String guiGenoList = "";
	// Path to the last selected trait file
	public static String guiTraitHistory = "";
	public static String guiQTLHistory = "";
	public static String guiGraphHistory = "";

	public static String guiQuickExportHistory = "";

	// History for selected external sort orders
	public static String guiExternalSortHistory = "";

	// Use UK English decimal mark settings
	public static boolean guiDecimalEnglish = false;

	public static String guiPedigreeList = "";

	// How many projects have been created
	public static int guiProjectCount = 1;

	// The locations of the last four accessed projects
	public static String guiRecentProject1;
	public static String guiRecentProject2;
	public static String guiRecentProject3;
	public static String guiRecentProject4;

	// Display localised text in...
	public static String localeText = "auto";

	// The width, height, location and maximized status of the main window
	public static int guiWinMainW = 1000;
	public static int guiWinMainH = 700;
	public static int guiWinMainX = Integer.MIN_VALUE;
	public static int guiWinMainY = Integer.MIN_VALUE;
	public static boolean guiWinMainMaximized = false;

	// The location of the main splits pane divider
	public static int guiNavSplitsLocation = 180;

	// The width, height and location of the overview dialog/panel
	public static boolean guiOverviewDialog = false;
	public static int guiOverviewWidth = 300;
	public static int guiOverviewHeight = 300;
	public static int guiOverviewX = Integer.MIN_VALUE;
	public static int guiOverviewY = Integer.MIN_VALUE;
	public static int guiOverviewSplitsLocation = 450;

	// Position of the Filter QTLs dialog
	public static int guiFilterQTLDialogX = Integer.MIN_VALUE;
	public static int guiFilterQTLDialogY = Integer.MIN_VALUE;

	// QTL splitpane location
	public static int guiQTLSplitterLocation = 25;

	// When to check for updates
	public static int guiUpdateSchedule = Install4j.STARTUP;

	// Warning messages
	public static boolean warnDuplicateMarkers = true;
	public static boolean warnEditMarkerMode = true;
	public static boolean warnEditLineMode = true;

	// String matches for missing data and heterozygous values when importing
	public static String ioMissingData = "-";
	public static String ioHeteroSeparator = "/";
	public static boolean ioHeteroCollapse = true;
	public static boolean ioUseHetSep = true;
	public static boolean ioMakeAllChromosome = false;
	public static boolean ioTransposed = false;

	public static int ioProjectFormat = 0;

	// Method used when exporting images
	public static int guiExportImageMethod = 0;
	// Image size to use when exporting
	public static int guiExportImageX;
	public static int guiExportImageY;

	// When cloning views, should Flapjack clone hidden lines/markers?
	public static boolean guiCloneHidden = true;

	// Auto assign traits to the heatmap after a sort-by-trait?
	public static boolean guiAssignTraits = true;

	// Graphs (against markers)
	public static int guiGraphStyle = 0;

	// Finding stuff...
	public static int guiFindDialogX = Integer.MIN_VALUE;
	public static int guiFindDialogY = Integer.MIN_VALUE;
	public static int guiFindMethod = 0;
	public static boolean guiFindMatchCase = false;
	public static boolean guiFindUseRegex = true;
	public static String guiFindHistory = ".*";

	public static boolean guiHideSelectedMarkers = false;
	public static boolean guiHideSelectedLines = false;

	public static int guiMouseMode = Constants.NAVIGATION;

	// Missing markers dialog
	public static boolean guiMissingMarkerAllChromsomes = false;
	public static int guiMissingMarkerPcnt = 95;

	// What type of map scaling should be used (local=0, global=1)
	public static int visMapScaling = Constants.LOCAL;

	public static boolean visShowMiniMapCanvas = true;
	public static boolean visShowLinePanel = true;
	public static boolean visShowMapCanvas = true;
	public static boolean visShowQTLCanvas = true;
	public static boolean visShowRowCanvas = true;
	public static boolean visShowColCanvas = true;
	public static boolean visShowTraitCanvas = true;
	public static boolean visShowStatusPanel = true;
	public static boolean visShowGraphCanvas = true;

	// Link the x/y zoom sliders?
	public static boolean visLinkSliders = true;
	// Which is an option only visible when advanced zoom controls are used
	public static boolean visAdvancedZoom = false;

	// Should the visualization canvas overlay the raw data?
	public static boolean visShowGenotypes = false;
	// Should the visualization canvas highlight heterozygotes?
	public static boolean visHighlightHZ = false;
	// Should the visualization canvas highlight gaps?
	public static boolean visHighlightGaps = false;

	// Mouse-over crosshair?
	public static boolean visCrosshair = true;

	// Standard colors used regardless of scheme
	public static Color visColorBackground;
	public static Color visColorOverviewOutline;
	public static Color visColorOverviewFill;
	public static Color visColorText;
	public static Color visColorHeatmapHigh;
	public static Color visColorHeatmapLow;

	// Colors used by the nucleotide color scheme
	public static Color visColorNucleotideA;
	public static Color visColorNucleotideC;
	public static Color visColorNucleotideG;
	public static Color visColorNucleotideT;
	public static Color visColorNucleotideHZ;
	public static Color visColorNucleotideOther;

	// Colors used by the ABH color scheme
	public static Color visColorABH_A;
	public static Color visColorABH_B;
	public static Color visColorABH_H;
	public static Color visColorABH_C;
	public static Color visColorABH_D;
	public static Color visColorABH_Other;

	// Colors used by the simple two color scheme
	public static Color visColorSimple2State1;
	public static Color visColorSimple2State2;
	public static Color visColorSimple2Other;

	// Colors used by the similarity schemes
	public static Color visColorSimilarityState1;
	public static Color visColorSimilarityState1Dark;
	public static Color visColorSimilarityState2;

	// Colors used by the allele frequency scheme
	public static Color visColorLoFreqState;
	public static Color visColorHiFreqState;

	public static boolean miscSubscribed = false;
	public static String miscEmail = "";
	public static String miscInstitution = "";

	// Proxy settings
	public static boolean proxyUse = false;
	public static int proxyPort = 8080;
	public static String proxyAddress = "";
	public static String proxyUsername = "";
	public static String proxyPassword = "";

	static void setDefaults()
	{
		setColorDefaults();
	}

	public static void setColorDefaults()
	{
		visColorBackground = new Color(255, 255, 255);
		visColorOverviewOutline = new Color(255, 0, 0);
		visColorOverviewFill = new Color(50, 50, 0);
		visColorText = new Color(0, 0, 0);
		visColorHeatmapLow = new Color(120, 255, 120);
		visColorHeatmapHigh = new Color(255, 120, 120);

		visColorNucleotideA = new Color(120, 255, 120);
		visColorNucleotideC = new Color(255, 160, 120);
		visColorNucleotideG = new Color(255, 120, 120);
		visColorNucleotideT = new Color(120, 120, 255);
		visColorNucleotideHZ = new Color(100, 100, 100);
		visColorNucleotideOther = new Color(204, 204, 204);

		visColorABH_A = new Color(255, 120, 120);
		visColorABH_B = new Color(120, 120, 255);
		visColorABH_H = new Color(120, 255, 120);
		visColorABH_C = new Color(208, 208, 255);
		visColorABH_D = new Color(255, 160, 120);
		visColorABH_Other = new Color(204, 204, 204);

		visColorSimple2State1 = new Color(255, 120, 120);
		visColorSimple2State2 = new Color(120, 255, 120);
		visColorSimple2Other = new Color(204, 204, 204);

		visColorSimilarityState1 = new Color(255, 120, 120);
		visColorSimilarityState1Dark = new Color(255, 90, 90);
		visColorSimilarityState2 = new Color(120, 255, 120);

		visColorLoFreqState = new Color(102, 102, 255);
		visColorHiFreqState = new Color(204, 255, 204);
	}

	// Updates the array of recently accessed documents so that 'document' is
	// the first element, even if it has been accessed previously
	public static void setRecentFiles(String[] files, String[] recentDocs)
	{
		for (int i = 0; i < files.length; i++)
			recentDocs[i] = files[i];
	}
}