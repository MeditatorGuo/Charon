package com.missile.charon;

import android.content.Context;
import android.util.Log;

import com.missile.charon.api.ApiService;
import com.missile.charon.cache.ApiCache;
import com.missile.charon.cache.DiskCache;
import com.missile.charon.callback.ApiCallback;
import com.missile.charon.cookie.ApiCookie;
import com.missile.charon.func.ApiErrFunc;
import com.missile.charon.func.ApiFunc;
import com.missile.charon.interceptor.GzipRequestInterceptor;
import com.missile.charon.interceptor.HeadersInterceptor;
import com.missile.charon.interceptor.OfflineCacheInterceptor;
import com.missile.charon.interceptor.OnlineCacheInterceptor;
import com.missile.charon.mode.ApiHost;
import com.missile.charon.mode.CacheMode;
import com.missile.charon.mode.CacheResult;
import com.missile.charon.subscriber.ApiSubscriber;
import com.missile.charon.utils.ClassUtil;
import com.missile.charon.utils.SSLUtil;

import java.io.File;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

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

    /**
     * 可传入自定义的接口服务
     *
     * @param service
     * @param <T>
     * @return
     */
    public <T> T create(final Class<T> service) {
        return retrofit.create(service);
    }


    /**
     * 由外部设置被观察者
     *
     * @param observable
     * @param <T>
     * @return
     */
    public <T> Observable<T> call(Observable<T> observable) {
        return observable.compose(new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .onErrorResumeNext(new ApiErrFunc<T>());
            }
        });
    }


    /**
     * 清除所有缓存
     *
     * @return
     */
    public Subscription clearCache() {
        return apiCache.clear();
    }

    /**
     * 清除对于Key的缓存
     *
     * @param key
     */
    public void removeCache(String key) {
        apiCache.remove(key);
    }

    private <T> Observable<T> get(String url, Map<String, Object> maps, Class<T> clazz) {
        return apiService.get(url, maps).compose(normalTransformer(clazz));
    }

    /**
     * 普通Get方式请求，无需订阅，只需传入Callback
     *
     * @param url
     * @param maps
     * @param callback
     * @param <T>
     * @return
     */
    public <T> Subscription get(String url, Map<String, Object> maps, ApiCallback<T> callback) {
        return this.get(url, maps, ClassUtil.getTClass(callback)).subscribe(new ApiSubscriber(mContext, callback));
    }

    private <T> Observable<CacheResult<T>> cacheGet(final String url, final Map<String, Object> maps, Class<T> clazz) {
        return apiService.get(url, maps).compose(normalTransformer(clazz)).compose(apiCache.transformer(cacheMode, clazz));
    }

    /**
     * 带缓存Get方式请求，请求前需配置缓存key，缓存时间默认永久，还可以配置缓存策略，无需订阅，只需配置Callback
     *
     * @param url
     * @param maps
     * @param callback
     * @param <T>
     * @return
     */
    public <T> Subscription cacheGet(String url, Map<String, Object> maps, ApiCallback<T> callback) {
        return this.cacheGet(url, maps, ClassUtil.getTClass(callback)).subscribe(new ApiSubscriber(mContext, callback));
    }


    private <T> Observable<T> post(final String url, final Map<String, Object> maps, Class<T> clazz) {
        return apiService.post(url, maps).compose(normalTransformer(clazz));
    }

    /**
     * 普通POST方式请求，无需订阅，只需传入Callback回调
     *
     * @param url
     * @param maps
     * @param callback
     * @param <T>
     * @return
     */
    public <T> Subscription post(String url, Map<String, Object> maps, ApiCallback<T> callback) {
        return this.post(url, maps, ClassUtil.getTClass(callback)).subscribe(new ApiSubscriber(mContext, callback));
    }

    private <T> Observable<CacheResult<T>> cachePost(final String url, final Map<String, Object> maps, Class<T> clazz) {
        return apiService.post(url, maps).compose(normalTransformer(clazz)).compose(apiCache.transformer(cacheMode, clazz));
    }

    /**
     * 带缓存POST方式请求，请求前需配置缓存key，缓存时间默认永久，还可以配置缓存策略，无需订阅，只需配置Callback
     *
     * @param url
     * @param maps
     * @param callback
     * @param <T>
     * @return
     */
    public <T> Subscription cachePost(String url, Map<String, Object> maps, ApiCallback<T> callback) {
        return this.cachePost(url, maps, ClassUtil.getTClass(callback)).subscribe(new ApiSubscriber(mContext, callback));
    }

    private <T> Observable<T> put(final String url, final Map<String, Object> maps, Class<T> clazz) {
        return apiService.put(url, maps).compose(normalTransformer(clazz));
    }

    /**
     * 修改信息请求，无需订阅，只需传入Callback
     *
     * @param url
     * @param maps
     * @param callback
     * @param <T>
     * @return
     */
    public <T> Subscription put(String url, Map<String, Object> maps, ApiCallback<T> callback) {
        return this.put(url, maps, ClassUtil.getTClass(callback)).subscribe(new ApiSubscriber(mContext, callback));
    }

    private <T> Observable<T> delete(final String url, final Map<String, Object> maps, Class<T> clazz) {
        return apiService.delete(url, maps).compose(normalTransformer(clazz));
    }

    /**
     * 删除信息请求，无需订阅，只需传入Callback
     *
     * @param url
     * @param maps
     * @param callback
     * @param <T>
     * @return
     */
    public <T> Subscription delete(String url, Map<String, Object> maps, ApiCallback<T> callback) {
        return this.delete(url, maps, ClassUtil.getTClass(callback)).subscribe(new ApiSubscriber(mContext, callback));
    }


    private <T> Observable.Transformer<ResponseBody, T> normalTransformer(final Class<T> clazz) {
        return new Observable.Transformer<ResponseBody, T>() {
            @Override
            public Observable<T> call(Observable<ResponseBody> responseBodyObservable) {
                return responseBodyObservable.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new ApiFunc<T>(clazz))
                        .onErrorResumeNext(new ApiErrFunc<T>());
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

        /**
         * 设置自定义的 OkHttpClient
         *
         * @param client
         * @return
         */
        public Charon.Builder client(OkHttpClient client) {
            retrofitBuilder.client(checkNotNull(client, "client == null"));
            return this;
        }

        /**
         * 设置Call的Factory
         *
         * @param factory
         * @return
         */
        public Charon.Builder callFactory(okhttp3.Call.Factory factory) {
            this.callFactory = checkNotNull(factory, "factory == null");
            return this;
        }

        /**
         * 设置连接超时时间（秒）
         *
         * @param timeout
         * @return
         */
        public Charon.Builder connectTimeout(int timeout) {
            return connectTimeout(timeout, TimeUnit.SECONDS);
        }

        /**
         * 设置读取超时时间（秒）
         *
         * @param timeout
         * @return
         */
        public Charon.Builder readTimeout(int timeout) {
            return readTimeout(timeout, TimeUnit.SECONDS);
        }

        /**
         * 设置写入超时时间（秒）
         *
         * @param timeout
         * @return
         */
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

        /**
         * 设置是否添加Cookie
         *
         * @param isCookie
         * @return
         */
        public Charon.Builder cookie(boolean isCookie) {
            this.isCookie = isCookie;
            return this;
        }

        /**
         * 设置是否添加Cache
         *
         * @param isCache
         * @return
         */
        public Charon.Builder cache(boolean isCache) {
            this.isCache = isCache;
            return this;
        }

        /**
         * 设置代理
         *
         * @param proxy
         * @return
         */
        public Charon.Builder proxy(Proxy proxy) {
            okHttpBuilder.proxy(checkNotNull(proxy, "proxy == null"));
            return this;
        }

        /**
         * 设置连接池
         *
         * @param connectionPool
         * @return
         */
        public Charon.Builder connectionPool(ConnectionPool connectionPool) {
            if (connectionPool == null) throw new NullPointerException("connectionPool == null");
            this.connectionPool = connectionPool;
            return this;
        }

        /**
         * 设置请求BaseUrl
         *
         * @param baseUrl
         * @return
         */
        public Charon.Builder baseUrl(String baseUrl) {
            this.baseUrl = checkNotNull(baseUrl, "baseUrl == null");
            return this;
        }

        /**
         * 设置转换工厂
         *
         * @param factory
         * @return
         */
        public Charon.Builder converterFactory(Converter.Factory factory) {
            this.converterFactory = factory;
            return this;
        }

        /**
         * 设置CallAdapter工厂
         *
         * @param factory
         * @return
         */
        public Charon.Builder callAdapterFactory(CallAdapter.Factory factory) {
            this.callAdapterFactory = factory;
            return this;
        }

        /**
         * 设置请求头部
         *
         * @param headers
         * @return
         */
        public Charon.Builder headers(Map<String, String> headers) {
            okHttpBuilder.addInterceptor(new HeadersInterceptor(headers));
            return this;
        }

        /**
         * 设置请求参数
         *
         * @param parameters
         * @return
         */
        public Charon.Builder parameters(Map<String, String> parameters) {
            okHttpBuilder.addInterceptor(new HeadersInterceptor(parameters));
            return this;
        }

        /**
         * 设置拦截器
         *
         * @param interceptor
         * @return
         */
        public Charon.Builder interceptor(Interceptor interceptor) {
            okHttpBuilder.addInterceptor(checkNotNull(interceptor, "interceptor == null"));
            return this;
        }

        /**
         * 设置网络拦截器
         *
         * @param interceptor
         * @return
         */
        public Charon.Builder networkInterceptor(Interceptor interceptor) {
            okHttpBuilder.addNetworkInterceptor(checkNotNull(interceptor, "interceptor == null"));
            return this;
        }

        /**
         * 设置Cookie管理
         *
         * @param cookie
         * @return
         */
        public Charon.Builder cookieManager(ApiCookie cookie) {
            if (cookie == null) throw new NullPointerException("cookieManager == null");
            this.apiCookie = cookie;
            return this;
        }

        /**
         * 设置SSL工厂
         *
         * @param sslSocketFactory
         * @return
         */
        public Charon.Builder SSLSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = sslSocketFactory;
            return this;
        }

        /**
         * 设置主机验证机制
         *
         * @param hostnameVerifier
         * @return
         */
        public Charon.Builder hostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }

        /**
         * 使用POST方式是否需要进行GZIP压缩，服务器不支持则不设置
         *
         * @return
         */
        public Charon.Builder postGzipInterceptor() {
            interceptor(new GzipRequestInterceptor());
            return this;
        }

        /**
         * 设置缓存Key,主要针对网络请求结果进行缓存
         *
         * @param cacheKey
         * @return
         */
        public Charon.Builder cacheKey(String cacheKey) {
            apiCacheBuilder.cacheKey(checkNotNull(cacheKey, "cacheKey == null"));
            return this;
        }

        /**
         * 设置缓存时间，默认永久，主要针对网络请求结果进行缓存
         *
         * @param cacheTime
         * @return
         */
        public Charon.Builder cacheTime(long cacheTime) {
            apiCacheBuilder.cacheTime(Math.max(DiskCache.CACHE_NEVER_EXPIRE, cacheTime));
            return this;
        }

        /**
         * 设置缓存类型，可根据类型自动配置缓存策略，主要针对网络请求结果进行缓存
         *
         * @param mCacheMode
         * @return
         */
        public Charon.Builder cacheMode(CacheMode mCacheMode) {
            cacheMode = mCacheMode;
            return this;
        }

        /**
         * 设置在线缓存，主要针对网络请求结果进行缓存
         *
         * @param cache
         * @return
         */
        public Charon.Builder cacheOnline(Cache cache) {
            networkInterceptor(new OnlineCacheInterceptor());
            this.cache = cache;
            return this;
        }

        /**
         * 设置在线缓存，主要针对网络请求结果进行缓存
         *
         * @param cache
         * @param cacheControlValue
         * @return
         */
        public Charon.Builder cacheOnline(Cache cache, final int cacheControlValue) {
            networkInterceptor(new OnlineCacheInterceptor(cacheControlValue));
            this.cache = cache;
            return this;
        }

        /**
         * 设置离线缓存，主要针对网络请求结果进行缓存
         *
         * @param cache
         * @return
         */
        public Charon.Builder cacheOffline(Cache cache) {
            networkInterceptor(new OfflineCacheInterceptor(mContext));
            interceptor(new OfflineCacheInterceptor(mContext));
            this.cache = cache;
            return this;
        }

        /**
         * 设置离线缓存，主要针对网络请求结果进行缓存
         *
         * @param cache
         * @return
         */
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
