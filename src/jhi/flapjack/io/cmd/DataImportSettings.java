// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.cmd;

public class DataImportSettings
{
	private String missingData = "-";
	private String hetSep = "/";
	private boolean collapseHeteozygotes = true;
	private boolean makeAllChrom = false;
	private boolean decimalEnglish = false;
	private boolean transposed = false;
	private boolean allowDuplicates = false;
	private boolean forceNucScheme = false;

	boolean isTransposed()
		{ return transposed; }

	void setTransposed(boolean transposed)
		{ this.transposed = transposed;	}

	boolean isCollapseHeteozygotes()
		{ return collapseHeteozygotes; }

	void setCollapseHeteozygotes(boolean collapseHeteozygotes)
		{ this.collapseHeteozygotes = collapseHeteozygotes; }

	String getMissingData()
		{ return missingData; }

	void setMissingData(String missingData)
		{ this.missingData = missingData; }

	String getHetSep()
		{ return hetSep; }

	void setHetSep(String hetSep)
		{ this.hetSep = hetSep; }

	boolean isMakeAllChrom()
		{ return makeAllChrom; }

	void setMakeAllChrom(boolean makeAllChrom)
		{ this.makeAllChrom = makeAllChrom; }

	boolean isDecimalEnglish()
		{ return decimalEnglish; }

	void setDecimalEnglish(boolean decimalEnglish)
		{ this.decimalEnglish = decimalEnglish; }

	public boolean isAllowDuplicates()
		{ return allowDuplicates; }

	public void setAllowDuplicates(boolean allowDuplicates)
		{ this.allowDuplicates = allowDuplicates; }

	public boolean isForceNucScheme()
		{ return forceNucScheme; }

	public void setForceNucScheme(boolean forceNucScheme)
		{ this.forceNucScheme = forceNucScheme; }
}