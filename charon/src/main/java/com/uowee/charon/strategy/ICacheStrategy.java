package com.uowee.charon.strategy;

import com.uowee.charon.cache.CharonCache;
import com.uowee.charon.mode.CacheResult;

import rx.Observable;

/**
 * Created by GuoWee on 2018/1/10.
 */

public interface ICacheStrategy<T> {

    <T> Observable<CacheResult<T>> execute(CharonCache apiCache, String cacheKey, Observable<T> source, Class<T> clazz);
}
