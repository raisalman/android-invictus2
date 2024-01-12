package net.invictusmanagement.invictuslifestyle.webservice;

import net.invictusmanagement.invictuslifestyle.models.AccessCodeResponse;
import net.invictusmanagement.invictuslifestyle.models.AccessPoint;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RestService {
    @GET("AccessPoints/getbrivoaccesscode")
    Call<AccessCodeResponse> getAccessCode();

    @GET("accesspoints")
    Call<List<AccessPoint>> getAccessPoints();

    @Headers({"Content-Type: application/json "})
    @POST("AccessPoints/accesspointaudit")
    Call<ResponseBody> postAccessPointAudit(@Body String json);

    @Headers({"Content-Type: application/json "})
    @POST("accesspoints")
    Call<ResponseBody> openAccessPoint(@Body String json);
}
