// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io;

import java.io.*;

import jhi.flapjack.data.results.*;

import scri.commons.gui.*;

public class SimMatrixExporter extends SimpleJob
{
	private SimMatrix matrix;
	private PrintWriter writer;
	// When true prints a listing of the line names in the first column of output
	private boolean hasRowHeader;

	private String filename;
	private boolean isFileWriting;

	// Constructor that creates a SimMatrixExporter to ouput using a pre-existing
	// PrintWriter that is passed in.
	public SimMatrixExporter(SimMatrix matrix, PrintWriter writer)
	{
		this.matrix = matrix;
		this.writer = writer;

		// +1 to count the header line too
		maximum = matrix.getLineInfos().size() + 1;
		hasRowHeader = false;
	}

	// Constructor that creates a PrintWriter from a supplied filename to
	// export a SimMatrix to file.
	public SimMatrixExporter(SimMatrix matrix, String filename)
	{
		this.matrix = matrix;
		this.filename = filename;

		maximum = matrix.getLineInfos().size() + 1;
		hasRowHeader = true;
		isFileWriting = true;
	}

	@Override
	public void runJob(int i)
		throws Exception
	{
		// The dendrogram can be passed to the servlets; in this scenario we
		// *always* want to use \r\n EOL chars...
		String EOL = "\r\n";

		// ...but for local writing to disk, the EOL should be platform-specific
		if (filename != null)
		{
			writer = new PrintWriter(new FileOutputStream(new File(filename)));
			EOL = System.getProperty("line.separator");
		}

		// Write the header line of the matrix
		writer.print(matrix.createFileHeaderLine(hasRowHeader));
		writer.flush();
		writer.print(EOL);
		writer.flush();
		progress++;

		// Write each successive line of the matrix file
		for (int j=0; j < matrix.getLineInfos().size() && okToRun; j++, progress++)
		{
			writer.print(matrix.createFileLine(j, hasRowHeader));
			writer.flush();
			writer.print(EOL);
			writer.flush();
		}


		if (isFileWriting)
			writer.close();
	}
}