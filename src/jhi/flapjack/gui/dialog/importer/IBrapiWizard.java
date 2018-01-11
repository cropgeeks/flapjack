// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.importer;

import javax.swing.*;

public interface IBrapiWizard
{
	void onShow();

	void onNext();

	JPanel getPanel();

	String getCardName();
}