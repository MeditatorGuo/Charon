package com.uowee.charon.config;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Sim.G on 2017/12/16.
 */

public class Config {
    private String isFormat;
    HashMap<String,String> error;
    private List<String> successCode;

    public String getIsFormat() {
        return isFormat;
    }

    public void setIsFormat(String isFormat) {
        this.isFormat = isFormat;
    }

    public HashMap<String, String> getError() {
        return error;
    }

    public void setError(HashMap<String, String> error) {
        this.error = error;
    }

    public List<String> getSuccessCode() {
        return successCode;
    }

    public void setSuccessCode(List<String> successCode) {
        this.successCode = successCode;
    }
}
