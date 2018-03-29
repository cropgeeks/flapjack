// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.RB;

public class MissingMarkersDialog extends PercentMissingDialog
{
	public MissingMarkersDialog(GTViewSet viewSet)
	{
		super(
			viewSet,
			RB.getString("gui.dialog.MissingMarkersDialog.title"),
			RB.getString("gui.dialog.MissingMarkersDialog.filter"),
			"filtering_markers.html",
			"gui.dialog.MissingMarkersDialog.percentLabel",
			Prefs.guiMissingMarkerPcnt
		);
	}

	protected void applySettings()
	{
		Prefs.guiMissingMarkerPcnt = value;
	}
}
