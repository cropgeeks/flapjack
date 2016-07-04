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

	private int prevQTLCount = 0;

	public MabcPanel(GTViewSet viewSet)
	{
		controls = new MabcPanelNB(this);
		this.viewSet = viewSet;

		table = controls.table;

		setLayout(new BorderLayout());
		add(new TitlePanel(RB.getString("gui.mabc.MabcPanel.title")), BorderLayout.NORTH);

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

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == controls.bFilter)
			((LineDataTable)table).filter();

		else if (e.getSource() == controls.bSort)
			((LineDataTable)table).multiColumnSort();

		else if (e.getSource() == controls.bExport)
			((LineDataTable)table).exportData();

		else if (e.getSource() == controls.bAuto)
			displayAutoSelectDialog();
	}

	private void handlePopup(MouseEvent e)
	{
		if (e.isPopupTrigger() == false)
			return;

		JPopupMenu menu = new JPopupMenu();

		final JMenuItem mCopy = new JMenuItem();
		mCopy.setText(RB.getString("gui.mabc.MabcPanel.copy"));
		mCopy.setIcon(Icons.getIcon("COPY"));
		mCopy.addActionListener(event ->
		{
			((LineDataTable)table).copyTableToClipboard();
		});

		final JMenuItem mExport = new JMenuItem();
		mExport.setText(RB.getString("gui.mabc.MabcPanel.export"));
		mExport.setIcon(Icons.getIcon("EXPORTTRAITS"));
		mExport.addActionListener(event ->
		{
			((LineDataTable)table).exportData();
		});

		final JMenuItem mAutoSelect = new JMenuItem();
		mAutoSelect.setText(RB.getString("gui.mabc.MabcPanel.autoSelect"));
		mAutoSelect.setIcon(Icons.getIcon("AUTOSELECT"));
		mAutoSelect.addActionListener(event ->
		{
			displayAutoSelectDialog();
		});

		menu.add(mCopy);
		menu.add(mExport);
		menu.add(mAutoSelect);
		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	private void displayAutoSelectDialog()
	{
		int qtlCount = viewSet.getLines().get(0).results().getMABCLineStats().getQTLScores().size();
		SpinnerNumberModel sModel = new SpinnerNumberModel(prevQTLCount, 0, qtlCount, 1);
		JSpinner spinner = new JSpinner(sModel);

		int option = JOptionPane.showOptionDialog(null, spinner, RB.getString("gui.mabc.MabcPanel.autoSelectDialogTitle"),
											JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

		if (option == JOptionPane.OK_OPTION)
		{
			int selected = model.selectQTL((int)spinner.getValue());
			prevQTLCount = (int)spinner.getValue();

			int total = model.getRowCount();
			String message = RB.format("gui.mabc.MabcPanel.selectedLines", selected, total);
			JOptionPane.showMessageDialog(null, message);
		}
	}
}