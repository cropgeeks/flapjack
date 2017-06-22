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

	public ArrayList<PedLineInfo> getPedigrees()
		{ return pedigrees; }

	public void setPedigrees(ArrayList<PedLineInfo> pedigrees)
		{ this.pedigrees = pedigrees; }



	public void create(ArrayList<String> triplets, HashMap<String, ArrayList<Line>> linesByName)
		throws DataFormatException
	{
		// Each triplet will be of the form:
		//   progeny \t parent \t type

		for (String str: triplets)
		{
			String[] tokens = str.split("\t");

			ArrayList<Line> parents = linesByName.get(tokens[1]);

			if (parents == null)
			{
				System.err.println("WARNING: Pedigree header references germplasm '" + tokens[1] + "' that does not exist");
				continue;
			}

			int type = PedLineInfo.TYPE_NA;
			switch (tokens[2])
			{
				case "RP" : type = PedLineInfo.TYPE_RP; break;
				case "DP" : type = PedLineInfo.TYPE_DP; break;
			}

			// STAR special case; the parental information applies to all lines
			if (tokens[0].equals("*"))
			{
				for (Map.Entry<String, ArrayList<Line>> entry : linesByName.entrySet())
					for (Line progeny: entry.getValue())
					{
						// Don't add a line as a parent of itself!!
						for (Line parent: parents)
							if (progeny.getName().equals(parent.getName()) == false)
								pedigrees.add(new PedLineInfo(progeny, parent, type));
					}
			}
			else
			{
				// Add every instance of this parent to every instance of this
				// child (by instance we mean duplicate lines)
				ArrayList<Line> progenies = linesByName.get(tokens[0]);

				if (progenies == null)
				{
					System.err.println("WARNING: Pedigree header references germplasm '" + tokens[0] + "' that does not exist");
					continue;
				}

				for (Line progeny: progenies)
					for (Line parent: parents)
						pedigrees.add(new PedLineInfo(progeny, parent, type));
			}
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
}