package com.uowee.charon.callback;

import com.uowee.charon.exception.CharonException;

/**
 * Created by Sim.G on 2017/12/6.
 */

public interface CharonCallback<T> {

    void onStart();

    void onError(CharonException e);

    void onComleted();

    void onNext(T t);

}
