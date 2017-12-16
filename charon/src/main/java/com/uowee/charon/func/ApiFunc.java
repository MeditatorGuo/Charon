package com.uowee.charon.func;

import com.google.gson.JsonParseException;
import com.uowee.charon.mode.CharonResponse;

import rx.functions.Func1;

/**
 * Created by Sim.G on 2017/12/8.
 */

public class ApiFunc<T> implements Func1<CharonResponse<T>,T> {

    @Override
    public T call(CharonResponse<T> response) {
         if(response == null || (response.getData() ==null && response.getResult() == null)){
             throw new JsonParseException("Server Data Error");
         }
         return response.getData();
    }
}
