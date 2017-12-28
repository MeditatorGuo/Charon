package com.uowee.charon;

import android.app.Activity;
import android.content.Context;

import com.uowee.charon.api.BaseApiService;
import com.uowee.charon.callback.CharonCallback;
import com.uowee.charon.func.ApiErrFunc;
import com.uowee.charon.interceptor.GzipRequestInterceptor;
import com.uowee.charon.interceptor.HeadersInterceptor;
import com.uowee.charon.mode.ApiResult;
import com.uowee.charon.subscriber.BaseSubscriber;
import com.uowee.charon.subscriber.CharonSubscriber;
import com.uowee.charon.utils.SSLUtil;
import com.uowee.charon.utils.Utils;

import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by GuoWee on 2017/10/26 14:34
 */
public final class Charon {

    public static final String TAG = "Charon";
    private static Context mContext;
    private static Retrofit retrofit;
    private static BaseApiService apiService;
    private static Retrofit.Builder retrofitBuilder;
    private static OkHttpClient okHttpClient;
    private static OkHttpClient.Builder okHttpClientBuilder;


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


    public <T> Observable<T> get(String url, Map<String, Object> maps) {
        return (Observable<T>) apiService.get(url, maps);
    }

    /**
     * @param url
     * @param callback
     * @param <T>
     * @return
     */
    public <T> Subscription get(String url, CharonCallback<T> callback) {
        return get(url, null, callback);
    }

    /**
     * 普通Get方式请求，需要传入subscriber
     *
     * @param url
     * @param maps
     * @param subscriber
     * @return
     */
    public Subscription get(String url, Map<String, Object> maps, BaseSubscriber<ResponseBody> subscriber) {
        return apiService.get(url, maps).compose(schedulersTransformer).compose(handleErrorTransform()).subscribe(subscriber);
    }

    /**
     * 普通Get方式请求，需要传入callback
     *
     * @param url
     * @param maps
     * @param callback
     * @param <T>
     * @return
     */
    public <T> Subscription get(String url, Map<String, Object> maps, CharonCallback<T> callback) {
        return apiService.get(url, maps).compose(schedulersTransformer).compose(handleErrorTransform()).subscribe(new CharonSubscriber(mContext, callback));
    }

    public Subscription post(String url, Map<String, Object> maps, BaseSubscriber<ResponseBody> subscriber) {
        return apiService.post(url, maps).compose(schedulersTransformer).compose(handleErrorTransform()).subscribe(subscriber);
    }

    public <T> Subscription post(String url, Map<String, Object> maps, CharonCallback<T> callback) {
        return apiService.post(url, maps).compose(schedulersTransformer).compose(handleErrorTransform()).subscribe(new CharonSubscriber(mContext, callback));
    }

    public <T> Subscription form(String url, @FieldMap(encoded = true) Map<String, Object> fields, CharonCallback<T> callback) {
        return apiService.postForm(url, fields).compose(schedulersTransformer).compose(handleErrorTransform()).subscribe(new CharonSubscriber(mContext, callback));
    }

    public Subscription form(String url, @FieldMap(encoded = true) Map<String, Object> fields, BaseSubscriber<ResponseBody> subscriber) {
        return apiService.postForm(url, fields).compose(schedulersTransformer).compose(handleErrorTransform()).subscribe(subscriber);
    }

    public <T> Subscription body(String url, Object body, CharonCallback<T> callback) {
        return apiService.postBody(url, body).compose(schedulersTransformer).compose(handleErrorTransform()).subscribe(new CharonSubscriber(mContext, callback));
    }

    public Subscription body(String url, Object body, BaseSubscriber<ResponseBody> subscriber) {
        return apiService.postBody(url, body).compose(schedulersTransformer).compose(handleErrorTransform()).subscribe(subscriber);
    }

    public <T> Subscription json(String url, RequestBody json, CharonCallback<T> callback) {
        return apiService.postJson(url, json).compose(schedulersTransformer).compose(handleErrorTransform()).subscribe(new CharonSubscriber(mContext, callback));
    }

    public Subscription json(String url, RequestBody json, BaseSubscriber<ResponseBody> subscriber) {
        return apiService.postJson(url, json).compose(schedulersTransformer).compose(handleErrorTransform()).subscribe(subscriber);
    }

    public Subscription put(String url, Map<String, Object> maps, BaseSubscriber<ResponseBody> subscriber) {
        return apiService.put(url, maps).compose(schedulersTransformer).compose(handleErrorTransform()).subscribe(subscriber);
    }

    public <T> Subscription put(String url, Map<String, Object> maps, CharonCallback<T> callback) {
        return apiService.put(url, maps).compose(schedulersTransformer).compose(handleErrorTransform()).subscribe(new CharonSubscriber(mContext, callback));
    }

    public Subscription delete(String url,Map<String,Object> maps,BaseSubscriber<ResponseBody> subscriber){
        return apiService.delete(url, maps).compose(schedulersTransformer).compose(handleErrorTransform()).subscribe(subscriber);
    }

    public <T> Subscription delete(String url,Map<String,Object> maps, CharonCallback<T> callback ){
        return apiService.delete(url, maps).compose(schedulersTransformer).compose(handleErrorTransform()).subscribe(new CharonSubscriber(mContext,callback));
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
                return observable.onErrorResumeNext(new ApiErrFunc());
            }
        };
    }


    public static final class Builder {
        private okhttp3.Call.Factory callFactory;
        private Converter.Factory converterFactory;
        private CallAdapter.Factory callAdapterFactory;
        private HostnameVerifier hostnameVerifier;
        private SSLSocketFactory sslSocketFactory;
        private ConnectionPool connectionPool;
        private String baseUrl;

        public Builder(Context context) {
            okHttpClientBuilder = new OkHttpClient.Builder();
            retrofitBuilder = new Retrofit.Builder();
            if (context instanceof Activity) {
                mContext = context.getApplicationContext();
            } else {
                mContext = context;
            }
        }

        /**
         * 设置请求头部
         *
         * @param headers
         * @return
         */
        public Builder headers(Map<String, String> headers) {
            okHttpClientBuilder.addInterceptor(new HeadersInterceptor(headers));
            return this;
        }

        /**
         * 设置请求参数
         *
         * @param parameters
         * @return
         */
        public Builder parameters(Map<String, String> parameters) {
            okHttpClientBuilder.addInterceptor(new HeadersInterceptor(parameters));
            return this;
        }

        /**
         * 设置拦截器
         *
         * @param interceptor
         * @return
         */
        public Builder interceptor(Interceptor interceptor) {
            okHttpClientBuilder.addInterceptor(Utils.checkNotNull(interceptor, "interceptor == null"));
            return this;
        }

        /**
         * 设置网络拦截器
         *
         * @param interceptor
         * @return
         */
        public Builder networkInterceptor(Interceptor interceptor) {
            okHttpClientBuilder.addNetworkInterceptor(Utils.checkNotNull(interceptor, "interceptor == null"));
            return this;
        }

        /**
         * 设置连接池
         *
         * @param connectionPool
         * @return
         */
        public Builder connectionPool(ConnectionPool connectionPool) {
            if (connectionPool == null) {
                throw new NullPointerException("connectionPool == null");
            }
            this.connectionPool = connectionPool;
            return this;
        }

        /**
         * 设置代理
         *
         * @param proxy
         * @return
         */
        public Builder proxy(Proxy proxy) {
            okHttpClientBuilder.proxy(Utils.checkNotNull(proxy, "proxy == null"));
            return this;
        }


        /**
         * 设置请求BaseURL
         *
         * @param baseUrl
         * @return
         */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /**
         * 设置自定义OkHttpClient
         *
         * @param client
         * @return
         */
        public Builder client(OkHttpClient client) {
            retrofitBuilder.client(Utils.checkNotNull(client, "client == null"));
            return this;
        }

        /**
         * 设置Call的工厂
         *
         * @param factory
         * @return
         */
        public Builder callFactory(Call.Factory factory) {
            this.callFactory = Utils.checkNotNull(factory, "factory == null");
            return this;
        }

        /**
         * 设置转换工厂
         *
         * @param factory
         * @return
         */
        public Builder converterFactory(Converter.Factory factory) {
            this.converterFactory = Utils.checkNotNull(factory, "factory == null");
            return this;
        }

        /**
         * 设置CallAdapter工厂
         *
         * @param factory
         * @return
         */
        public Builder callAdapterFactory(CallAdapter.Factory factory) {
            this.callAdapterFactory = Utils.checkNotNull(factory, "factory == null");
            return this;
        }

        /**
         * 设置连接超时时间（秒）
         *
         * @param timeout
         * @return
         */
        public Builder connectTimeout(int timeout) {
            connectTimeout(timeout, TimeUnit.SECONDS);
            return this;
        }

        /**
         * 设置读取超时时间（秒）
         *
         * @param timeout
         * @return
         */
        public Builder readTimeout(int timeout) {
            readTimeout(timeout, TimeUnit.SECONDS);
            return this;
        }

        /**
         * 设置写入超时时间（秒）
         *
         * @param timeout
         * @return
         */
        public Builder writeTimeout(int timeout) {
            writeTimeout(timeout, TimeUnit.SECONDS);
            return this;
        }

        /**
         * 设置连接超时时间
         *
         * @param timeout
         * @param unit
         * @return
         */
        public Builder connectTimeout(int timeout, TimeUnit unit) {
            if (timeout > -1) {
                okHttpClientBuilder.connectTimeout(timeout, unit);
            } else {
                okHttpClientBuilder.connectTimeout(Const.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            }
            return this;
        }

        /**
         * 设置读取超时时间
         *
         * @param timeout
         * @param unit
         * @return
         */
        public Builder readTimeout(int timeout, TimeUnit unit) {
            if (timeout > -1) {
                okHttpClientBuilder.readTimeout(timeout, unit);
            } else {
                okHttpClientBuilder.readTimeout(Const.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            }
            return this;
        }

        /**
         * 设置写入超时时间
         *
         * @param timeout
         * @param unit
         * @return
         */
        public Builder writeTimeout(int timeout, TimeUnit unit) {
            if (timeout > -1) {
                okHttpClientBuilder.writeTimeout(timeout, unit);
            } else {
                okHttpClientBuilder.writeTimeout(Const.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            }
            return this;
        }


        /**
         * 设置SSL工厂
         *
         * @param sslSocketFactory
         * @return
         */
        public Builder sslSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = sslSocketFactory;
            return this;
        }

        /**
         * 设置主机验证机制
         *
         * @param hostnameVerifier
         * @return
         */
        public Builder hostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }

        /**
         * 使用POST方式是否需要进行GZIP压缩，服务器不支持则不设置
         *
         * @return
         */
        public Builder postGzipInterceptor() {
            interceptor(new GzipRequestInterceptor());
            return this;
        }

        public Charon build() {

            if (okHttpClientBuilder == null) {
                throw new NullPointerException("OkHttpClientBuilder is required.");
            }

            if (retrofitBuilder == null) {
                throw new NullPointerException("RetrofitBuilder is required.");
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
            okHttpClientBuilder.hostnameVerifier(hostnameVerifier);

            if (sslSocketFactory == null) {
                sslSocketFactory = SSLUtil.getSslSocketFactory(null, null, null);
            }
            okHttpClientBuilder.sslSocketFactory(sslSocketFactory);

            if (connectionPool == null) {
                connectionPool = new ConnectionPool(Const.DEFAULT_MAX_IDLE_CONNECTIONS,
                        Const.DEFAULT_KEEP_ALIVE_DURATION, TimeUnit.SECONDS);
            }
            okHttpClientBuilder.connectionPool(connectionPool);

            okHttpClient = okHttpClientBuilder.build();
            retrofitBuilder.client(okHttpClient);
            retrofit = retrofitBuilder.build();
            apiService = retrofit.create(BaseApiService.class);
            return new Charon();
        }
    }
}
