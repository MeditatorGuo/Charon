package com.missile.charon.strategy;

import com.missile.charon.cache.ApiCache;
import com.missile.charon.mode.CacheResult;
import rx.Observable;


public class OnlyCacheStrategy<T> extends CacheStrategy<T> {
    @Override
    public <T> Observable<CacheResult<T>> execute(ApiCache apiCache, String cacheKey, Observable<T> source, Class<T> clazz) {
        return loadCache(apiCache, cacheKey, clazz);
    }
}
