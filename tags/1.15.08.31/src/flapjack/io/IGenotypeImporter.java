package flapjack.io;

import java.io.*;

public interface IGenotypeImporter
{
	public void importGenotypeData() throws IOException, DataFormatException;

	public void cancelImport();

	public long getLineCount();

	public long getMarkerCount();

	public long getBytesRead();

	public void cleanUp();
}