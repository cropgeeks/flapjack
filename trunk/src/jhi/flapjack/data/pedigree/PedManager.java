package jhi.flapjack.data.pedigree;

import java.util.*;

import jhi.flapjack.data.*;
import jhi.flapjack.io.*;

public class PedManager extends XMLRoot
{
	ArrayList<PedLineInfo> pedigrees = new ArrayList<>();

	public PedManager()
	{
	}

	public void create(ArrayList<String> triplets, HashMap<String, ArrayList<Line>> linesByName)
		throws DataFormatException
	{
		// Each triplet will be of the form:
		//   progeny \t parent \t type

		for (String str: triplets)
		{
			String[] tokens = str.split("\t");

			// STAR CASE


			// linesByName could have multiple Line instances per name (if
			// duplicates were allowed), but there's no way to map pedigree info
			// in that situation, so we just use the first instance
			Line progeny = linesByName.get(tokens[0]).get(0);
			Line parent = linesByName.get(tokens[1]).get(0);

			int type = PedLineInfo.TYPE_NA;
			switch (tokens[2])
			{
				case "RP" : type = PedLineInfo.TYPE_RP; break;
				case "DP" : type = PedLineInfo.TYPE_DP; break;
			}

			pedigrees.add(new PedLineInfo(progeny, parent, type));
		}
	}

	public ArrayList<PedLineInfo> getParentsForLine(LineInfo lineInfo)
	{
		Line line = lineInfo.getLine();

		ArrayList<PedLineInfo> parents = new ArrayList<>();

		for (PedLineInfo pedInfo: pedigrees)
		{
			if (pedInfo.getProgeny() == line)
				parents.add(pedInfo);
		}

		return parents;
	}

	// this could be written as another structure that holds a list of known RP/DP for quicker lookup
	public boolean isRP(LineInfo lineInfo)
	{
		Line line = lineInfo.getLine();

		for (PedLineInfo pedInfo: pedigrees)
		{
			if (pedInfo.getParent() == line && pedInfo.getType() == PedLineInfo.TYPE_RP)
				return true;
		}

		return false;
	}

	public boolean isDP(LineInfo lineInfo)
	{
		Line line = lineInfo.getLine();

		for (PedLineInfo pedInfo: pedigrees)
		{
			if (pedInfo.getParent() == line && pedInfo.getType() == PedLineInfo.TYPE_DP)
				return true;
		}

		return false;
	}

	// Returns true if we're not holding any pedigree information
	public boolean hasNoInfo()
	{
		return pedigrees.size() == 0;
	}
}