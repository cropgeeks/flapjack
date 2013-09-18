// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.analysis;

import java.util.*;

import flapjack.data.*;
import flapjack.servlet.*;

import scri.commons.gui.*;

public class PCoAGenerator extends SimpleJob
{
	private SimMatrix matrix;
	private Dendrogram dendrogram;

	public PCoAGenerator(SimMatrix matrix)
	{
		this.matrix = matrix;
	}

	public void runJob(int index)
		throws Exception
	{
		int lineCount = matrix.getLineInfos().size();

		// Run the servlet (upload, run, download)
		PCoAClient client = new PCoAClient();
		client.doClientStuff(matrix, lineCount);
	}
}