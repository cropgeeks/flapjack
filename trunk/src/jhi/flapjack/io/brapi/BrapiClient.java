// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;
import java.util.zip.*;
import javax.xml.bind.*;

import jhi.flapjack.gui.*;

import jhi.brapi.api.*;
import jhi.brapi.api.authentication.*;
import jhi.brapi.api.calls.*;
import jhi.brapi.api.genomemaps.*;
import jhi.brapi.api.markerprofiles.*;
import jhi.brapi.api.studies.*;
import jhi.brapi.client.*;

import okhttp3.*;

import retrofit2.*;
import retrofit2.converter.jackson.*;

public class BrapiClient
{
	private RetrofitService service;
	private String baseURL;

	// The resource selected by the user for use
	private XmlResource resource;

	private String username, password;
	private String mapID, studyID, methodID;

	private CallsUtils callsUtils;

	private String authToken;

	public void initService()
		throws Exception
	{
		baseURL = resource.getUrl();
		baseURL = baseURL.endsWith("/") ? baseURL : baseURL + "/";

		service = createService(baseURL, null);
	}

	private RetrofitService createService(String baseURL, String authToken)
	{
		Interceptor inter = buildInterceptor(authToken);

		// Tweak to make the timeout on Retrofit connections last longer
		OkHttpClient httpClient = new OkHttpClient.Builder()
			.readTimeout(60, TimeUnit.SECONDS)
			.connectTimeout(60, TimeUnit.SECONDS)
			.addNetworkInterceptor(inter)
			.build();

		return buildService(baseURL, httpClient);
	}

	private Interceptor buildInterceptor(String authToken)
	{
		String bearer = "Bearer %s";

		Interceptor inter = chain ->
		{
			Request original = chain.request();

			// If we already have an authorization token in the header, or we
			// don't have a valid token to add, return the original request
			if (original.header("Authorization") != null || authToken == null || authToken.isEmpty())
				return chain.proceed(original);

			// Otherwise add the header and return the tweaked request
			Request next = original.newBuilder()
				.header("Authorization", String.format(bearer, authToken))
				.build();

			return chain.proceed(next);
		};

		return inter;
	}

	private RetrofitService buildService(String baseURL, OkHttpClient client)
	{
		Retrofit retrofit = new Retrofit.Builder()
			.baseUrl(baseURL)
			.addConverterFactory(JacksonConverterFactory.create())
			.client(client)
			.build();

		return retrofit.create(RetrofitService.class);
	}

	private String enc(String str)
	{
		try { return URLEncoder.encode(str, "UTF-8"); }
		catch (UnsupportedEncodingException e) { return str; }
	}

	public void getCalls()
		throws Exception
	{
		List<BrapiCall> calls = new ArrayList<>();
		Pager pager = new Pager();

		while (pager.isPaging())
		{
			BrapiListResource<BrapiCall> br = service.getCalls(null, pager.getPageSize(), pager.getPage())
				.execute()
				.body();

			calls.addAll(br.data());

			pager.paginate(br.getMetadata());
		}

		callsUtils = new CallsUtils(calls);

		if (callsUtils.validate() == false)
			throw new Exception("/calls not valid");
	}

	public boolean hasToken()
		{ return callsUtils.hasToken(); }

	public boolean hasAlleleMatrixSearchTSV()
		{ return callsUtils.hasAlleleMatrixSearchTSV(); }

	public boolean hasAlleleMatrixSearchFlapjack()
		{ return callsUtils.hasAlleleMatrixSearchFlapjack(); }

	public boolean hasMapsMapDbId()
		{ return callsUtils.hasMapsMapDbId(); }

	public boolean doAuthentication()
		throws Exception
	{
		if (username == null && password == null)
			return false;

		BrapiTokenPost tokenPost = new BrapiTokenPost(enc(username), enc(password), "password", "flapjack");

		BrapiSessionToken token = service.getAuthToken(tokenPost)
			.execute()
			.body();

		if (token == null)
			return false;

		authToken = token.getSessionToken();

		service = createService(baseURL, token.getSessionToken());

		return true;
	}

	// Returns a list of available maps
	public List<BrapiGenomeMap> getMaps()
		throws Exception
	{
		List<BrapiGenomeMap> list = new ArrayList<>();
		Pager pager = new Pager();

		while (pager.isPaging())
		{
			BrapiListResource<BrapiGenomeMap> br = service.getMaps(null, null, pager.getPageSize(), pager.getPage())
				.execute()
				.body();

			list.addAll(br.data());

			pager.paginate(br.getMetadata());
		}

		return list;
	}

	// Returns the details (markers, chromosomes, positions) for a given map
	public List<BrapiMarkerPosition> getMapMarkerData()
		throws Exception
	{
		List<BrapiMarkerPosition> list = new ArrayList<>();
		Pager pager = new Pager();

		while (pager.isPaging())
		{
			BrapiListResource<BrapiMarkerPosition> br = service.getMapMarkerData(enc(mapID), null, pager.getPageSize(), pager.getPage())
				.execute()
				.body();

			list.addAll(br.data());

			pager.paginate(br.getMetadata());
		}

		return list;
	}

	public BrapiMapMetaData getMapMetaData()
		throws Exception
	{
		BrapiBaseResource<BrapiMapMetaData> br = service.getMapMetaData(enc(mapID))
			.execute()
			.body();

		return br.getResult();
	}

	// Returns a list of available studies
	public List<BrapiStudies> getStudies()
		throws Exception
	{
		List<BrapiStudies> list = new ArrayList<>();
		Pager pager = new Pager();

		while (pager.isPaging())
		{
			BrapiListResource<BrapiStudies> br = service.getStudies("genotype", pager.getPageSize(), pager.getPage())
				.execute()
				.body();

			list.addAll(br.data());

			pager.paginate(br.getMetadata());
		}

		return list;
	}

	public List<BrapiStudies> getStudiesByPost()
		throws Exception
	{
		List<BrapiStudies> list = new ArrayList<>();
		Pager pager = new Pager();

		BrapiStudiesPost post = new BrapiStudiesPost();
		post.setStudyType("genotype");

		while (pager.isPaging())
		{
			BrapiListResource<BrapiStudies> br = service.getStudiesPost(post)
				.execute()
				.body();

			list.addAll(br.data());

			pager.paginate(br.getMetadata());
		}

		return list;
	}

	public List<BrapiMarkerProfile> getMarkerProfiles()
		throws Exception
	{
		List<BrapiMarkerProfile> list = new ArrayList<>();
		Pager pager = new Pager();

		while (pager.isPaging())
		{
			BrapiListResource<BrapiMarkerProfile> br = service.getMarkerProfiles(null, studyID, null, null, pager.getPageSize(), pager.getPage())
				.execute()
				.body();

			list.addAll(br.data());

			pager.paginate(br.getMetadata());
		}

		return list;
	}


	public List<BrapiAlleleMatrix> getAlleleMatrix(List<BrapiMarkerProfile> markerprofiles)
		throws Exception
	{
		List<BrapiAlleleMatrix> list = new ArrayList<>();
		Pager pager = new Pager();

		List<String> ids = markerprofiles.stream().map(BrapiMarkerProfile::getMarkerProfileDbId).collect(Collectors.toList());

		while (pager.isPaging())
		{
			BrapiBaseResource<BrapiAlleleMatrix> br = service.getAlleleMatrix(ids, null, null, null, null, null, null, pager.getPageSize(), pager.getPage())
				.execute()
				.body();

			list.add(br.getResult());

			pager.paginate(br.getMetadata());
		}

		return list;
	}

	private URI getAlleleMatrixFile(List<BrapiMarkerProfile> markerProfiles, String format)
		throws Exception
	{
		List<String> ids = markerProfiles.stream().map(BrapiMarkerProfile::getMarkerProfileDbId).collect(Collectors.toList());

		BrapiBaseResource<BrapiAlleleMatrix> br = service.getAlleleMatrix(ids, null, format, null, null, null, null, null, null)
			.execute()
			.body();

		Metadata md = br.getMetadata();
		List<String> files = md.getDatafiles();

		return new URI(files.get(0));
	}

	public URI getAlleleMatrixTSV(List<BrapiMarkerProfile> markerprofiles)
		throws Exception
	{
		return getAlleleMatrixFile(markerprofiles, "tsv");
	}

	public URI getAlleleMatrixFlapjack(List<BrapiMarkerProfile> markerProfiles)
		throws Exception
	{
		return getAlleleMatrixFile(markerProfiles, "flapjack");
	}

	public XmlBrapiProvider getBrapiProviders()
		throws Exception
	{
		URL url = new URL("https://ics.hutton.ac.uk/resources/brapi/brapi.zip");

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
			for (int n; (n = in.read(b)) != -1;)
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


	public String getUsername()
	{ return username; }

	public void setUsername(String username)
	{ this.username = username; }

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

//	private static void initCertificates(Client client, XmlResource resource)
//		throws Exception
//	{
//		if (resource.getCertificate() == null)
//			return;
//
//		// Download the "trusted" certificate needed for this resource
//		URLConnection yc = new URL(resource.getCertificate()).openConnection();
//
//		CertificateFactory cf = CertificateFactory.getInstance("X.509");
//		InputStream in = new BufferedInputStream(yc.getInputStream());
//		java.security.cert.Certificate cer;
//		try {
//			cer = cf.generateCertificate(in);
//		} finally { in.close();	}
//
//		// Create a KeyStore to hold the certificate
//		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//		keyStore.load(null, null);
//		keyStore.setCertificateEntry("cer", cer);
//
//		// Create a TrustManager that trusts the certificate in the KeyStore
//		String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//		TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//		tmf.init(keyStore);
//
//		// Create an SSLContext that uses the TrustManager
//		SSLContext sslContext = SSLContext.getInstance("TLS");
//		sslContext.init(null, tmf.getTrustManagers(), null);
//
//		// Then *finally*, apply the TrustManager info to Restlet
//		client.setContext(new Context());
//		Context context = client.getContext();
//
//		context.getAttributes().put("sslContextFactory", new SslContextFactory() {
//		    public void init(Series<Parameter> parameters) { }
//		   	public SSLContext createSslContext() throws Exception { return sslContext; }
//		});
//	}

	// For cases where we need to use a standard http connection or similar,
	// this method can be used to grab the authorization token and add it to
	// the URLConnection
	URLConnection addAuthTokenToConnection(URLConnection connection)
	{
		if (authToken != null && !authToken.isEmpty())
			connection.setRequestProperty("Authorization", "Bearer " + authToken);

		return connection;
	}
}