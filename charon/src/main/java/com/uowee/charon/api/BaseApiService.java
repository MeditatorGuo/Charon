package com.uowee.charon.api;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

public interface BaseApiService {

    @GET()
    Observable<ResponseBody> get(@Url String url, @QueryMap Map<String,String> maps);

}


