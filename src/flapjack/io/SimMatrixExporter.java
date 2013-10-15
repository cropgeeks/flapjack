package flapjack.io;

import java.io.*;

import flapjack.data.*;

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
		if (filename != null)
			writer = new PrintWriter(new FileOutputStream(new File(filename)));

		// Write the header line of the matrix
		writer.println(matrix.createFileHeaderLine(hasRowHeader));
		progress++;

		// Write each successive line of the matrix file
		for (int j=0; j < matrix.getLineInfos().size() && okToRun; j++, progress++)
			writer.println(matrix.createFileLine(j, hasRowHeader));


		if (isFileWriting)
			writer.close();
	}
}