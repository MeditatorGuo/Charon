package com.uowee.charon.exception;

/**
 * Created by Sim.G on 2017/12/16.
 */

public class CharonThrowable extends Exception {
     private int code;
     private String message;

    public CharonThrowable(java.lang.Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }

    public CharonThrowable(java.lang.Throwable throwable, int code, String message) {
        super(throwable);
        this.code = code;
        this.message = message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



}
