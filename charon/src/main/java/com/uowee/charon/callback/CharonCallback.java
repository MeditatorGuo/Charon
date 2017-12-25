package com.uowee.charon.callback;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Sim.G on 2017/12/6.
 */

public abstract class CharonCallback<T, E> implements Callback, IGenericConvert<E> {
    protected String TAG = "CharonCallback";
    protected Handler handler;
    private Context context;


    public CharonCallback() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
    }


    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public abstract T onHandleResponse(ResponseBody response) throws Exception;

    public abstract void onError( Throwable e);

    public abstract void onCancel( Throwable e);

    public abstract void onNext( Call call, T response);



    public static CharonCallback DEFAULT_CALLBACK = new CharonCallback() {

        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {

        }

        @Override
        public Object onHandleResponse(ResponseBody response) throws Exception {
            return null;
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onCancel(Throwable e) {

        }

        @Override
        public void onNext(Call call, Object response) {

        }

        @Override
        public Object transform(Object response, Class clazz) throws Exception {
            return null;
        }
    };

}
