// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;
import javax.xml.bind.*;

import jhi.flapjack.gui.*;

import jhi.brapi.api.*;
import jhi.brapi.api.authentication.*;
import jhi.brapi.api.core.serverinfo.*;
import jhi.brapi.api.core.studies.*;
import jhi.brapi.api.genotyping.genomemaps.*;
import jhi.brapi.client.*;

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
		BrapiServerInfo serverInfo = new BrapiServerInfo();

		Response<BrapiBaseResource<BrapiServerInfo>> response = service.getServerInfo(null)
			.execute();

		if (response.isSuccessful())
		{
			BrapiBaseResource<BrapiServerInfo> serverInfoResponse = response.body();

			serverInfo = serverInfoResponse.getResult();
		}
		else
		{
			String errorMessage = ErrorHandler.getMessage(generator, response);

			throw new Exception(errorMessage);
		}

		callsUtils = new CallsUtils(serverInfo.getCalls());
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
	public List<GenomeMap> getMaps()
		throws Exception
	{
		List<GenomeMap> mapList = new ArrayList<>();
		Pager pager = new Pager();

		while (pager.isPaging())
		{
			Response<BrapiListResource<GenomeMap>> response = service.getMaps(null, null, null, null, null, null, null, pager.getPageSize(), pager.getPage())
				.execute();

			if (response.isSuccessful())
			{
				BrapiListResource<GenomeMap> maps = response.body();

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
	public List<MarkerPosition> getMapMarkerData()
		throws Exception
	{
		List<MarkerPosition> markerPositionList = new ArrayList<>();
		Pager pager = new Pager();

		while (pager.isPaging())
		{
			Response<BrapiListResource<MarkerPosition>> response = service.getMarkerPositions(enc(mapID), null, null, null, null, pager.getPageSize(), pager.getPage())
				.execute();

			if (response.isSuccessful())
			{
				BrapiListResource<MarkerPosition> markerPositions = response.body();

				markerPositionList.addAll(markerPositions.data());
				pager.paginate(markerPositions.getMetadata());
			}
			else
			{
				String errorMessage = ErrorHandler.getMessage(generator, response);

				throw new Exception(errorMessage);
			}

		}

		return markerPositionList;
	}

	public GenomeMap getMapMetaData()
		throws Exception
	{
		GenomeMap genomeMap = new GenomeMap();

		Pager pager = new Pager();

		Response<BrapiBaseResource<GenomeMap>> response = service.getMapById(enc(mapID), pager.getPageSize(), pager.getPage()).execute();

		if (response.isSuccessful())
		{
			BrapiBaseResource<GenomeMap> responseBody = response.body();
			genomeMap = responseBody.getResult();
		}
		else
		{
			String errorMessage = ErrorHandler.getMessage(generator, response);

			throw new Exception(errorMessage);
		}

		return genomeMap;
	}

	// Returns a list of available studies
	public List<Study> getStudies()
		throws Exception
	{
		List<Study> studiesList = new ArrayList<>();
		Pager pager = new Pager();

		while (pager.isPaging())
		{
			Response<BrapiListResource<Study>> response = service.getStudies(null, "genotype", null, null,
				null, null, null, null, null, null, null, null, null, null,
				null,  pager.getPageSize(), pager.getPage())
				.execute();

			if (response.isSuccessful())
			{
				BrapiListResource<Study> studies = response.body();

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

	public List<Study> getStudiesByPost()
		throws Exception
	{
		List<Study> studiesList = new ArrayList<>();
		Pager pager = new Pager();

		BrapiStudiesPost post = new BrapiStudiesPost();
		post.setStudyTypes(Collections.singletonList("genotype"));

		while (pager.isPaging())
		{
			Response<BrapiListResource<Study>> response = service.getStudiesPost(post)
				.execute();

			if (response.isSuccessful())
			{
				BrapiListResource<Study> studies = response.body();

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
		int responseCode = service.getStudies(null, "genotype", null, null,
			null, null, null, null, null, null, null, null, null, null,
			null,  pager.getPageSize(), pager.getPage())
			.execute().code();

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