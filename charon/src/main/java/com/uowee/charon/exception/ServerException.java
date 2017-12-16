package com.uowee.charon.exception;

/**
 * Created by Sim.G on 2017/12/16.
 */

public class ServerException extends RuntimeException {

    public int code;
    public String message;
    public ServerException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }


}
