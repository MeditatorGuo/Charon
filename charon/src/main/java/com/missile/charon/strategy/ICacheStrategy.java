package com.missile.charon.strategy;

import com.missile.charon.cache.ApiCache;
import com.missile.charon.mode.CacheResult;
import rx.Observable;

/**
 * Created by GuoWee on 2018/6/26.
 */

public interface ICacheStrategy<T> {
    <T> Observable<CacheResult<T>> execute(ApiCache apiCache, String cacheKey, Observable<T> source, Class<T> clazz);
}
