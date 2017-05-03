package jhi.flapjack.io.cmd;

class DataImportSettings
{
	private String missingData = "-";
	private String hetSep = "/";
	private boolean collapseHeteozygotes = true;
	private boolean useHetSep = true;
	private boolean makeAllChrom = false;
	private boolean decimalEnglish = false;
	private boolean transposed = false;

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

	boolean isUseHetSep()
		{ return useHetSep; }

	void setUseHetSep(boolean useHetSep)
		{ this.useHetSep = useHetSep; }

	boolean isMakeAllChrom()
		{ return makeAllChrom; }

	void setMakeAllChrom(boolean makeAllChrom)
		{ this.makeAllChrom = makeAllChrom; }

	boolean isDecimalEnglish()
		{ return decimalEnglish; }

	void setDecimalEnglish(boolean decimalEnglish)
		{ this.decimalEnglish = decimalEnglish; }
}