package com.uowee.charon.subscriber;

import android.accounts.NetworkErrorException;
import android.content.Context;

import com.uowee.charon.exception.CharonException;
import com.uowee.charon.mode.ApiCode;
import com.uowee.charon.utils.Network;

import java.lang.ref.WeakReference;

import rx.Subscriber;

/**
 * Created by Sim.G on 2017/12/6.
 */

public abstract class BaseSubscriber<T> extends Subscriber<T> {

    public WeakReference<Context> contextWeakReference;

    public BaseSubscriber(Context context) {
        contextWeakReference = new WeakReference<Context>(context);
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof CharonException) {
            onError((CharonException) e);
        } else {
            onError(new CharonException(e, ApiCode.Request.UNKNOWN));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!Network.isConnected(contextWeakReference.get())) {
            onError(new CharonException(new NetworkErrorException(), ApiCode.Request.NETWORK_ERROR));
        }
    }

    public abstract void onError(CharonException e);

}
