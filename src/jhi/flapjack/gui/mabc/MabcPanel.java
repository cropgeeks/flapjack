// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.mabc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.table.*;

import scri.commons.gui.*;

public class MabcPanel extends JPanel implements ActionListener
{
	private JTable table;
	private MabcTableModel model;
	private GTViewSet viewSet;

	private MabcPanelNB controls;

	public MabcPanel(GTViewSet viewSet)
	{
		controls = new MabcPanelNB(this);
		this.viewSet = viewSet;

		table = controls.table;

		setLayout(new BorderLayout());
		add(new TitlePanel("MABC Results"), BorderLayout.NORTH);

//		setLayout(new BorderLayout(0, 0));
//		setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0));
		add(controls);

		updateModel(viewSet);

		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				handlePopup(e);
			}
			public void mouseReleased(MouseEvent e) {
				handlePopup(e);
			}
		});
	}

	public void updateModel(GTViewSet viewset)
	{
		model = new MabcTableModel(viewset);

		table.setModel(model);
		((LineDataTable)table).setViewSet(viewSet);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == controls.bSort)
			((LineDataTable)table).multiColumnSort();

		else if (e.getSource() == controls.bExport)
			((LineDataTable)table).exportData();
	}

	private void handlePopup(MouseEvent e)
	{
		if (e.isPopupTrigger() == false)
			return;

		JPopupMenu menu = new JPopupMenu();

		final JMenuItem mCopy = new JMenuItem();
		mCopy.setText("Copy to clipboard");
		mCopy.setIcon(Icons.getIcon("COPY"));
		mCopy.addActionListener(event ->
		{
			((LineDataTable)table).copyTableToClipboard();
		});

		final JMenuItem mExport = new JMenuItem();
		mExport.setText("Export to file");
		mExport.setIcon(Icons.getIcon("EXPORTTRAITS"));
		mExport.addActionListener(event ->
		{
			((LineDataTable)table).exportData();
		});

		menu.add(mCopy);
		menu.add(mExport);
		menu.show(e.getComponent(), e.getX(), e.getY());
	}
}