// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.RB;

public class HeterozygousMarkersDialog extends PercentMissingDialog
{
	public HeterozygousMarkersDialog(GTViewSet viewSet)
	{
		super(
			viewSet,
			RB.getString("gui.dialog.HeterozygousMarkersDialog.title"),
			RB.getString("gui.dialog.HeterozygousMarkersDialog.filter"),
			"filtering_markers.html",
			"gui.dialog.HeterozygousMarkersDialog.percentLabel",
			Prefs.guiHeterozygousMarkerPcnt
		);
	}

	protected void applySettings()
	{
		Prefs.guiHeterozygousMarkerPcnt = value;
	}
}