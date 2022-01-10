// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import scri.commons.gui.*;

public class HeterozygousLinesDialog extends PercentMissingDialog
{
	public HeterozygousLinesDialog(GTViewSet viewSet)
	{
		super(
			viewSet,
			RB.getString("gui.dialog.HeterozygousLinesDialog.title"),
			RB.getString("gui.dialog.HeterozygousLinesDialog.filter"),
			"filtering_markers.html",
			"gui.dialog.HeterozygousLinesDialog.percentLabel",
			Prefs.guiHeterozygousLinesPcnt
		);
	}

	protected void applySettings()
	{
		Prefs.guiHeterozygousLinesPcnt = value;
	}
}