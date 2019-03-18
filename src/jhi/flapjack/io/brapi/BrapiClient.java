// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.*;
import java.util.zip.*;
import javax.xml.bind.*;

import jhi.flapjack.gui.*;

import jhi.brapi.api.*;
import jhi.brapi.api.allelematrices.*;
import jhi.brapi.api.authentication.*;
import jhi.brapi.api.calls.*;
import jhi.brapi.api.genomemaps.*;
import jhi.brapi.api.markerprofiles.*;
import jhi.brapi.api.studies.*;
import jhi.brapi.client.*;

import okhttp3.*;
import retrofit2.Call;
import retrofit2.Response;
import scri.commons.gui.*;

public class BrapiClient
{
	private RetrofitServiceGenerator generator;
	private RetrofitService service;
	private String baseURL;

	// The resource selected by the user for use
	private XmlResource resource;

	private String username, password;
	private String mapID, studyID, methodID, matrixID;

	private CallsUtils callsUtils;

	private AsyncChecker.AsyncStatus status = AsyncChecker.AsyncStatus.PENDING;

	private volatile boolean isOk = true;

	public void initService()
	{
		baseURL = resource.getUrl();
		baseURL = baseURL.endsWith("/") ? baseURL : baseURL + "/";

		generator = new RetrofitServiceGenerator(baseURL, resource.getCertificate());
		service = generator.generate(null);
	}

	private String enc(String str)
	{
		try
		{
			return URLEncoder.encode(str, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			return str;
		}
	}

	public void getCalls()
		throws Exception
	{
		List<BrapiCall> calls = new ArrayList<>();
		Pager pager = new Pager();

		while (pager.isPaging())
		{
			Response<BrapiListResource<BrapiCall>> response = service.getCalls(null, pager.getPageSize(), pager.getPage())
				.execute();

			if (response.isSuccessful())
			{
				BrapiListResource<BrapiCall> callResponse = response.body();

				calls.addAll(callResponse.data());
				pager.paginate(callResponse.getMetadata());
			}
			else
			{
				String errorMessage = ErrorHandler.getMessage(generator, response);

				throw new Exception(errorMessage);
			}
		}

		callsUtils = new CallsUtils(calls);
	}

	public boolean validateCalls()
	{
		if (callsUtils.validate() == false)
		{
			TaskDialog.errorWithLog("The selected BrAPI service does not appear to support the required functionality "
				+ "for use by Flapjack (" + callsUtils.getExceptionMsg() + ").");

			return false;
		}

		return true;
	}

	public boolean hasToken()
	{
		return callsUtils.hasToken();
	}

	public boolean hasAlleleMatrixSearchTSV()
	{
		return callsUtils.hasAlleleMatrixSearchTSV();
	}

	public boolean hasAlleleMatrixSearchFlapjack()
	{
		return callsUtils.hasAlleleMatrixSearchFlapjack();
	}

	public boolean hasMaps()
	{
		return callsUtils.hasMaps();
	}

	public boolean hasMapsMapDbId()
	{
		return callsUtils.hasMapsMapDbId();
	}

	public boolean hasAlleleMatrices()
	{
		return callsUtils.hasAlleleMatrices();
	}

	public boolean hasStudiesSearchGET()
	{
		return callsUtils.hasStudiesSearchGET();
	}

	public boolean hasStudiesSearchPOST()
	{
		return callsUtils.hasStudiesSearchPOST();
	}

	public boolean doAuthentication()
		throws Exception
	{
		if (username == null && password == null)
			return false;

		BrapiTokenLoginPost tokenPost = new BrapiTokenLoginPost(username, password, "password", "flapjack");

		Response<BrapiSessionToken> response = service.getAuthToken(tokenPost).execute();

		if (response.isSuccessful())
		{
			BrapiSessionToken token = response.body();

			if (token == null)
				return false;

			service = generator.generate(token.getAccess_token());
			return true;
		}
		else
		{
			return false;
		}
	}

	// Returns a list of available maps
	public List<BrapiGenomeMap> getMaps()
		throws Exception
	{
		List<BrapiGenomeMap> mapList = new ArrayList<>();
		Pager pager = new Pager();

		while (pager.isPaging())
		{
			Response<BrapiListResource<BrapiGenomeMap>> response = service.getMaps(null, null, pager.getPageSize(), pager.getPage())
				.execute();

			if (response.isSuccessful())
			{
				BrapiListResource<BrapiGenomeMap> maps = response.body();

				mapList.addAll(maps.data());
				pager.paginate(maps.getMetadata());
			}
			else
			{
				String errorMessage = ErrorHandler.getMessage(generator, response);

				throw new Exception(errorMessage);
			}
		}

		return mapList;
	}

	// Returns the details (markers, chromosomes, positions) for a given map
	public List<BrapiMarkerPosition> getMapMarkerData()
		throws Exception
	{
		List<BrapiMarkerPosition> mapDetailList = new ArrayList<>();
		Pager pager = new Pager();

		while (pager.isPaging())
		{
			Response<BrapiListResource<BrapiMarkerPosition>> response = service.getMapMarkerData(enc(mapID), null, pager.getPageSize(), pager.getPage())
				.execute();

			if (response.isSuccessful())
			{
				BrapiListResource<BrapiMarkerPosition> mapDetails = response.body();

				mapDetailList.addAll(mapDetails.data());
				pager.paginate(mapDetails.getMetadata());
			}
			else
			{
				String errorMessage = ErrorHandler.getMessage(generator, response);

				throw new Exception(errorMessage);
			}

		}

		return mapDetailList;
	}

	public BrapiMapMetaData getMapMetaData()
		throws Exception
	{
		List<BrapiLinkageGroup> linkageGroups = new ArrayList<>();
		BrapiMapMetaData mapMetaData = new BrapiMapMetaData();

		Pager pager = new Pager();

		while (pager.isPaging())
		{
			Response<BrapiBaseResource<BrapiMapMetaData>> response = service.getMapMetaData(enc(mapID)).execute();

			if (response.isSuccessful())
			{
				BrapiBaseResource<BrapiMapMetaData> responseBody = response.body();
				mapMetaData = responseBody.getResult();
				linkageGroups.addAll(mapMetaData.getData());
				pager.paginate(responseBody.getMetadata());
			}
			else
			{
				String errorMessage = ErrorHandler.getMessage(generator, response);

				throw new Exception(errorMessage);
			}
		}
		mapMetaData.setData(linkageGroups);

		return mapMetaData;
	}

	// Returns a list of available studies
	public List<BrapiStudies> getStudies()
		throws Exception
	{
		List<BrapiStudies> studiesList = new ArrayList<>();
		Pager pager = new Pager();

		while (pager.isPaging())
		{
			Response<BrapiListResource<BrapiStudies>> response = service.getStudies("genotype", pager.getPageSize(), pager.getPage())
				.execute();

			if (response.isSuccessful())
			{
				BrapiListResource<BrapiStudies> studies = response.body();

				studiesList.addAll(studies.data());
				pager.paginate(studies.getMetadata());
			}
			else
			{
				String errorMessage = ErrorHandler.getMessage(generator, response);

				throw new Exception(errorMessage);
			}
		}

		return studiesList;
	}

	public List<BrapiStudies> getStudiesByPost()
		throws Exception
	{
		List<BrapiStudies> studiesList = new ArrayList<>();
		Pager pager = new Pager();

		BrapiStudiesPost post = new BrapiStudiesPost();
		post.setStudyType("genotype");

		while (pager.isPaging())
		{
			Response<BrapiListResource<BrapiStudies>> response = service.getStudiesPost(post)
				.execute();

			if (response.isSuccessful())
			{
				BrapiListResource<BrapiStudies> studies = response.body();

				studiesList.addAll(studies.data());
				pager.paginate(studies.getMetadata());
			}
			else
			{
				String errorMessage = ErrorHandler.getMessage(generator, response);

				throw new Exception(errorMessage);
			}

		}

		return studiesList;
	}

	public List<BrapiMarkerProfile> getMarkerProfiles()
		throws Exception
	{
		List<BrapiMarkerProfile> markerProfileList = new ArrayList<>();
		Pager pager = new Pager();

		while (pager.isPaging())
		{
			Response<BrapiListResource<BrapiMarkerProfile>> response = service.getMarkerProfiles(null, studyID, null, null, pager.getPageSize(), pager.getPage())
				.execute();

			if (response.isSuccessful())
			{
				BrapiListResource<BrapiMarkerProfile> markerProfiles = response.body();

				markerProfileList.addAll(markerProfiles.data());
				pager.paginate(markerProfiles.getMetadata());
			}
			else
			{
				String errorMessage = ErrorHandler.getMessage(generator, response);

				throw new Exception(errorMessage);
			}

		}

		return markerProfileList;
	}

	// Returns a list of available matrices
	public List<BrapiAlleleMatrixDataset> getMatrices()
		throws Exception
	{
		List<BrapiAlleleMatrixDataset> alleleMatrixList = new ArrayList<>();
		Pager pager = new Pager();

		while (pager.isPaging())
		{
			Response<BrapiListResource<BrapiAlleleMatrixDataset>> response = service.getMatrices(studyID, pager.getPageSize(), pager.getPage())
				.execute();

			if (response.isSuccessful())
			{
				BrapiListResource<BrapiAlleleMatrixDataset> alleleMatrices = response.body();

				alleleMatrixList.addAll(alleleMatrices.data());
				pager.paginate(alleleMatrices.getMetadata());
			}
			else
			{
				String errorMessage = ErrorHandler.getMessage(generator, response);

				throw new Exception(errorMessage);
			}
		}

		return alleleMatrixList;
	}

	public List<BrapiAlleleMatrix> getAlleleMatrix(List<BrapiMarkerProfile> markerprofiles)
		throws Exception
	{
		List<BrapiAlleleMatrix> alleleMatrixList = new ArrayList<>();
		Pager pager = new Pager();

		List<String> ids = markerprofiles.stream().map(BrapiMarkerProfile::getMarkerprofileDbId).collect(Collectors.toList());

		while (pager.isPaging())
		{
			Response<BrapiBaseResource<BrapiAlleleMatrix>> response = service.getAlleleMatrix(ids, null, null, null, null, null, null, pager.getPageSize(), pager.getPage())
				.execute();

			if (response.isSuccessful())
			{
				BrapiBaseResource<BrapiAlleleMatrix> alleleMatrix = response.body();

				alleleMatrixList.add(alleleMatrix.getResult());
				pager.paginate(alleleMatrix.getMetadata());
			}
			else
			{
				String errorMessage = ErrorHandler.getMessage(generator, response);

				throw new Exception(errorMessage);
			}

		}

		return alleleMatrixList;
	}

	public URI getAlleleMatrixFileByProfiles(List<BrapiMarkerProfile> markerProfiles, String format)
		throws Exception
	{
		List<String> ids = markerProfiles.stream().map(BrapiMarkerProfile::getMarkerprofileDbId).collect(Collectors.toList());

		BrapiAlleleMatrixSearchPost alleleMatrixSearchPost = new BrapiAlleleMatrixSearchPost();
		alleleMatrixSearchPost.setMarkerprofileDbId(ids);
		alleleMatrixSearchPost.setFormat(format);

		Response<BrapiBaseResource<BrapiAlleleMatrix>> response = service.getAlleleMatrix(alleleMatrixSearchPost).execute();

		if (response.isSuccessful() == false)
		{
			response = service.getAlleleMatrix(ids, null, format, null, null, null, null, null, null)
				.execute();
		}

		return handleAlleleMatrixReponse(response);
	}

	// Calls /allelematrix-search?format=flapjack
	public URI getAlleleMatrixFileById()
		throws Exception
	{
		Response<BrapiBaseResource<BrapiAlleleMatrix>> response = service.getAlleleMatrix(matrixID, "flapjack", null, null, null, null, null, null)
			.execute();

		return handleAlleleMatrixReponse(response);
	}

	private URI handleAlleleMatrixReponse(Response<BrapiBaseResource<BrapiAlleleMatrix>> response)
		throws Exception
	{
		if (response.isSuccessful())
		{
			BrapiBaseResource<BrapiAlleleMatrix> alleleMatrix = response.body();

			Status async = AsyncChecker.hasAsyncId(alleleMatrix.getMetadata().getStatus());

			if (async != null)
			{
				return pollAlleleMatrixStatus(async.getMessage());
			}
			else
			{
				if (alleleMatrix.getMetadata().getDatafiles().size() >= 1)
					return new URI(alleleMatrix.getMetadata().getDatafiles().get(0));

				throw new Exception("Resource indicated it wasn't asynchronous and also doesn't contain a datafile in the metadata object.");
			}
		}
		else
		{
			String errorMessage = ErrorHandler.getMessage(generator, response);

			throw new Exception(errorMessage);
		}
	}

	private URI pollAlleleMatrixStatus(String id)
		throws Exception
	{
		Call<BrapiListResource<Object>> statusCall = service.getAlleleMatrixStatus(id);

		// Make an initial call to check the status on the resource
		BrapiListResource<Object> statusPoll = statusCall.execute().body();
		status = AsyncChecker.checkStatus(statusPoll.getMetadata().getStatus());

		// Keep checking until the async call returns anything other than "INPROCESS"
		while ((status == AsyncChecker.AsyncStatus.PENDING || status == AsyncChecker.AsyncStatus.INPROCESS) && isOk)
		{
			// Wait for a second before polling again
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
			}
			// Clone the previous retrofit call so we can call it again
			statusPoll = statusCall.clone().execute().body();
			status = AsyncChecker.checkStatus(statusPoll.getMetadata().getStatus());
		}

		if (!isOk)
			return null;

		// Check if the call finished successfully, if so grab the datafile
		if (status == AsyncChecker.AsyncStatus.FINISHED)
			return new URI(statusPoll.getMetadata().getDatafiles().get(0));

		else if (status == AsyncChecker.AsyncStatus.FAILED)
		{
			String errorMessage = statusPoll.getMetadata().getStatus().stream().map(Status::getMessage).collect(Collectors.joining(", "));
			System.out.println(errorMessage);
			throw new Exception("The BrAPI resource returned the following asynchronous status: " + status.toString());
		}

		else if (status == AsyncChecker.AsyncStatus.UNKNOWN)
			throw new Exception("Unknown asynchronous status returned by BrAPI resource.");

		// IF we've reached this point, there's something really wrong.
		throw new Exception("The BrAPI resource has not returned an asynchronous status that can be understood by Flapjack.");
	}

	public XmlBrapiProvider getBrapiProviders()
		throws Exception
	{
		URL url = new URL("https://ics.hutton.ac.uk/resources/brapi/flapjack-brapi-201903-v1.1.zip");

		File dir = new File(FlapjackUtils.getCacheDir(), "brapi");
		dir.mkdirs();

		// Download the zip file and extract its contents into a temp folder
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(url.openStream()));
		ZipEntry ze = zis.getNextEntry();

		while (ze != null)
		{
			BufferedOutputStream out = new BufferedOutputStream(
				new FileOutputStream(new File(dir, ze.getName())));
			BufferedInputStream in = new BufferedInputStream(zis);

			byte[] b = new byte[4096];
			for (int n; (n = in.read(b)) != -1; )
				out.write(b, 0, n);

			out.close();
			ze = zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();


		// Now read the contents of the XML file
		JAXBContext jaxbContext = JAXBContext.newInstance(XmlBrapiProvider.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		File xml = new File(dir, "brapi.xml");

		return (XmlBrapiProvider) jaxbUnmarshaller.unmarshal(xml);
	}

	public boolean requiresAuthentication()
		throws Exception
	{
		Pager pager = new Pager();

		// Check if the studies call requires authentication
		int responseCode = service.getStudies("genotype", pager.getPageSize(), pager.getPage()).execute().code();

		// 401 and 403 represent the two possible unauthorized / unauthenticated response codes
		return responseCode == 401 || responseCode == 403;
	}

	// Use the okhttp client we configured our retrofit service with. This means
	// the client is configured with any authentication tokens and any custom
	// certificates that may be required to interact with the current BrAPI
	// resource
	InputStream getInputStream(URI uri)
		throws Exception
	{
		// TODO: Don't return the bytestream directly, check status codes wherever we hit URIs
		return generator.getResponse(uri).body().byteStream();
	}

	okhttp3.Response getResponse(URI uri)
		throws Exception
	{
		return generator.getResponse(uri);
	}

	public String currentAsyncStatusMessage()
	{
		return status.toString();
	}

	public void removeAuthHeader()
	{
		service = generator.removeAuthHeader();
	}


	public String getUsername()
		{ return username; }

	public void setUsername(String username)
		{	 this.username = username; }

	public String getPassword()
		{ return password; }

	public void setPassword(String password)
		{ this.password = password; }

	public String getMethodID()
		{ return methodID; }

	public void setMethodID(String methodID)
		{ this.methodID = methodID;	}

	public XmlResource getResource()
		{ return resource; }

	public void setResource(XmlResource resource)
		{ this.resource = resource; }

	public String getMapID()
		{ return mapID; }

	public void setMapID(String mapIndex)
		{ this.mapID = mapIndex; }

	public String getStudyID()
		{ return studyID; }

	public void setStudyID(String studyID)
		{ this.studyID = studyID; }

	public String getMatrixID()
		{ return matrixID; }

	public void setMatrixID(String matrixID)
		{ this.matrixID = matrixID; }

	public void cancel()
	{
		isOk = false;
		generator.cancelAll();
	}
}