// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.analysis;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;

import scri.commons.gui.*;

public class HomozygousLinesDialog extends PercentMissingDialog
{
	public HomozygousLinesDialog(GTViewSet viewSet)
	{
		super(
			viewSet,
			RB.getString("gui.dialog.HomozygousLinesDialog.title"),
			RB.getString("gui.dialog.HomozygousLinesDialog.filter"),
			"filtering_markers.html",
			"gui.dialog.HomozygousLinesDialog.percentLabel",
			Prefs.guiHomozygousLinesPcnt
		);
	}

	protected void applySettings()
	{
		Prefs.guiHomozygousLinesPcnt = value;
	}
}