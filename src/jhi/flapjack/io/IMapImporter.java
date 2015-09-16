package jhi.flapjack.io;

import java.io.*;
import java.util.*;

public interface IMapImporter
{
	public void importMap() throws Exception;

	public void cancelImport();

	public HashMap<String, MarkerIndex> getMarkersHashMap();

	public LinkedList<String> getDuplicates();

	public long getBytesRead();

	public long getMarkerCount();
}
