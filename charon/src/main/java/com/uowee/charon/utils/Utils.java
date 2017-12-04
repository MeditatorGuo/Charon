package com.uowee.charon.utils;

/**
 * Created by GuoWee on 2017/10/26 18:04
 */
public class Utils {

    public static <T> T checkNotNull(T t, String message) {
        if (t == null) {
            throw new NullPointerException(message);
        }
        return t;
    }
}
