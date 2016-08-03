package jhi.flapjack.analysis;

import jhi.flapjack.data.*;

import scri.commons.gui.*;

/**
 * Created by gs40939 on 03/05/2016.
 */
public class SimulateF1 extends SimpleJob
{
	private GTViewSet viewSet;

	private int p1Index;
	private int p2Index;
	private int f1Index;

	public SimulateF1(GTViewSet viewSet, int p1Index, int p2Index)
	{
		this.viewSet = viewSet;
		this.p1Index = p1Index;
		this.p2Index = p2Index;
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
		String f1Name = "Exp F1:" + as.getLines().get(p1Index).name() + "x" + as.getLines().get(p2Index).name();

		// Create a line for our F1 so we can simulate data for it
		Line f1 = viewSet.getDataSet().createLine(f1Name, true);

		StateTable stateTable = viewSet.getDataSet().getStateTable();

		// For each chromosome
		for (int c=0; c < as.viewCount(); c++)
		{
			// Deal with simulating data for normal chromsomes
			if (as.getGTView(c).getChromosomeMap().isSpecialChromosome() == false)
			{
				// For each marker
				for (int m = 0; m < as.markerCount(c); m++)
				{
					// Compare parent states to discover if we have to set a homozygous or heterozygous allele in the f1
					int p1State = as.getState(c, p1Index, m);
					int p2State = as.getState(c, p2Index, m);

					setLociForF1(f1, stateTable, c, m, p1State, p2State);
				}
			}
			// Simulate data for special chromosome
			else
			{
				int specialChromsomeIndex = c;
				int loci = 0;
				int dummyCount = 5;

				for (int view=0; view < as.viewCount()-1; view++)
				{
					for (int m=0; m < as.markerCount(view); m++, loci++)
					{
						// Compare parent states to discover if we have to set a homozygous or heterozygous allele in the f1
						int p1State = as.getState(view, p1Index, m);
						int p2State = as.getState(view, p2Index, m);

						// Set f1 loci where the parents have hets or missing data to missing data
						setLociForF1(f1, stateTable, c, loci, p1State, p2State);
					}
					if (view < specialChromsomeIndex -1)
					{
						for (int d = 0; d < dummyCount; d++, loci++)
							f1.setLoci(c, loci, 0);
					}
				}
			}
		}

		// Create a LineInfo for the f1 and add it to the viewSet
		viewSet.getLines().add(new LineInfo(f1, viewSet.getDataSet().countLines()));
		f1Index = viewSet.getLines().size()-1;
	}

	private void setLociForF1(Line f1, StateTable stateTable, int c, int m, int p1State, int p2State)
	{
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

	public int getF1Index()
	{
		return f1Index;
	}
}