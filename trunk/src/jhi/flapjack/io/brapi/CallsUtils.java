// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

import java.util.*;

import jhi.brapi.api.calls.*;
import jhi.flapjack.gui.*;

class CallsUtils
{
	private static final String GET = "GET";
	private static final String POST = "POST";
	private static final String JSON = "json";
	private static final String TSV = "tsv";
	private static final String FLAPJACK = "flapjack";

	String exceptionMsg = "";

	private List<BrapiCall> calls;

	CallsUtils(List<BrapiCall> calls)
	{
		this.calls = calls;
	}

	boolean validate()
	{
		// First validate the calls that MUST be present
		if (Prefs.guiBrAPIUseStudies && hasCall("studies-search", JSON, GET) == false)
		{
			exceptionMsg = "/studies-search not implemented";
			return false;
		}

		if (Prefs.guiBrAPIUseMaps && hasCall("maps", JSON, GET) == false)
		{
			exceptionMsg = "/maps not implmented";
			return false;
		}

		if (hasCall("maps/id/positions", JSON, GET) == false)
		{
			exceptionMsg = "maps/id/positions not implmented";
			return false;
		}

		// "v2" flow for genotype data extract (this is our preferred route)
		if (hasAlleleMatrices() && hasAlleleMatrixSearchFlapjack() == false)
		{
			exceptionMsg = "no Flapjack format support in /allelematrix-search";
			return false;
		}

		// or "v1"
		// TODO: Put a proper error messages in the false cases here
		else if (hasCall("markerprofiles", JSON, GET))
			return (hasCall("allelematrix-search", JSON, POST) || hasAlleleMatrixSearchTSV() || hasAlleleMatrixSearchFlapjack());

		return false;
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

	boolean hasAlleleMatrices()
	{
		return hasCall("allelematrices", JSON, GET);
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