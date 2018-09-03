package com.uowee.sample.app.clean;

/**
 * Created by GuoWee on 2018/7/2.
 */

public interface UseCaseScheduler {
    void execute(Runnable runnable);

    <V extends UseCase.ResponseValue> void notifyResponse(final V response,
                                                          final UseCase.UseCaseCallback<V> useCaseCallback);

    <V extends UseCase.ResponseValue> void onError(
            final UseCase.UseCaseCallback<V> useCaseCallback);
}
