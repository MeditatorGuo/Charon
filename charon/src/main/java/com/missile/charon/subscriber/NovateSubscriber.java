package com.missile.charon.subscriber;

import android.content.Context;

import com.missile.charon.callback.NovateCallback;
import com.missile.charon.exception.ApiException;

/**
 * Created by GuoWee on 2018/6/26.
 */

public class NovateSubscriber<T> extends BaseSubscriber<T> {

    private NovateCallback<T> callback;

    public NovateSubscriber(Context context, NovateCallback<T> callback) {
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
    public void onError(ApiException e) {
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
