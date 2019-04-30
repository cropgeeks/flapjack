// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io;

public interface IGenotypeImporter
{
	public boolean importGenotypeDataAsBytes()
		throws Exception;

	public void importGenotypeDataAsInts()
		throws Exception;

	public void cancelImport();

	public long getLineCount();

	public long getMarkerCount();

	public long getBytesRead();

	public void cleanUp();

	public boolean isOK();
}