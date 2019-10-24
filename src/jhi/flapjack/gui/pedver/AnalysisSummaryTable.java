// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.pedver;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import scri.commons.gui.*;

public class AnalysisSummaryTable extends JTable
{
	public AnalysisSummaryTable()
	{
		setDefaultRenderer(Number.class,
			new NumberFormatCellRenderer());
	}
}