package jhi.flapjack.data;

import java.util.*;
import java.util.stream.Collectors;

public class PedigreeManager extends XMLRoot
{
	public static final String PEDVERLINES = "parentsfor";
	public static final String RECURRENTPARENT = "recurrentparent";
	public static final String DONORPARENT = "donorparent";

	private HashMap<Line, ArrayList<Line>> parentsToChildren = new HashMap<>();
	private HashMap<Line, ArrayList<Line>> childrenToParents = new HashMap<>();
	private HashSet<ArrayList<Line>> parentSets = new HashSet<>();

	private Line recurrentParent;
	private Line donorParent;

	public PedigreeManager()
	{
	}

	public boolean init(HashMap<String, ArrayList<String>> linesToParents, HashMap<String, ArrayList<Line>> linesWithDupes,
		 String rpName, String dpName)
	{
		if (!isPedigreeValid(linesToParents, linesWithDupes))
			return false;

		// Iterate over parental data (if there was any in the input file)
		linesToParents.forEach((lineName, parentNames) ->
		{
			Line line = linesWithDupes.get(lineName).get(0);

			ArrayList<Line> parents = parentNames.stream()
				.flatMap(name -> linesWithDupes.get(name).stream())
				.collect(Collectors.toCollection(ArrayList::new));

			childrenToParents.put(line, parents);
			parentSets.add(parents);

			addToParentsToChildrenMap(line, parents);
		});

		if (rpName != null && linesWithDupes.containsKey(rpName))
			recurrentParent = linesWithDupes.get(rpName).get(0);

		if (dpName != null && linesWithDupes.containsKey(dpName))
			donorParent = linesWithDupes.get(dpName).get(0);

		return true;
	}

	private boolean isPedigreeValid(HashMap<String, ArrayList<String>> linesToParents, HashMap<String, ArrayList<Line>> linesWithDupes)
	{
		// Do some sanity checking if lineNames have appeared in our linesToParents
		// map, which don't exist in the linesWithDupes map, we should warn the
		// user
		boolean linesExist = linesWithDupes.keySet().containsAll(linesToParents.keySet());
		boolean parentsExist = linesToParents.values().stream()
			.flatMap(Collection::stream)
			.allMatch(linesWithDupes::containsKey);

		return linesExist && parentsExist;
	}

	private void addToParentsToChildrenMap(Line line, ArrayList<Line> parents)
	{
		// Setup linkage between parents and children
		for (Line p : parents)
		{
			parentsToChildren.putIfAbsent(p, new ArrayList<Line>());
			parentsToChildren.get(p).add(line);
		}
	}

	// Methods required for XML serialization

	public HashMap<Line, ArrayList<Line>> getParentsToChildren()
		{ return parentsToChildren; }

	public void setParentsToChildren(HashMap<Line, ArrayList<Line>> parentsToChildren)
		{ this.parentsToChildren = parentsToChildren; }

	public HashMap<Line, ArrayList<Line>> getChildrenToParents()
		{ return childrenToParents; }

	public void setChildrenToParents(HashMap<Line, ArrayList<Line>> childrenToParents)
		{ this.childrenToParents = childrenToParents; }

	public HashSet<ArrayList<Line>> getParentSets()
		{ return parentSets; }

	public void setParentSets(HashSet<ArrayList<Line>> parentSets)
		{ this.parentSets = parentSets; }

	public Line getRecurrentParent()
		{ return recurrentParent; }

	public void setRecurrentParent(Line recurrentParent)
		{ this.recurrentParent = recurrentParent; }

	public Line getDonorParent()
		{ return donorParent; }

	public void setDonorParent(Line donorParent)
		{ this.donorParent = donorParent; }
}