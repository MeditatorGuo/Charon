package com.missile.charon.interceptor;

import android.content.Context;

import java.io.IOException;

import com.missile.charon.Configure;
import com.missile.charon.utils.NetworkUtil;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by GuoWee on 2018/6/26.
 */

public class OfflineCacheInterceptor implements Interceptor {
    private Context context;
    private String cacheControlValue;

    public OfflineCacheInterceptor(Context context) {
        this(context, Configure.MAX_AGE_OFFLINE);
    }

    public OfflineCacheInterceptor(Context context, int cacheControlValue) {
        this.context = context;
        this.cacheControlValue = String.format("max-stale=%d", cacheControlValue);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!NetworkUtil.isConnected(context)) {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
            Response response = chain.proceed(request);
            return response.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, " + cacheControlValue)
                    .removeHeader("Pragma")
                    .build();
        }
        return chain.proceed(request);
    }
}
