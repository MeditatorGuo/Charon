package com.uowee.charon.api;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

public interface BaseApiService {

    @GET()
    Observable<ResponseBody> get(@Url String url, @QueryMap Map<String, Object> maps);

    @POST()
    @FormUrlEncoded
    Observable<ResponseBody> post(@Url String url, @FieldMap Map<String, Object> maps);

    @POST()
    @FormUrlEncoded
    Observable<ResponseBody> postForm(@Url String url, @FieldMap Map<String, Object> maps);

    @POST()
    Observable<ResponseBody> postBody(@Url String url, @Body Object obj);

    @POST()
    Observable<ResponseBody> postJson(@Url String url, @Body RequestBody json);

    @DELETE()
    Observable<ResponseBody> delete(@Url() String url, @QueryMap Map<String, Object> maps);

    @PUT()
    Observable<ResponseBody> put(@Url() String url, @QueryMap Map<String, Object> maps);
}


