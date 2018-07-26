package com.missile.charon;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import com.missile.charon.api.ApiService;
import com.missile.charon.cache.ApiCache;
import com.missile.charon.cache.DiskCache;
import com.missile.charon.callback.NovateCallback;
import com.missile.charon.cookie.ApiCookie;
import com.missile.charon.func.ApiErrFunc;
import com.missile.charon.interceptor.GzipRequestInterceptor;
import com.missile.charon.interceptor.HeadersInterceptor;
import com.missile.charon.interceptor.OfflineCacheInterceptor;
import com.missile.charon.interceptor.OnlineCacheInterceptor;
import com.missile.charon.mode.ApiHost;
import com.missile.charon.mode.ApiResult;
import com.missile.charon.mode.CacheMode;
import com.missile.charon.subscriber.BaseSubscriber;
import com.missile.charon.subscriber.NovateSubscriber;
import com.missile.charon.utils.SSLUtil;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by GuoWee on 2018/6/26.
 */

public class Charon {
    private static Context mContext;
    private static ApiService apiService;
    private static Retrofit retrofit;
    private static Retrofit.Builder retrofitBuilder;
    private static OkHttpClient okHttpClient;
    private static OkHttpClient.Builder okHttpBuilder;
    private static ApiCache apiCache;
    private static ApiCache.Builder apiCacheBuilder;
    private static CacheMode cacheMode = CacheMode.ONLY_REMOTE;

    private Charon() {
    }

    public <T> T create(final Class<T> service) {
        return retrofit.create(service);
    }

    public Subscription clearCache() {
        return apiCache.clear();
    }

    public void removeCache(String key) {
        apiCache.remove(key);
    }

    public <T> Observable<T> get(String url, Map<String, Object> maps) {
        return (Observable<T>) apiService.get(url, maps);
    }

    public <T> Subscription get(String url, NovateCallback<T> callback) {
        return get(url, null, callback);
    }

    public Subscription get(String url, Map<String, Object> maps, BaseSubscriber<ResponseBody> subscriber) {
        return apiService.get(url, maps).compose(schedulersTransformer).compose(handleErrorTransform()).subscribe(subscriber);
    }

    public <T> Subscription get(String url, Map<String, Object> maps, NovateCallback<T> callback) {
        return apiService.get(url, maps).compose(schedulersTransformer).compose(handleErrorTransform()).subscribe(new NovateSubscriber(mContext, callback));
    }

    public Subscription post(String url, Map<String, Object> maps, BaseSubscriber<ResponseBody> subscriber) {
        return apiService.post(url, maps).compose(schedulersTransformer).compose(handleErrorTransform()).subscribe(subscriber);
    }

    public <T> Subscription post(String url, Map<String, Object> maps, NovateCallback<T> callback) {
        return apiService.post(url, maps).compose(schedulersTransformer).compose(handleErrorTransform()).subscribe(new NovateSubscriber(mContext, callback));
    }

    public Subscription put(String url, Map<String, Object> maps, BaseSubscriber<ResponseBody> subscriber) {
        return apiService.put(url, maps).compose(schedulersTransformer).compose(handleErrorTransform()).subscribe(subscriber);
    }

    public <T> Subscription put(String url, Map<String, Object> maps, NovateCallback<T> callback) {
        return apiService.put(url, maps).compose(schedulersTransformer).compose(handleErrorTransform()).subscribe(new NovateSubscriber(mContext, callback));
    }

    public Subscription delete(String url, Map<String, Object> maps, BaseSubscriber<ResponseBody> subscriber) {
        return apiService.delete(url, maps).compose(schedulersTransformer).compose(handleErrorTransform()).subscribe(subscriber);
    }

    public <T> Subscription delete(String url, Map<String, Object> maps, NovateCallback<T> callback) {
        return apiService.delete(url, maps).compose(schedulersTransformer).compose(handleErrorTransform()).subscribe(new NovateSubscriber(mContext, callback));
    }


    final Observable.Transformer schedulersTransformer = new Observable.Transformer() {
        @Override
        public Object call(Object o) {
            return ((Observable) o).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    final <T> Observable.Transformer<ApiResult<T>, T> handleErrorTransform() {

        return new Observable.Transformer<ApiResult<T>, T>() {
            @Override
            public Observable<T> call(Observable<ApiResult<T>> observable) {
                return observable.compose(schedulersTransformer).onErrorResumeNext(new ApiErrFunc<T>());
            }
        };
    }


    private static <T> T checkNotNull(T t, String message) {
        if (t == null) {
            throw new NullPointerException(message);
        }
        return t;
    }

    public static final class Builder {
        private okhttp3.Call.Factory callFactory;
        private Converter.Factory converterFactory;
        private CallAdapter.Factory callAdapterFactory;
        private HostnameVerifier hostnameVerifier;
        private SSLSocketFactory sslSocketFactory;
        private ConnectionPool connectionPool;
        private File httpCacheDirectory;
        private ApiCookie apiCookie;
        private Cache cache;
        private Boolean isCookie = false;
        private Boolean isCache = false;
        private String baseUrl;

        public Builder(Context context) {
            mContext = context;
            okHttpBuilder = new OkHttpClient.Builder();
            retrofitBuilder = new Retrofit.Builder();
            apiCacheBuilder = new ApiCache.Builder(mContext);

        }

        public Charon.Builder client(OkHttpClient client) {
            retrofitBuilder.client(checkNotNull(client, "client == null"));
            return this;
        }

        public Charon.Builder callFactory(okhttp3.Call.Factory factory) {
            this.callFactory = checkNotNull(factory, "factory == null");
            return this;
        }

        public Charon.Builder connectTimeout(int timeout) {
            return connectTimeout(timeout, TimeUnit.SECONDS);
        }

        public Charon.Builder readTimeout(int timeout) {
            return readTimeout(timeout, TimeUnit.SECONDS);
        }

        public Charon.Builder writeTimeout(int timeout) {
            return writeTimeout(timeout, TimeUnit.SECONDS);
        }

        public Charon.Builder connectTimeout(int timeout, TimeUnit unit) {
            if (timeout > -1) {
                okHttpBuilder.connectTimeout(timeout, unit);
            } else {
                okHttpBuilder.connectTimeout(Configure.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            }
            return this;
        }

        public Charon.Builder readTimeout(int timeout, TimeUnit unit) {
            if (timeout > -1) {
                okHttpBuilder.readTimeout(timeout, unit);
            } else {
                okHttpBuilder.readTimeout(Configure.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            }
            return this;
        }

        public Charon.Builder writeTimeout(int timeout, TimeUnit unit) {
            if (timeout > -1) {
                okHttpBuilder.writeTimeout(timeout, unit);
            } else {
                okHttpBuilder.writeTimeout(Configure.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            }
            return this;
        }

        public Charon.Builder cookie(boolean isCookie) {
            this.isCookie = isCookie;
            return this;
        }


        public Charon.Builder cache(boolean isCache) {
            this.isCache = isCache;
            return this;
        }

        public Charon.Builder proxy(Proxy proxy) {
            okHttpBuilder.proxy(checkNotNull(proxy, "proxy == null"));
            return this;
        }

        public Charon.Builder connectionPool(ConnectionPool connectionPool) {
            if (connectionPool == null) throw new NullPointerException("connectionPool == null");
            this.connectionPool = connectionPool;
            return this;
        }

        public Charon.Builder baseUrl(String baseUrl) {
            this.baseUrl = checkNotNull(baseUrl, "baseUrl == null");
            return this;
        }

        public Charon.Builder converterFactory(Converter.Factory factory) {
            this.converterFactory = factory;
            return this;
        }


        public Charon.Builder callAdapterFactory(CallAdapter.Factory factory) {
            this.callAdapterFactory = factory;
            return this;
        }

        public Charon.Builder headers(Map<String, String> headers) {
            okHttpBuilder.addInterceptor(new HeadersInterceptor(headers));
            return this;
        }


        public Charon.Builder parameters(Map<String, String> parameters) {
            okHttpBuilder.addInterceptor(new HeadersInterceptor(parameters));
            return this;
        }


        public Charon.Builder interceptor(Interceptor interceptor) {
            okHttpBuilder.addInterceptor(checkNotNull(interceptor, "interceptor == null"));
            return this;
        }


        public Charon.Builder networkInterceptor(Interceptor interceptor) {
            okHttpBuilder.addNetworkInterceptor(checkNotNull(interceptor, "interceptor == null"));
            return this;
        }

        public Charon.Builder cookieManager(ApiCookie cookie) {
            if (cookie == null) throw new NullPointerException("cookieManager == null");
            this.apiCookie = cookie;
            return this;
        }


        public Charon.Builder SSLSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = sslSocketFactory;
            return this;
        }


        public Charon.Builder hostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }


        public Charon.Builder postGzipInterceptor() {
            interceptor(new GzipRequestInterceptor());
            return this;
        }


        public Charon.Builder cacheKey(String cacheKey) {
            apiCacheBuilder.cacheKey(checkNotNull(cacheKey, "cacheKey == null"));
            return this;
        }


        public Charon.Builder cacheTime(long cacheTime) {
            apiCacheBuilder.cacheTime(Math.max(DiskCache.CACHE_NEVER_EXPIRE, cacheTime));
            return this;
        }

        public Charon.Builder cacheMode(CacheMode mCacheMode) {
            cacheMode = mCacheMode;
            return this;
        }


        public Charon.Builder cacheOnline(Cache cache) {
            networkInterceptor(new OnlineCacheInterceptor());
            this.cache = cache;
            return this;
        }


        public Charon.Builder cacheOnline(Cache cache, final int cacheControlValue) {
            networkInterceptor(new OnlineCacheInterceptor(cacheControlValue));
            this.cache = cache;
            return this;
        }


        public Charon.Builder cacheOffline(Cache cache) {
            networkInterceptor(new OfflineCacheInterceptor(mContext));
            interceptor(new OfflineCacheInterceptor(mContext));
            this.cache = cache;
            return this;
        }


        public Charon.Builder cacheOffline(Cache cache, final int cacheControlValue) {
            networkInterceptor(new OfflineCacheInterceptor(mContext, cacheControlValue));
            interceptor(new OfflineCacheInterceptor(mContext, cacheControlValue));
            this.cache = cache;
            return this;
        }

        public Charon build() {
            if (okHttpBuilder == null) {
                throw new IllegalStateException("okHttpBuilder required.");
            }
            if (retrofitBuilder == null) {
                throw new IllegalStateException("retrofitBuilder required.");
            }
            if (apiCacheBuilder == null) {
                throw new IllegalStateException("apiCacheBuilder required.");
            }
            if (baseUrl == null) {
                baseUrl = ApiHost.getHost();
            }
            retrofitBuilder.baseUrl(baseUrl);

            if (converterFactory == null) {
                converterFactory = GsonConverterFactory.create();
            }
            retrofitBuilder.addConverterFactory(converterFactory);

            if (callAdapterFactory == null) {
                callAdapterFactory = RxJavaCallAdapterFactory.create();
            }
            retrofitBuilder.addCallAdapterFactory(callAdapterFactory);

            if (callFactory != null) {
                retrofitBuilder.callFactory(callFactory);
            }

            if (hostnameVerifier == null) {
                hostnameVerifier = new SSLUtil.UnSafeHostnameVerifier(baseUrl);
            }
            okHttpBuilder.hostnameVerifier(hostnameVerifier);

            if (sslSocketFactory == null) {
                sslSocketFactory = SSLUtil.getSslSocketFactory(null, null, null);
            }
            okHttpBuilder.sslSocketFactory(sslSocketFactory);

            if (connectionPool == null) {
                connectionPool = new ConnectionPool(Configure.DEFAULT_MAX_IDLE_CONNECTIONS, Configure.DEFAULT_KEEP_ALIVE_DURATION, TimeUnit.SECONDS);
            }
            okHttpBuilder.connectionPool(connectionPool);

            if (isCookie && apiCookie == null) {
                apiCookie = new ApiCookie(mContext);
            }
            if (isCookie) {
                okHttpBuilder.cookieJar(apiCookie);
            }

            if (httpCacheDirectory == null) {
                httpCacheDirectory = new File(mContext.getCacheDir(), Configure.CACHE_HTTP_DIR);
            }
            if (isCache) {
                try {
                    if (cache == null) {
                        cache = new Cache(httpCacheDirectory, Configure.CACHE_MAX_SIZE);
                    }
                    cacheOnline(cache);
                    cacheOffline(cache);
                } catch (Exception e) {
                    Log.e("TAG", "Could not create http cache" + e);
                }
            }
            if (cache != null) {
                okHttpBuilder.cache(cache);
            }

            okHttpClient = okHttpBuilder.build();
            retrofitBuilder.client(okHttpClient);
            retrofit = retrofitBuilder.build();
            apiCache = apiCacheBuilder.build();
            apiService = retrofit.create(ApiService.class);
            return new Charon();
        }

    }
}
