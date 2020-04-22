// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

import java.util.*;

import jhi.brapi.api.core.serverinfo.*;

class CallsUtils
{
	private static final String GET = "GET";
	private static final String POST = "POST";
	private static final String JSON = "application/json";
	private static final String TSV = "tsv";
	private static final String FLAPJACK = "flapjack";

	private String exceptionMsg = "";

	private List<BrapiCall> calls;

	CallsUtils(List<BrapiCall> calls)
	{
		this.calls = calls;
	}

	boolean validate()
	{
		// First validate the calls that MUST be present
		if (hasStudiesSearch() == false)
		{
			exceptionMsg = "studies not implemented";
			return false;
		}

		// TODO: BrAPI v2 calls

		return true;
	}

	boolean hasCall(String signature, String datatype, String method)
	{
		for (BrapiCall call : calls)
			if (call.getService().equals(signature) && call.hasDataType(datatype) && call.hasMethod(method))
				return true;

		return false;
	}

	boolean hasToken()
	{
		return hasCall("token", JSON, POST);
	}

	boolean hasMaps()
	{
		return hasCall("maps", JSON, GET);
	}

	boolean hasMapsMapDbId()
	{
		return hasCall("maps/{id}", JSON, GET);
	}

	boolean hasStudiesSearch()
		{ return hasCall("studies", JSON, GET); }

	public String getExceptionMsg()
		{ return exceptionMsg; }
}