package com.uowee.charon.callback;

import com.uowee.charon.exception.CharonException;

/**
 * Created by Sim.G on 2017/12/6.
 */

public abstract class CharonCallback<T> {

    public abstract void onStart();

    public abstract void onError(CharonException e);

    public abstract void onCompleted();

    public abstract void onNext(T response);


}
