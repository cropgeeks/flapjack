// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

import java.util.*;
import java.util.stream.Collectors;

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
		if (Prefs.guiBrAPIUseStudies && hasStudiesSearchGET() == false && hasStudiesSearchPOST() == false)
		{
			exceptionMsg = "studies-search not implemented";
			return false;
		}

		if (Prefs.guiBrAPIUseMaps && hasCall("maps", JSON, GET) == false)
		{
			exceptionMsg = "maps not implmented";
			return false;
		}

		if (Prefs.guiBrAPIUseMaps && hasCall("maps/{id}/positions", JSON, GET) == false)
		{
			exceptionMsg = "maps/{id}/positions not implmented";
			return false;
		}

		// "v2" flow for genotype data extract (this is our preferred route)
		if (hasAlleleMatrices())
		{
			if (hasAlleleMatrixSearchFlapjack() == false)
			{
				exceptionMsg = "no Flapjack format support in allelematrix-search";
				return false;
			}

			return true;
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
			if (call.getCall().equals(signature) && call.hasDataType(datatype) && call.hasMethod(method))
				return true;

		return false;
	}

	boolean hasToken()
	{
		return hasCall("token", JSON, POST);
	}

	boolean hasMapsMapDbId()
	{
		return hasCall("maps/{id}", JSON, GET);
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

	boolean hasStudiesSearchGET()
		{ return hasCall("studies-search", JSON, GET); }

	boolean hasStudiesSearchPOST()
		{ return hasCall("studies-search", JSON, POST); }
}