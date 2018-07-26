package com.missile.charon.func;

import com.missile.charon.exception.ApiException;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by GuoWee on 2018/6/26.
 */

public class ApiErrFunc<T> implements Func1<Throwable, Observable<T>> {
    @Override
    public Observable<T> call(Throwable throwable) {
        return Observable.error(ApiException.handleException(throwable));
    }
}
