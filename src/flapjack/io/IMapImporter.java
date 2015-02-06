package flapjack.io;

import java.io.*;
import java.util.*;

public interface IMapImporter
{
	public void importMap() throws IOException, DataFormatException;

	public void cancelImport();

	public HashMap<String, MarkerIndex> getMarkersHashMap();

	public LinkedList<String> getDuplicates();

	public long getBytesRead();

	long getMarkerCount();
}
