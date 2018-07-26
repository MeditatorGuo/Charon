package com.missile.charon.callback;

import com.missile.charon.exception.ApiException;

/**
 * Created by GuoWee on 2018/6/26.
 */

public abstract class NovateCallback<T> {
    public abstract void onStart();

    public abstract void onError(ApiException e);

    public abstract void onCompleted();

    public abstract void onNext(T response);
}
