package com.missile.charon.subscriber;

import android.accounts.NetworkErrorException;
import android.content.Context;

import java.lang.ref.WeakReference;

import com.missile.charon.exception.ApiException;
import com.missile.charon.mode.ApiCode;
import com.missile.charon.utils.NetworkUtil;
import rx.Subscriber;

/**
 * Created by GuoWee on 2018/6/26.
 */

public abstract class BaseSubscriber<T> extends Subscriber<T> {
    public WeakReference<Context> contextWeakReference;

    public BaseSubscriber(Context context) {
        contextWeakReference = new WeakReference<Context>(context);
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof ApiException) {
            onError((ApiException) e);
        } else {
            onError(new ApiException(e, ApiCode.Request.UNKNOWN));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!NetworkUtil.isConnected(contextWeakReference.get())) {
            onError(new ApiException(new NetworkErrorException(), ApiCode.Request.NETWORK_ERROR));
        }
    }

    public abstract void onError(ApiException e);
}
