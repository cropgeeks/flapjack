package jhi.flapjack.io.brapi;

import java.util.*;

import jhi.brapi.resource.*;

import retrofit2.Call;
import retrofit2.http.*;

public interface BrapiService
{
	@FormUrlEncoded
	@POST("token")
	Call<BrapiSessionToken> getAuthToken(@Field("grant_type") String grantType, @Field("username") String username, @Field("password") String password, @Field("client_id") String clientId);

	@GET("studies-search")
	Call<BasicResource<DataResult<BrapiStudies>>> getStudies(@Query("studyType") String studyType, @Query("pageSize") String pageSize, @Query("page") String page);

	@GET("maps")
	Call<BasicResource<DataResult<BrapiGenomeMap>>> getMaps(@Query("pageSize") String pageSize, @Query("page") String page);

	@GET("maps/{id}/positions")
	Call<BasicResource<DataResult<BrapiMarkerPosition>>> getMapMarkerData(@Path("id") String id, @Query("pageSize") String pageSize, @Query("page") String page);

	@GET("markerprofiles")
	Call<BasicResource<DataResult<BrapiMarkerProfile>>> getMarkerProfiles(@Query("studyDbId") String studyDbId, @Query("pageSize") String pageSize, @Query("page") String page);

	@FormUrlEncoded
	@POST("allelematrix-search")
	Call<BasicResource<BrapiAlleleMatrix>> getAlleleMatrix(@Field("markerprofileDbId")List<String> markerProfileDbIds, @Query("pageSize") String pageSize, @Query("page") String page);

	@FormUrlEncoded
	@POST("allelematrix-search")
	Call<BasicResource<BrapiAlleleMatrix>> getAlleleMatrix(@Field("markerprofileDbId")List<String> markerProfileDbIds, @Field("formnat") String format, @Query("pageSize") String pageSize, @Query("page") String page);
}
