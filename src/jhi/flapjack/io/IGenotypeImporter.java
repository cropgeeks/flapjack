// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io;

import java.io.*;

public interface IGenotypeImporter
{
	public void importGenotypeData() throws Exception;

	public void cancelImport();

	public long getLineCount();

	public long getMarkerCount();

	public long getBytesRead();

	public void cleanUp();
}