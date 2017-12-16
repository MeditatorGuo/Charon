package com.uowee.charon.subscriber;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.uowee.charon.callback.ResponseCallback;
import com.uowee.charon.config.ConfigLoader;
import com.uowee.charon.exception.CharonException;
import com.uowee.charon.exception.CharonThrowable;
import com.uowee.charon.exception.FormatException;
import com.uowee.charon.exception.ServerException;
import com.uowee.charon.mode.CharonResponse;
import com.uowee.charon.utils.Logger;
import com.uowee.charon.utils.ReflectionUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;

import static com.uowee.charon.Charon.TAG;


/**
 * Created by Sim.G on 2017/12/6.
 */

public class CharonSubscriber<T> extends BaseSubscriber<ResponseBody> {

    protected ResponseCallback<T> callback;
    private Type finalNeedType;

    public CharonSubscriber(Context context, ResponseCallback callback) {
        super(context);
        this.callback = callback;
    }


    @Override
    public void onStart() {
        super.onStart();
        Type[] types = ReflectionUtil.getParameterizedTypeswithInterfaces(callback);
        if (ReflectionUtil.methodHandler(types) == null || ReflectionUtil.methodHandler(types).size() == 0) {
            throw new NullPointerException("callBack<T> 中T不合法");
        }
        finalNeedType = ReflectionUtil.methodHandler(types).get(0);
        if (callback != null) {
            callback.onStart();
        }
    }

    @Override
    public void onCompleted() {
        if (callback != null) {
            callback.onCompleted();
        }
    }

    @Override
    public void onError(CharonThrowable e) {
        if (callback != null) {
            callback.onError(e);
        }
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        try {
            byte[] bytes = responseBody.bytes();
            String jsonStr = new String(bytes);
            Logger.e(TAG,"JSON :" + jsonStr);
            if (callback != null) {
                int code = 1;
                String msg = "";
                String dataStr = "";
                T dataResponse = null;
                CharonResponse<T> baseResponse = null;
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr.trim());

                    code = jsonObject.optInt("code");
                    Logger.e(TAG,"CODE>>>>>>" +  code);
                    msg = jsonObject.optString("msg");
                    if (TextUtils.isEmpty(msg)) {
                        msg = jsonObject.optString("message");
                    }
                    if (TextUtils.isEmpty(msg)) {
                        msg = jsonObject.optString("error");
                    }
                    baseResponse = new CharonResponse<>();
                    baseResponse.setCode(code);
                    baseResponse.setMessage(msg);
                    dataStr = jsonObject.opt("data").toString();
                    Logger.e(TAG,"DataStr>>>>>>>>>>>>>>>>>>>>" + dataStr);

                    if (dataStr.isEmpty()) {
                        dataStr = jsonObject.optString("result");
                    }
                    if (dataStr.isEmpty()) {
                        baseResponse.setResult(null);
                    }

                    if (!dataStr.isEmpty() && dataStr.charAt(0) == '{') {
                        dataStr = jsonObject.optJSONObject("data").toString();

                        if (dataStr.isEmpty()) {
                            dataStr = jsonObject.optJSONObject("result").toString();
                        }
                      //  Logger.e(TAG,"DataStr>>>>>>>>>>>>>>>>>>>>" + dataStr);
                        dataResponse = (T) new Gson().fromJson(dataStr, ReflectionUtil.newInstance(finalNeedType).getClass());
                        if (ConfigLoader.isFormat(context) && dataResponse == null) {
                            Logger.e(TAG, "dataResponse 无法解析为:" + finalNeedType);
                            throw new FormatException();
                        }
                    } else if (!dataStr.isEmpty() && dataStr.charAt(0) == '[') {
                        throw new ClassCastException();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onError(CharonException.handleException(e));
                    }
                }

                if (dataResponse != null) {
                    baseResponse.setData(dataResponse);
                }

                if (dataResponse == null && baseResponse.isOk(context)) {
                    Logger.d(TAG, "Response data 数据获取失败！");
                    callback.onSuccess(code, msg, null, jsonStr);
                    return;
                }

                if (ConfigLoader.isFormat(context) && baseResponse == null) {
                    Logger.e(TAG, "dataResponse 无法解析为:" + finalNeedType);
                    throw new FormatException();
                }

                baseResponse.setData(dataResponse);
                if (baseResponse.isOk(context)) {
                    callback.onSuccess(code, msg, dataResponse, jsonStr);
                    callback.onSuccess(baseResponse);
                } else {
                    msg = baseResponse.getMsg() != null ? baseResponse.getMsg() :
                            (baseResponse.getError() != null ? baseResponse.getError() :
                                    (baseResponse.getMessage() != null ? baseResponse.getMessage() : "API未知异常"));
                    ServerException serverException = new ServerException(baseResponse.getCode(), msg);
                    callback.onError(CharonException.handleException(serverException));

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onError(CharonException.handleException(e));
            }
        }
    }
}
