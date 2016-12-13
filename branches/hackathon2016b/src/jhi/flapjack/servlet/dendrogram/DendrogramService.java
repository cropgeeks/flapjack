package jhi.flapjack.servlet.dendrogram;

import okhttp3.*;
import retrofit2.Call;
import retrofit2.http.*;
import retrofit2.http.Headers;

public interface DendrogramService
{
	@Multipart
	@POST("dendrogram")
	Call<ResponseBody> postSimMatrix(@Part MultipartBody.Part file, @Query("lineCount") String lineCount, @Query("flapjackUID") String flapjackUID);

	@Headers("Accept: application/zip")
	@GET("dendrogram/{taskId}")
	Call<ResponseBody> getDendrogram(@Path("taskId") String uri);

	@DELETE("dendrogram/{taskId}")
	Call<ResponseBody> cancelJob(@Path("taskId") String uri);
}
