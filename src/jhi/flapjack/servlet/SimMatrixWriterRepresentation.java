// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.servlet;

import java.io.*;

import jhi.flapjack.data.results.*;
import jhi.flapjack.io.*;

import org.restlet.data.*;
import org.restlet.representation.*;

public class SimMatrixWriterRepresentation extends WriterRepresentation
{
	private SimMatrix matrix;

	public SimMatrixWriterRepresentation(MediaType mediaType, SimMatrix matrix)
	{
		super(mediaType);

		this.matrix = matrix;
	}

	@Override
	public void write(Writer writer) throws IOException
	{
		PrintWriter pw = new PrintWriter(writer);
		SimMatrixExporter exporter = new SimMatrixExporter(matrix, pw);
		try
		{
			exporter.runJob(0);
		}
		catch (Exception e) { e.printStackTrace(); }
	}
}