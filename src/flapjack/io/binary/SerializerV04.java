// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io.binary;

import java.io.*;
import java.util.*;

import flapjack.data.*;

public class SerializerV04 extends SerializerV03
{
	SerializerV04(DataInputStream in, DataOutputStream out)
		{ super(in, out); }

	@Override
	protected GTView loadGTView(GTViewSet viewSet, int index)
		throws Exception
	{
		GTView view = super.loadGTView(viewSet, index);

		ChromosomeMap map = viewSet.getDataSet().getChromosomeMaps().get(index);

		ArrayList<QTLInfo> qtls = new ArrayList<QTLInfo>();

		// Number of qtlinfos
		int qtlCount = in.readInt();
		// QTLInfo data
		for (int i = 0; i < qtlCount; i++)
			qtls.add(loadQTLInfo(map));

		view.setQTLs(qtls);

		return view;
	}

	protected QTLInfo loadQTLInfo(ChromosomeMap map)
		throws Exception
	{
		int index = in.readInt();
		float mapOffset = in.readFloat();

		QTL qtl = map.getQTLs().get(index);

		QTLInfo qtlInfo = new QTLInfo(qtl, index, mapOffset);

		return qtlInfo;
	}

	@Override
	protected void saveGTView(GTView view)
		throws Exception
	{
		super.saveGTView(view);

		// Number of QTLInfos
		out.writeInt(view.getQTLs().size());
		for (QTLInfo qtlInfo : view.getQTLs())
			saveQTLInfo(qtlInfo);
	}

	protected void saveQTLInfo(QTLInfo qtlInfo)
		throws Exception
	{
		out.writeInt(qtlInfo.getIndex());
		out.writeFloat(qtlInfo.getMapOffset());
	}
}