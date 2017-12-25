package com.uowee.charon.subscriber;

import android.content.Context;

import com.uowee.charon.callback.CharonCallback;
import com.uowee.charon.exception.CharonThrowable;

import okhttp3.ResponseBody;

/**
 * Created by Sim.G on 2017/12/17.
 */

public class CharonSubscriber<T,E> extends BaseSubscriber<ResponseBody> {

    private Context context;
    private CharonCallback<T,E> callback;

    public CharonSubscriber(CharonCallback<T,E> callback){
        super();
        if(callback == null){
            this.callback =CharonCallback.DEFAULT_CALLBACK ;
        }else {
            this.callback = callback;
        }
    }

    public CharonSubscriber setContext(Context context){
        this.context = context;
        return this;
    }
    public Context getContext(){
        return context;
    }

    @Override
    public void onError(CharonThrowable e) {

    }

    @Override
    public void onNext(ResponseBody responseBody) {

    }
}
