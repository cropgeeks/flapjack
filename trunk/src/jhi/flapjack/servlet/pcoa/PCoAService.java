package jhi.flapjack.servlet.pcoa;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface PCoAService
{
	@Multipart
	@POST("pcoa")
	Call<ResponseBody> postSimMatrix(@Part MultipartBody.Part file, @Query("noDimensions") String noDimensions, @Query("flapjackUID") String flapjackUID);

	@Headers("Accept: text/plain")
	@GET("pcoa/{taskId}")
	Call<ResponseBody> getPcoa(@Path("taskId") String uri);

	@DELETE("pcoa/{taskId}")
	Call<ResponseBody> cancelJob(@Path("taskId") String uri);
}
