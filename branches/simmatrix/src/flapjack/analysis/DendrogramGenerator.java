// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.analysis;

import java.awt.image.*;
import java.io.*;
import java.util.*;

import flapjack.data.*;
import flapjack.servlet.*;

import scri.commons.gui.*;

public class DendrogramGenerator extends SimpleJob
{
	private SimMatrix matrix;
	private Dendrogram dendrogram;

	public DendrogramGenerator(SimMatrix matrix)
	{
		this.matrix = matrix;
	}

	public Dendrogram getDendrogram()
		{ return dendrogram; }

	public void runJob(int index)
		throws Exception
	{
		// Turn the matrix into text for easy transmission to the servlet
		StringBuilder sb = matrix.createStringMatrix();
		int lineCount = matrix.getLineInfos().size();

		// Run the servlet (upload, run, download)
		DendrogramClient client = new DendrogramClient();
		dendrogram = client.doClientStuff(sb, lineCount);


		// Use the line order that was returned (as a list of ints) to determine
		// what LineInfo order should be stored with the Dendrogram object
		ArrayList<Integer> intOrder = client.getLineOrder();
		ArrayList<LineInfo> order = new ArrayList<LineInfo>();

		for (int i = 0; i < intOrder.size(); i++)
			order.add(matrix.getLineInfos().get(intOrder.get(i)));

		dendrogram.setOrder(order);
	}
}