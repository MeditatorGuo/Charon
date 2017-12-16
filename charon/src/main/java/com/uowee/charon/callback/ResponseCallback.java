package com.uowee.charon.callback;

import com.uowee.charon.mode.CharonResponse;

/**
 * Created by Sim.G on 2017/12/16.
 */
@Deprecated
public interface ResponseCallback<T> {
      void onStart();
      void onCompleted();
      void onError(Throwable e);
      @Deprecated
      void onSuccess(CharonResponse<T> response);
      void onSuccess(int code,String msg,T response,String origResponse);



}
