package com.uowee.charon.callback;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ResponseBody;

/**
 * Created by Sim.G on 2017/12/6.
 */

public abstract class CharonCallback<T, E> implements Callback, IGenericConvert<E> {
    protected String TAG = "CharonCallback";
    protected Object tag;
    protected Handler handler;
    private Context context;

    public CharonCallback(Object tag) {
        this.tag = tag;
    }

    public CharonCallback() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public abstract T onHandleResponse(ResponseBody response) throws Exception;

    public abstract void onError(Object tag, Throwable e);

    public abstract void onCancel(Object tag, Throwable e);

    public abstract void onNext(Object tag, Call call, T response);


}
