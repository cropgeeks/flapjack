package jhi.flapjack.io.brapi;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.concurrent.*;

import com.fasterxml.jackson.databind.*;
import okhttp3.*;
import okhttp3.Response;

import retrofit2.*;
import retrofit2.converter.jackson.*;
import uk.ac.hutton.ics.brapi.client.*;

public class RetrofitServiceGenerator
{
	private final String baseURL;
	private final String certificate;

	private OkHttpClient httpClient;

	private Retrofit retrofit;

	private Interceptor authHeader;

	public RetrofitServiceGenerator(String baseURL, String certificate)
	{
		this.baseURL = baseURL;
		this.certificate = certificate;
	}

	public BrapiGenotypingService generateGenotype(String authToken)
	{
		authHeader = buildInterceptor(authToken);

		// Tweak to make the timeout on Retrofit connections last longer
		httpClient = new OkHttpClient.Builder()
				.readTimeout(60, TimeUnit.SECONDS)
				.connectTimeout(60, TimeUnit.SECONDS)
				.addNetworkInterceptor(authHeader)
				.build();

		// If the resource has an associated certificate, ensure it is in the
		// trust manager and keystore
		try
		{
			httpClient = initCertificate(httpClient, certificate);
		}
		catch (Exception e) { e.printStackTrace(); }

		return buildGenotypeService(baseURL, httpClient);
	}

	public BrapiCoreService generateCore(String authToken)
	{
		authHeader = buildInterceptor(authToken);

		// Tweak to make the timeout on Retrofit connections last longer
		httpClient = new OkHttpClient.Builder()
			.readTimeout(60, TimeUnit.SECONDS)
			.connectTimeout(60, TimeUnit.SECONDS)
			.addNetworkInterceptor(authHeader)
			.build();

		// If the resource has an associated certificate, ensure it is in the
		// trust manager and keystore
		try
		{
			httpClient = initCertificate(httpClient, certificate);
		}
		catch (Exception e) { e.printStackTrace(); }

		return buildCoreService(baseURL, httpClient);
	}

	public BrapiGenotypingService removeGenotypeAuthHeader()
	{
		OkHttpClient.Builder builder = httpClient.newBuilder();
		builder.networkInterceptors().remove(authHeader);

		httpClient = builder.build();

		return buildGenotypeService(baseURL, httpClient);
	}

	public BrapiCoreService removeCoreAuthHeader()
	{
		OkHttpClient.Builder builder = httpClient.newBuilder();
		builder.networkInterceptors().remove(authHeader);

		httpClient = builder.build();

		return buildCoreService(baseURL, httpClient);
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

	private OkHttpClient initCertificate(OkHttpClient client, String certificate)
			throws Exception
	{
		if (certificate == null || certificate.isEmpty())
			return client;

		// Deal with self signed certificates
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		InputStream caInput = new URL(certificate).openStream();
		Certificate ca;
		ca = cf.generateCertificate(caInput);

		String keyStoreType = KeyStore.getDefaultType();
		KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		keyStore.load(null, null);
		keyStore.setCertificateEntry("ca", ca);

		String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
		tmf.init(keyStore);

		SSLContext sslContext = SSLContext.getInstance("SSL");
		sslContext.init(null, tmf.getTrustManagers(), null);

		client = client.newBuilder()
				.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager)tmf.getTrustManagers()[0])
				.hostnameVerifier((s, sslSession) -> true)
				.build();

		caInput.close();

		return client;
	}

	private BrapiGenotypingService buildGenotypeService(String baseURL, OkHttpClient client)
	{
		ObjectMapper mapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		this.retrofit = (new retrofit2.Retrofit.Builder())
			.baseUrl(baseURL)
			.addConverterFactory(JacksonConverterFactory.create(mapper))
			.client(client)
			.build();

		return retrofit.create(BrapiGenotypingService.class);
	}

	private BrapiCoreService buildCoreService(String baseURL, OkHttpClient client)
	{
		ObjectMapper mapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		this.retrofit = (new retrofit2.Retrofit.Builder())
			.baseUrl(baseURL)
			.addConverterFactory(JacksonConverterFactory.create(mapper))
			.client(client)
			.build();

		return retrofit.create(BrapiCoreService.class);
	}

	public Response getResponse(URI uri)
		throws Exception
	{
		Request request = new Request.Builder()
			.url(uri.toURL())
			.build();

		return httpClient.newCall(request).execute();
	}

	Retrofit getRetrofit()
	{
		return retrofit;
	}

	public void cancelAll()
	{
		httpClient.dispatcher().cancelAll();
	}
}