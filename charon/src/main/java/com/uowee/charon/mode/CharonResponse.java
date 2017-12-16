package com.uowee.charon.mode;

import android.content.Context;

import com.uowee.charon.config.ConfigLoader;

/**
 * Created by Sim.G on 2017/12/16.
 */

public class CharonResponse<T> {
    // result code
    private int code;
    // error message
    private String msg, error, message;
    // response data
    private T data, result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }


    public boolean isOk(Context context) {
        return ConfigLoader.checkSucess(context, getCode());
    }

    @Override
    public String toString() {
        return "CharonResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", result=" + result +
                '}';
    }
}
