package flapjack.gui.dialog.analysis;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import flapjack.data.*;
import flapjack.gui.*;

import scri.commons.gui.*;

public class AlleleStatisticsDialog extends JDialog implements ActionListener
{
	private JButton bClose;

	private NBAlleleStatisticsPanel nbPanel;

	public AlleleStatisticsDialog(GTViewSet viewSet)
	{
		super(
			Flapjack.winMain,
			RB.getString("gui.dialog.analysis.AlleleStatisticsDialog.title"),
			true
		);

		Vector<int[]> results = getStatistics(viewSet);
		nbPanel = new NBAlleleStatisticsPanel(viewSet, results);

		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bClose);
		SwingUtils.addCloseHandler(this, bClose);

		pack();
		setLocationRelativeTo(Flapjack.winMain);
//		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bClose = SwingUtils.getButton(RB.getString("gui.text.close"));
		bClose.addActionListener(this);

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		p1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 5));
		p1.add(bClose);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		setVisible(false);
	}

	private Vector<int[]> getStatistics(GTViewSet viewSet)
	{
		StateTable stateTable = viewSet.getDataSet().getStateTable();

		int viewCount = viewSet.getViews().size();
		int stateCount = stateTable.size();

		Vector<int[]> results = new Vector<int[]>(viewCount);

		int i = 0;
		for (GTView view: viewSet.getViews())
			results.add(stateTable.getStatistics(view));

		return results;
	}
}