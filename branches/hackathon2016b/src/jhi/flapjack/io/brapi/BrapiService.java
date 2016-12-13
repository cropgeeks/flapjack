package jhi.flapjack.io.brapi;

import java.util.*;

import jhi.brapi.api.*;
import jhi.brapi.api.authentication.*;
import jhi.brapi.api.calls.*;
import jhi.brapi.api.genomemaps.*;
import jhi.brapi.api.markerprofiles.*;
import jhi.brapi.api.studies.*;

import retrofit2.Call;
import retrofit2.http.*;

public interface BrapiService
{
	@GET("calls")
	Call<BrapiListResource<BrapiCall>> getCalls(@Query("pageSize") String pageSize, @Query("page") String page);

	@FormUrlEncoded
	@POST("token")
	Call<BrapiSessionToken> getAuthToken(@Field("grant_type") String grantType, @Field("username") String username, @Field("password") String password, @Field("client_id") String clientId);

	@GET("studies-search")
	Call<BrapiListResource<BrapiStudies>> getStudies(@Query("studyType") String studyType, @Query("pageSize") String pageSize, @Query("page") String page);

	@GET("maps")
	Call<BrapiListResource<BrapiGenomeMap>> getMaps(@Query("pageSize") String pageSize, @Query("page") String page);

	@GET("maps/{id}")
	Call<BrapiBaseResource<BrapiMapMetaData>> getMapMetaData(@Path("id") String id);

	@GET("maps/{id}/positions")
	Call<BrapiListResource<BrapiMarkerPosition>> getMapMarkerData(@Path("id") String id, @Query("pageSize") String pageSize, @Query("page") String page);

	@GET("markerprofiles")
	Call<BrapiListResource<BrapiMarkerProfile>> getMarkerProfiles(@Query("studyDbId") String studyDbId, @Query("pageSize") String pageSize, @Query("page") String page);

	@FormUrlEncoded
	@POST("allelematrix-search")
	Call<BrapiBaseResource<BrapiAlleleMatrix>> getAlleleMatrix(@Field("markerprofileDbId")List<String> markerProfileDbIds, @Field("format") String format, @Query("pageSize") String pageSize, @Query("page") String page);
}