package com.uowee.charon.subscriber;

import android.content.Context;

import com.uowee.charon.callback.CharonCallback;
import com.uowee.charon.exception.CharonException;

/**
 * Created by Sim.G on 2017/12/17.
 */

public class CharonSubscriber<T> extends BaseSubscriber<T> {

    private CharonCallback<T> callback;

    public CharonSubscriber(Context context, CharonCallback<T> callback) {
        super(context);
        if (callback == null) {
            throw new NullPointerException("this callback is null!");
        }
        this.callback = callback;
    }

    @Override
    public void onStart() {
        super.onStart();
        callback.onStart();
    }

    @Override
    public void onError(CharonException e) {
        callback.onError(e);
    }

    @Override
    public void onCompleted() {
        callback.onCompleted();
    }

    @Override
    public void onNext(T t) {
        callback.onNext(t);
    }
}
