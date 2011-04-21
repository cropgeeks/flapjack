// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.io;

import java.awt.*;
import java.io.*;
import java.text.*;
import java.util.*;

import flapjack.data.*;

import scri.commons.file.*;
import scri.commons.gui.*;

public class QTLExporter extends SimpleJob
{
	private DataSet dataSet;
	private File file;


	public QTLExporter(DataSet dataSet, File file)
	{
		this.dataSet = dataSet;
		this.file = file;
	}

	public void runJob(int index)
		throws Exception
	{

	}
}