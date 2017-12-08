package com.uowee.charon.func;

import com.uowee.charon.exception.CharonException;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Sim.G on 2017/12/8.
 */

public class ApiErrFunc<T> implements Func1<Throwable, Observable<T>> {
    @Override
    public Observable<T> call(Throwable throwable) {
        return Observable.error(CharonException.handleException(throwable));
    }
}
