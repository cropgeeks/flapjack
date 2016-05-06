package jhi.flapjack.analysis;

import jhi.flapjack.data.*;

import scri.commons.gui.*;

/**
 * Created by gs40939 on 03/05/2016.
 */
public class SimulateF1 extends SimpleJob
{
	private GTViewSet viewSet;

	private LineInfo p1LineInfo;
	private LineInfo p2LineInfo;
	private LineInfo f1LineInfo;

	public SimulateF1(GTViewSet viewSet, LineInfo p1LineInfo, LineInfo p2LineInfo)
	{
		this.viewSet = viewSet;
		this.p1LineInfo = p1LineInfo;
		this.p2LineInfo = p2LineInfo;
	}

	@Override
	public void runJob(int i)
		throws Exception
	{
		// This analysis will run on selected lines/markers only
		AnalysisSet as = new AnalysisSet(viewSet)
			.withViews(null)													// <-- USER STILL NEEDS TO PICK CHROMOSOMES TO WORK WITH (in dialog)
			.withSelectedLines()
			.withAllMarkersIncludingHidden();

		simulate(as);
	}

	private void simulate(AnalysisSet as)
	{
		// Pick two lines as our parents. For now just use the first two
		// TODO: Define the parental lines in some way so they can be set from input file, or UI

		// Create dummy name of expected F1
		String f1Name = "Exp F1:" + p1LineInfo.name() + "x" + p2LineInfo.name();

		int p1Index = as.getLines().indexOf(p1LineInfo);
		int p2Index = as.getLines().indexOf(p2LineInfo);

		// Create a line for our F1 so we can simulate data for it
		Line f1 = viewSet.getDataSet().createLine(f1Name, true);

		StateTable stateTable = viewSet.getDataSet().getStateTable();

		// For each chromosome
		for (int c=0; c < as.viewCount(); c++)
		{
			// For each marker
			for (int m=0; m < as.markerCount(c); m++)
			{
				// Compare parent states to discover if we have to set a homozygous or heterozygous allele in the f1
				int p1State = as.getState(c, p1Index, m);
				int p2State = as.getState(c, p2Index, m);

				// Set f1 loci where the parents have hets or missing data to missing data
				if (stateTable.isHet(p1State) || stateTable.isHet(p2State) || p1State == 0 || p2State == 0)
					f1.setLoci(c, m, 0);

				// If the parents have homozygous states that match each other set the f1 loci to that state
				else if (p1State == p2State)
					f1.setLoci(c, m, p1State);

				// Otherwise find the stateCode for the het which can be created by combining the two homozygous
				// parent alleles
				else
				{
					int codeForHet = findCodeForHet(stateTable, p1State, p2State);
					f1.setLoci(c, m, codeForHet);
				}
			}
		}

		// Create a LineInfo for the f1 and add it to the viewSet
		f1LineInfo = new LineInfo(f1, viewSet.getDataSet().countLines());
		viewSet.getLines().add(2, f1LineInfo);
	}

	private int findCodeForHet(StateTable stateTable, int p1StateCode, int p2StateCode)
	{
		int codeForHet = -1;

		// We need to create a temporary AlleleState to make use of AlleleState's
		// matches method
		String p1Allele = stateTable.getAlleleState(p1StateCode).homzAllele();
		String p2Allele = stateTable.getAlleleState(p2StateCode).homzAllele();
		AlleleState f1State = new AlleleState(p1Allele + "/" + p2Allele, true, "/");

		// Loop over the table looking for the stateCode of a matching allele
		for (int i=0; i < stateTable.getStates().size(); i++)
		{
			if (f1State.matches(stateTable.getAlleleState(i)))
			{
				codeForHet = i;
				break;
			}
		}

		// If we didn't find a suitable statecode we need to create one and add
		// it to the statetable
		if (codeForHet == -1)
			codeForHet = stateTable.getStateCode(p1Allele + "/" + p2Allele, true, " ", true, "/");

		return codeForHet;
	}

	public LineInfo getF1LineInfo()
	{
		return f1LineInfo;
	}
}