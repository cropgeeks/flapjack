// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io;

import java.awt.*;
import java.io.*;
import java.text.*;
import java.util.*;

import jhi.flapjack.data.*;

import scri.commons.io.*;
import scri.commons.gui.*;

public class QTLExporter extends SimpleJob
{
	private NumberFormat nf = NumberFormat.getInstance();

	private DataSet dataSet;
	private File file;


	public QTLExporter(DataSet dataSet, File file)
	{
		this.dataSet = dataSet;
		this.file = file;

		nf.setMaximumFractionDigits(6);
	}

	public void runJob(int index)
		throws Exception
	{
		// Get the list of QTL
		ArrayList<QTL> qtls = new ArrayList<>();

		// Scan each chromosome
		for (ChromosomeMap cMap: dataSet.getChromosomeMaps())
			for (QTL qtl: cMap.getQtls())
			{
				qtls.add(qtl);
			}

		maximum = qtls.size();
		if (maximum == 0)
			return;

		// Now output the QTL to disk
		BufferedWriter out = new BufferedWriter(new FileWriter(file));

		// File header for drag and drop detection
		out.write("# fjFile = QTL");
		out.newLine();

		writeHeader(out, qtls);

		for (QTL qtl: qtls)
		{
			if (okToRun() == false)
				break;

			out.write(qtl.getName());
			out.write("\t" + qtl.getChromosomeMap().getName());
			out.write("\t" + nf.format(qtl.getPosition()));
			out.write("\t" + nf.format(qtl.getMin()));
			out.write("\t" + nf.format(qtl.getMax()));
			out.write("\t" + qtl.getTrait());
			out.write("\t" + qtl.getExperiment());

			for (int i = 0; i < qtl.getValues().length; i++)
				out.write("\t" + qtl.getValues()[i]);

			out.newLine();
			progress++;
		}


		out.close();
	}

	private void writeHeader(BufferedWriter out, ArrayList<QTL> qtls)
		throws Exception
	{
		QTL qtl = qtls.get(0);

		out.write(RB.getString("gui.traits.QTLTableModel.qtl"));
		out.write("\t" + RB.getString("gui.traits.QTLTableModel.chromosome"));
		out.write("\t" + RB.getString("gui.traits.QTLTableModel.position"));
		out.write("\t" + RB.getString("gui.traits.QTLTableModel.min"));
		out.write("\t" + RB.getString("gui.traits.QTLTableModel.max"));
		out.write("\t" + RB.getString("gui.traits.QTLTableModel.trait"));
		out.write("\t" + RB.getString("gui.traits.QTLTableModel.experiment"));
//		out.write("\t" + RB.getString("gui.traits.QTLTableModel.visible"));

		for (int i = 0; i < qtl.getValues().length; i++)
			out.write("\t" + qtl.getVNames()[i]);

		out.newLine();
	}
}