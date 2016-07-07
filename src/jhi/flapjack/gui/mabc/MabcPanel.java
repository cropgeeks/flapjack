// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.mabc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import jhi.flapjack.data.*;
import jhi.flapjack.gui.*;
import jhi.flapjack.gui.table.*;

import scri.commons.gui.*;

public class MabcPanel extends JPanel implements ActionListener, ListSelectionListener
{
	private JTable table;
	private MabcTableModel model;
	private GTViewSet viewSet;

	private MabcPanelNB controls;

	// Variables used to 'remember' what the user picked last time they
	// auto-selected or ranked lines
	private int qtlStatusCount = 0, rank = 1;

	public MabcPanel(GTViewSet viewSet)
	{
		controls = new MabcPanelNB(this);
		this.viewSet = viewSet;

		table = controls.table;
		table.getSelectionModel().addListSelectionListener(this);

		setLayout(new BorderLayout());
		add(new TitlePanel(RB.getString("gui.mabc.MabcPanel.title")), BorderLayout.NORTH);
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

		else if (e.getSource() == controls.bSelect)
			displayAutoSelectDialog();

		else if (e.getSource() == controls.bRank)
			rankSelectedLines();
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
			return;

		controls.bRank.setEnabled(
			table.getSelectionModel().getMinSelectionIndex() != -1);
	}

	private void handlePopup(MouseEvent e)
	{
		if (e.isPopupTrigger() == false)
			return;

		JPopupMenu menu = ((LineDataTable)table).getPopupMenu();

		final JMenuItem mSelect = new JMenuItem();
		mSelect.setText(RB.getString("gui.mabc.MabcPanel.autoSelect"));
		mSelect.setIcon(Icons.getIcon("AUTOSELECT"));
		mSelect.addActionListener(event -> displayAutoSelectDialog());

		final JMenuItem mRank = new JMenuItem();
		mRank.setText("Rank...");
		mRank.setIcon(Icons.getIcon("RANK"));
		mRank.addActionListener(event -> rankSelectedLines());
		mRank.setEnabled(table.getSelectionModel().getMinSelectionIndex() != -1);

		menu.add(mSelect, 0);
		menu.add(mRank, 1);
		menu.add(new JPopupMenu.Separator(), 2);

		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	private void displayAutoSelectDialog()
	{
		int qtlCount = viewSet.getLines().get(0).results().getMABCLineStats().getQTLScores().size();
		SpinnerNumberModel sModel = new SpinnerNumberModel(qtlStatusCount, 0, qtlCount, 1);
		JSpinner spinner = new JSpinner(sModel);
		((JSpinner.DefaultEditor)spinner.getEditor()).getTextField().setColumns(4);

		JPanel panel = new JPanel(new FlowLayout());
		panel.add(new JLabel(RB.getString("gui.mabc.MabcPanel.autoSelectDialogLabel")));
		panel.add(spinner);

		int option = JOptionPane.showOptionDialog(Flapjack.winMain, panel,
			RB.getString("gui.mabc.MabcPanel.autoSelectDialogTitle"),
			JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

		if (option == JOptionPane.OK_OPTION)
		{
			qtlStatusCount = (int)spinner.getValue();
			int selected = model.selectQTL(qtlStatusCount);

			int total = model.getRowCount();
			TaskDialog.info(
				RB.format("gui.mabc.MabcPanel.selectedLines", selected, total),
				RB.getString("gui.text.close"));
		}
	}

	private void rankSelectedLines()
	{
		SpinnerNumberModel sModel = new SpinnerNumberModel(
			rank, Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
		JSpinner spinner = new JSpinner(sModel);
		((JSpinner.DefaultEditor)spinner.getEditor()).getTextField().setColumns(4);

		JPanel panel = new JPanel(new FlowLayout());
		panel.add(new JLabel(RB.getString("gui.mabc.MabcPanel.rankLabel")));
		panel.add(spinner);

		int option = JOptionPane.showOptionDialog(Flapjack.winMain, panel,
			RB.getString("gui.mabc.MabcPanel.rankTitle"),
			JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

		if (option == JOptionPane.OK_OPTION)
		{
			rank = (int)spinner.getValue();
			ListSelectionModel lsModel = table.getSelectionModel();

			// Loop over every (selected) row, convert it to a model row, and
			// then set the new rank value on it
			for (int i = 0; i < table.getRowCount(); i++)
				if (lsModel.isSelectedIndex(i))
					model.setRank(table.convertRowIndexToModel(i), rank);
		}
	}
}