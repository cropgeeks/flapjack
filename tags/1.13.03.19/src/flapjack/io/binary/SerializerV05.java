// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io.binary;

import java.io.*;
import java.util.*;

import flapjack.data.*;

public class SerializerV05 extends SerializerV04
{
	SerializerV05(DataInputStream in, DataOutputStream out)
		{ super(in, out); }

	@Override
	protected void saveLineInfo(LineInfo lineInfo)
		throws Exception
	{
		super.saveLineInfo(lineInfo);

		// Duplicate line?
		out.writeBoolean(lineInfo.getDuplicate());
	}

	protected LineInfo loadLineInfo(DataSet dataSet)
		throws Exception
	{
		LineInfo lineInfo = super.loadLineInfo(dataSet);

		// Duplicate line?
		lineInfo.setDuplicate(in.readBoolean());

		return lineInfo;
	}
}