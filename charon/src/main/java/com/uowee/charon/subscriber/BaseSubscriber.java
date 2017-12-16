package com.uowee.charon.subscriber;

import android.accounts.NetworkErrorException;
import android.content.Context;

import com.uowee.charon.exception.CharonException;
import com.uowee.charon.exception.CharonThrowable;
import com.uowee.charon.mode.ApiCode;
import com.uowee.charon.utils.Logger;
import com.uowee.charon.utils.Network;

import java.lang.ref.WeakReference;

import rx.Subscriber;

/**
 * Created by Sim.G on 2017/12/6.
 */

public abstract class BaseSubscriber<T> extends Subscriber<T> {

    protected Context context;
    public BaseSubscriber(Context context) {
          this.context = context;
    }


    @Override
    public void onError(Throwable e) {
        if (e != null && e.getMessage() != null) {
            Logger.v("Charon", e.getMessage());

        } else {
            Logger.v("Charon", "Throwable  || Message == Null");
        }

        if (e instanceof Throwable) {
            Logger.e("Charon", "--> e instanceof Throwable");
            Logger.e("Charon", "--> " + e.getCause().toString());
            onError(e);
        } else {
            Logger.e("Charon", "e !instanceof Throwable");
            String detail = "";
            if (e.getCause() != null) {
                detail = e.getCause().getMessage();
            }
            Logger.e("Charon", "--> " + detail);
            onError(CharonException.handleException(e));
        }
        onCompleted();
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.v("Charon", "--> http is start");
        if (!Network.isConnected(context)) {
            onError(new CharonException(new NetworkErrorException(), ApiCode.Request.NETWORK_ERROR));
        }
    }

    @Override
    public void onCompleted() {
        Logger.v("Charon", "-->http is complete");
    }

    public abstract void onError(CharonThrowable e);

}
