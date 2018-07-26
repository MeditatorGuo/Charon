package com.missile.charon.convert;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.net.UnknownServiceException;

import com.missile.charon.exception.ApiException;
import com.missile.charon.mode.ApiResult;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by GuoWee on 2018/6/26.
 */

public class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        if (adapter != null && gson != null) {
            JsonReader jsonReader = gson.newJsonReader(value.charStream());
            try {
                T data = adapter.read(jsonReader);
                if (data == null) throw new UnknownServiceException("server back data is null");
                if (data instanceof ApiResult) {
                    ApiResult apiResult = (ApiResult) data;
                    if (!ApiException.isSuccess(apiResult)) {
                        throw new UnknownServiceException(apiResult.getMsg() == null ? "unknow error" : apiResult.getMsg());
                    }
                }
                return data;
            } finally {
                value.close();
            }
        } else {
            return null;
        }
    }
}
