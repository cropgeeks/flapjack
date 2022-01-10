// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.cmd;

import java.io.*;

import jhi.flapjack.io.*;

public class CreateProjectSettings
{
	private File genotypes;
	private File map;
	private File traits;
	private File qtls;
	private FlapjackFile project;
	private String datasetName;

	public CreateProjectSettings(File genotypes, File map, File traits, File qtls, FlapjackFile project, String datasetName)
	{
		this.genotypes = genotypes;
		this.map = map;
		this.traits = traits;
		this.qtls = qtls;
		this.project = project;
		this.datasetName = datasetName;
	}

	File getGenotypes()
		{ return genotypes; }

	void setGenotypes(File genotypes)
		{ this.genotypes = genotypes; }

	File getMap()
		{ return map; }

	void setMap(File map)
		{ this.map = map; }

	File getTraits()
		{ return traits; }

	void setTraits(File traits)
		{ this.traits = traits; }

	File getQtls()
		{ return qtls; }

	void setQtls(File qtls)
		{ this.qtls = qtls; }

	FlapjackFile getProject()
		{ return project; }

	void setProject(FlapjackFile project)
		{ this.project = project; }

	String getDatasetName()
		{ return datasetName; }

	void setDatasetName(String datasetName)
		{ this.datasetName = datasetName; }
}