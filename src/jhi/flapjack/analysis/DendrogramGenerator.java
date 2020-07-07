// Copyright 2009-2020 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.analysis;

import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;
import jhi.flapjack.io.*;
import jhi.flapjack.servlet.dendrogram.*;

import scri.commons.gui.*;

public class DendrogramGenerator extends SimpleJob
{
	private SimMatrix matrix, orderedMatrix;
	private Dendrogram dendrogram;
	// Note: this is the *new* view that the dendrogram will ultimately hang from
	// not the view that was used to generate the matrix
	private GTViewSet newViewSet;

	// Stores the index position of each line before it was ordered, but *this*
	// ArrayList is in the *new* (dendrogram) order.
	// e.g if original order = a,b,c and dendrogram new order = b,c,a
	// then this list will store 1,2,0.
	private ArrayList<Integer> rIntOrder;

	private DendrogramClient client;

	public DendrogramGenerator(SimMatrix matrix, GTViewSet newViewSet)
	{
		this.matrix = matrix;
		this.newViewSet = newViewSet;
	}

	public Dendrogram getDendrogram()
		{ return dendrogram; }

	public SimMatrix getOrderedMatrix()
		{ return orderedMatrix; }

	public ArrayList<Integer> rIntOrder()
		{ return rIntOrder; }

	public void runJob(int index)
		throws Exception
	{
		int lineCount = matrix.getLineInfos().size();

		// Run the servlet (upload, run, download)
		client = new DendrogramClient(matrix, lineCount);
		dendrogram = client.generateDendrogram();

		// We've got a result so let Flapjack crete new views and cahce things to db
		if (okToRun)
		{
			// Use the line order that was returned (as a list of ints) to determine
			// what LineInfo order should be stored with the Dendrogram object
			rIntOrder = client.getLineOrder();
			ArrayList<LineInfo> order = new ArrayList<>();

			for (int i = 0; i < rIntOrder.size(); i++)
				order.add(newViewSet.getLines().get(rIntOrder.get(i)));

			dendrogram.setViewSet(newViewSet);
			ProjectSerializerDB.cacheToDisk(dendrogram.getPng());
			ProjectSerializerDB.cacheToDisk(dendrogram.getPdf());

			orderedMatrix = matrix.cloneAndReorder(rIntOrder, order);
			ProjectSerializerDB.cacheToDisk(orderedMatrix);

			newViewSet.setLines(order);
			newViewSet.getDendrograms().add(dendrogram);
			newViewSet.getMatrices().add(orderedMatrix);
		}
	}

	public void cancelJob()
	{
		super.cancelJob();
//		SwingUtilities.invokeLater(() -> client.cancelJob());

		new Thread(() -> client.cancelJob()).start();
	}
}