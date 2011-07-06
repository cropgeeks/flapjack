// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.io.binary;

import java.io.*;
import java.util.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.io.*;

// V3 serialization adds support for experiments in traits and changes what used
// to be a single integer for the selected graph in a GTViewSet to be an array
// of three ints (stored as a string).

class SerializerV03 extends SerializerV02
{
	SerializerV03(DataInputStream in, DataOutputStream out)
		{ super(in, out); }

	protected void saveTrait(Trait trait)
		throws Exception
	{
		super.saveTrait(trait);

		// Experiment
		writeString(trait.getExperiment());
	}

	protected Trait loadTrait(DataSet dataSet)
		throws Exception
	{
		Trait trait = super.loadTrait(dataSet);

		// Experiment
		trait.setExperiment(readString());

		return trait;
	}

	protected void saveGTViewSet(GTViewSet viewSet)
		throws Exception
	{
		super.saveGTViewSet(viewSet);

		// Selected graphs
		writeString(viewSet.getSelectedGraphs());
	}

	protected GTViewSet loadGTViewSet(DataSet dataSet)
		throws Exception
	{
		GTViewSet viewSet = super.loadGTViewSet(dataSet);

		// Selected graphs
		viewSet.setSelectedGraphs(readString());

		return viewSet;
	}
}