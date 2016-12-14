// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

import java.util.*;

import jhi.brapi.api.calls.*;

class CallsUtils
{
	private static final String GET = "GET";
	private static final String POST = "POST";
	private static final String JSON = "json";
	private static final String TSV = "tsv";
	private static final String FLAPJACK = "flapjack";

	private List<BrapiCall> calls;

	CallsUtils(List<BrapiCall> calls)
	{
		this.calls = calls;
	}

	boolean validate()
	{
		// First validate the calls that MUST be present
		if (hasCall("studies-search", JSON, GET) == false)
			return false;
		if (hasCall("maps", JSON, GET) == false)
			return false;
		if (hasCall("maps/id/positions", JSON, GET) == false)
			return false;
		if (hasCall("markerprofiles", JSON, GET) == false)
			return false;
		if (hasCall("allelematrix-search", JSON, POST) == false
			&& hasAlleleMatrixSearchTSV() == false
			&&  hasAlleleMatrixSearchFlapjack() == false)
			return false;

		return true;
	}

	boolean hasCall(String signature, String datatype, String method)
	{
		for (BrapiCall call : calls)
			if (call.getCall().equals(signature) && call.getDatatypes().contains(datatype) && call.getMethods().contains(method))
				return true;

		return false;
	}

	boolean hasToken()
	{
		return hasCall("token", JSON, GET);
	}

	boolean hasMapsMapDbId()
	{
		return hasCall("maps/id", JSON, GET);
	}

	boolean hasAlleleMatrixSearchTSV()
	{
		return hasCall("allelematrix-search", TSV, POST);
	}

	boolean hasAlleleMatrixSearchFlapjack()
	{
		return hasCall("allelematrix-search", FLAPJACK, POST);
	}
}