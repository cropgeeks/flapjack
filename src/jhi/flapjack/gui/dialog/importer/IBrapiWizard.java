// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.dialog.importer;

import javax.swing.*;

public interface IBrapiWizard
{
	void onShow();

	void onNext();

	JPanel getPanel();

	String getCardName();

	boolean refreshData();

	void onBack();
}