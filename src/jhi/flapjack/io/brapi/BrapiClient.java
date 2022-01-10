// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.*;
import javax.xml.bind.*;

import jhi.flapjack.gui.*;

import retrofit2.Response;
import scri.commons.gui.*;
import uk.ac.hutton.ics.brapi.client.*;
import uk.ac.hutton.ics.brapi.resource.base.*;
import uk.ac.hutton.ics.brapi.resource.core.serverinfo.ServerInfo;
import uk.ac.hutton.ics.brapi.resource.core.study.Study;
import uk.ac.hutton.ics.brapi.resource.genotyping.call.*;
import uk.ac.hutton.ics.brapi.resource.genotyping.map.*;
import uk.ac.hutton.ics.brapi.resource.genotyping.map.Map;
import uk.ac.hutton.ics.brapi.resource.genotyping.variant.VariantSet;

public class BrapiClient
{
	private RetrofitServiceGenerator generator;
	private BrapiCoreService coreService;
	private BrapiGenotypingService genotypeService;
	private TokenService tokenService;
	String baseURL;

	// The resource selected by the user for use
	private XmlResource resource;

	private String username, password;
	private String mapID, studyID, variantSetID, variantSetName;
	private long totalMarkers, totalLines;
	private String ioMissingData, ioHeteroSeparator;

	private CallsUtils callsUtils;

	private AsyncChecker.AsyncStatus status = AsyncChecker.AsyncStatus.PENDING;

	private volatile boolean isOk = true;

	// Used for matrix/json streaming to track the objects we've received
	private HashMap<String,String> markers, lines;
	private long alleles = 0;

	public void initService()
	{
		baseURL = resource.getUrl();
		baseURL = baseURL.endsWith("/") ? baseURL : baseURL + "/";

		generator = new RetrofitServiceGenerator(baseURL, resource.getCertificate());
		genotypeService = generator.generateGenotype(null);
		coreService = generator.generateCore(null);
		tokenService = generator.generateToken();
	}

	private String enc(String str)
	{
		return URLEncoder.encode(str, StandardCharsets.UTF_8);
	}

	public void getServerInfo()
		throws Exception
	{
		ServerInfo serverInfo = new ServerInfo();

		Response<BaseResult<ServerInfo>> response = coreService.getServerInfo(null, 0, Integer.MAX_VALUE)
																   .execute();

		if (response.isSuccessful())
		{
			BaseResult<ServerInfo> serverInfoResponse = response.body();

			serverInfo = serverInfoResponse.getResult();

			if (serverInfo == null)
				throwError(0, null);
		}
		else
			throwError(response.code(), response.message());

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

	public boolean hasMaps()
	{
		return callsUtils.hasMaps();
	}

	public boolean hasMapsMapDbId()
	{
		return callsUtils.hasMapsMapDbId();
	}

	public boolean hasStudiesSearch()
	{
		return callsUtils.hasStudiesSearch();
	}

	public boolean doAuthentication()
		throws Exception
	{
		if (username == null && password == null)
			return false;

		BrapiTokenLoginPost tokenPost = new BrapiTokenLoginPost(username, password, "password", "flapjack");

		Response<BrapiSessionToken> response = tokenService.getAuthToken(tokenPost).execute();

		if (response.isSuccessful())
		{
			BrapiSessionToken token = response.body();

			if (token == null)
				return false;

			genotypeService = generator.generateGenotype(token.getAccess_token());
			coreService = generator.generateCore(token.getAccess_token());

			return true;
		}

		return false;
	}

	// Returns a list of available maps
	public List<Map> getMaps()
		throws Exception
	{
		List<Map> mapList = new ArrayList<>();
		Pager pager = new Pager();

		while (pager.isPaging())
		{
			Response<BaseResult<ArrayResult<Map>>> response = genotypeService.getMaps(null, null, null, null, null, null, null, null, pager.getPage(), pager.getPageSize())
																 .execute();

			if (response.isSuccessful())
			{
				BaseResult<ArrayResult<Map>> maps = response.body();

				if (maps == null)
					throwError(0, null);

				mapList.addAll(maps.getResult().getData());
				pager.paginate(maps.getMetadata());
			}
			else
				throwError(response.code(), response.message());
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
			Response<BaseResult<ArrayResult<MarkerPosition>>> response = genotypeService.getMarkerPositions(enc(mapID), null, null, null, null, pager.getPage(), pager.getPageSize())
																			.execute();

			if (response.isSuccessful())
			{
				BaseResult<ArrayResult<MarkerPosition>> markerPositions = response.body();

				if (markerPositions == null)
					throwError(0, null);

				markerPositionList.addAll(markerPositions.getResult().getData());
				pager.paginate(markerPositions.getMetadata());
			}
			else
				throwError(response.code(), response.message());
		}

		return markerPositionList;
	}

	// Returns a list of available studies
	public List<Study> getStudies()
		throws Exception
	{
		List<Study> studiesList = new ArrayList<>();
		Pager pager = new Pager();

		while (pager.isPaging())
		{
			Response<BaseResult<ArrayResult<Study>>> response = coreService.getStudies(null, "genotype", null, null,
				null, null, null, null, null, null, null, null, null, null, null, null,
				null, pager.getPage(), pager.getPageSize())
																   .execute();

			if (response.isSuccessful())
			{
				BaseResult<ArrayResult<Study>> studies = response.body();

				if (studies == null)
					throwError(0, null);

				studiesList.addAll(studies.getResult().getData());
				pager.paginate(studies.getMetadata());
			}
			else
				throwError(response.code(), response.message());
		}

		return studiesList;
	}

	public List<Study> getStudiesByPost()
		throws Exception
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public List<CallSet> getCallsets()
		throws Exception
	{
		List<CallSet> callsetList = new ArrayList<>();
		Pager pager = new Pager();

		while (pager.isPaging())
		{
			Response<BaseResult<ArrayResult<CallSet>>> response = genotypeService.getCallsets(null, null, studyID, null, null, pager.getPage(), pager.getPageSize())
																	 .execute();

			if (response.isSuccessful())
			{
				BaseResult<ArrayResult<CallSet>> callset = response.body();

				if (callset == null)
					throwError(0, null);

				callsetList.addAll(callset.getResult().getData());
				pager.paginate(callset.getMetadata());
			}
			else
				throwError(response.code(), response.message());
		}

		return callsetList;
	}

	public List<VariantSet> getVariantSets()
		throws Exception
	{
		List<VariantSet> vList = new ArrayList<>();
		Pager pager = new Pager();

		while (pager.isPaging())
		{
			Response<BaseResult<ArrayResult<VariantSet>>> response = genotypeService.getVariantSets(null, null, null, studyID, null, pager.getPage(), pager.getPageSize())
																		.execute();

			if (response.isSuccessful())
			{
				BaseResult<ArrayResult<VariantSet>> variantSet = response.body();

				if (variantSet == null)
					throwError(0, null);

				vList.addAll(variantSet.getResult().getData());
				pager.paginate(variantSet.getMetadata());
			}
			else
				throwError(response.code(), response.message());
		}

		return vList;
	}

	public VariantSet getVariantSet()
		throws Exception
	{
		VariantSet variantSet = null;

		Response<BaseResult<VariantSet>> response = genotypeService.getVariantSetById(variantSetID)
																   .execute();
		if (response.isSuccessful())
		{
			BaseResult<VariantSet> r = response.body();

			variantSet = r.getResult();

			if (variantSet == null)
				throwError(0, null);
		}
		else
			throwError(response.code(), response.message());

		return variantSet;
	}

	private void throwError(int code, String responseMessage)
		throws Exception
	{
		if (responseMessage != null)
			throw new Exception("There was an error communicating with the server:\n  HTTP status code: "
				+ code+ "\n  HTTP status msg:  " + responseMessage);
		else
			throw new Exception("Flapjack could not process the data returned by the server.");
	}

	// Returns a list of CallSetCallsDetail objects, where each object defines
	// an intersection of line/marker (and hence allele) information
	//
	// BRAPI: /variantsets/{variantSetDbId}/calls
	public HashMap<String,String> getCallSetCallsDetails(File cacheFile)
		throws Exception
	{
		// Reset counters
		markers = new HashMap<String,String>();
		lines = new HashMap<String,String>();
		alleles = 0;

		BufferedWriter out = new BufferedWriter(new FileWriter(cacheFile));

//		List<CallSetCallsDetail> list = new ArrayList<>();
		Pager pager = new Pager();

		int page = 0;
		while (pager.isPaging() && isOk)
		{
			// Be VERY careful here with the page token pagination method
			// We only have the *current* page's infomation, so the next page we
			// need to ask for is pager.getNextPageToken() even though the
			// method parameter itself is just called pageToken
			Response<TokenBaseResult<CallResult<Call>>> response = genotypeService.getVariantSetByIdCalls(variantSetID, null, null, null, null, pager.getNextPageToken(), pager.getPageSize())
																									 .execute();

			if (response.isSuccessful())
			{
				TokenBaseResult<CallResult<Call>> r = response.body();

//				list.addAll(r.getResult().getData());
				pager.paginate(r.getMetadata());

				// Cache each line/marker/allele intersection to disk
				for (Call detail: r.getResult().getData())
				{
					if (detail == null)
						throwError(0, null);

					lines.put(detail.getCallSetName(), "");
					markers.put(detail.getVariantName(), "");
					alleles++;

					out.write(detail.getCallSetName() + "\t"
						+ detail.getVariantName() + "\t"
						+ detail.getGenotype().getValues().get(0));
					out.newLine();
				}

/*				System.out.println("data paged:");
				System.out.println("  getNextPageToken: " + pager.getNextPageToken());
				System.out.println("  getPageToken:     " + pager.getPageToken());
				System.out.println("  getPrevPageToken: " + pager.getPrevPageToken());
				System.out.println("  getPageSize:      " + pager.getPageSize());
				System.out.println("  getTotalPages:    " + pager.getTotalPages());

				System.out.println("list size is " + list.size());
*/
				ioHeteroSeparator = r.getResult().getSepUnphased();
				ioMissingData = r.getResult().getUnknownString();

				System.out.println("Processed page " + (++page) + " out of " + pager.getTotalPages());
			}
			else
				throwError(response.code(), response.message());
		}

		out.close();

		return markers;
	}

	public XmlBrapiProvider getBrapiProviders()
		throws Exception
	{
		URL url = new URL("https://ics.hutton.ac.uk/resources/brapi/flapjack-brapi-202103-v2.0.zip");

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
		int responseCode = coreService.getStudies(null, "genotype", null, null,
			null, null, null, null, null, null, null, null, null, null, null, null, null,
			pager.getPage(), pager.getPageSize())
										  .execute().code();

		// 401 and 403 represent the two possible unauthorized / unauthenticated response codes
		return responseCode == 401 || responseCode == 403;
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
		genotypeService = generator.removeGenotypeAuthHeader();
		coreService = generator.removeCoreAuthHeader();
	}


	public String getUsername()
		{ return username; }

	public void setUsername(String username)
		{	 this.username = username; }

	public String getPassword()
		{ return password; }

	public void setPassword(String password)
		{ this.password = password; }

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

	public String getVariantSetID()
		{ return variantSetID; }

	public void setVariantSetID(String variantSetID)
		{ this.variantSetID = variantSetID; }

	public String getVariantSetName()
		{ return variantSetName; }

	public void setVariantSetName(String variantSetName)
		{ this.variantSetName = variantSetName; }

	public long getTotalMarkers()
	{
		return totalMarkers;
	}

	public void setTotalMarkers(long totalMarkers)
	{
		this.totalMarkers = totalMarkers;
	}

	public long getTotalLines()
	{
		return totalLines;
	}

	public void setTotalLines(long totalLines)
	{
		this.totalLines = totalLines;
	}

	public void cancel()
	{
		isOk = false;
		generator.cancelAll();
	}

	public String getIoMissingData()
		{ return ioMissingData; }

	public String getIoHeteroSeparator()
		{ return ioHeteroSeparator; }

	public int jsonMarkerCount()
		{ return markers.size(); }

	public int jsonLineCount()
		{ return lines.size(); }

	public long jsonAlleleCount()
		{ return alleles; }
}