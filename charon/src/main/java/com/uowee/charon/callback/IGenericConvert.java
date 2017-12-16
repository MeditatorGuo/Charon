package com.uowee.charon.callback;

/**
 * Created by Sim.G on 2017/12/16.
 */

public interface IGenericConvert<E> {
    <T> T transform(E response,Class<T> clazz) throws Exception;
}
