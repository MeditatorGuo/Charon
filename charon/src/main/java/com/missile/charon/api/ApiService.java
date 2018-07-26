package com.missile.charon.api;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by GuoWee on 2018/6/26.
 */

public interface ApiService {

    @GET()
    Observable<ResponseBody> get(@Url String url, @QueryMap Map<String, Object> maps);

    @POST()
    @FormUrlEncoded
    Observable<ResponseBody> post(@Url String url, @FieldMap Map<String, Object> maps);

    @DELETE()
    Observable<ResponseBody> delete(@Url() String url, @QueryMap Map<String, Object> maps);

    @PUT()
    Observable<ResponseBody> put(@Url() String url, @QueryMap Map<String, Object> maps);
}
