package com.missile.charon.subscriber;

import android.content.Context;

import com.missile.charon.callback.ApiCallback;
import com.missile.charon.exception.ApiException;

/**
 * Created by GuoWee on 2018/6/26.
 */

public class ApiSubscriber<T> extends BaseSubscriber<T> {

    private ApiCallback<T> callback;

    public ApiSubscriber(Context context, ApiCallback<T> callback) {
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
