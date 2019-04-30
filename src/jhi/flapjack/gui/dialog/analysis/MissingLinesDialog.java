// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class MissingLinesDialog extends PercentMissingDialog
{
	public MissingLinesDialog(GTViewSet viewSet)
	{
		super(
			viewSet,
			RB.getString("gui.dialog.MissingLinesDialog.title"),
			RB.getString("gui.dialog.MissingLinesDialog.filter"),
			"filtering_markers.html",
			"gui.dialog.MissingLinesDialog.percentLabel",
			Prefs.guiMissingLinesPcnt
		);
	}

	protected void applySettings()
	{
		Prefs.guiMissingLinesPcnt = value;
	}
}