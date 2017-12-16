package com.uowee.charon.config;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.uowee.charon.utils.FileUtil;
import com.uowee.charon.utils.Logger;

import java.util.HashMap;

/**
 * Created by Sim.G on 2017/12/16.
 */

public class ConfigLoader {

    private static Config config;

    private final static String CONFIG_NAME = "charon-config.json";

    private static ConfigLoader instance;
    private static Context appContext;
    /**
     * @return Application Context
     */
    public static Context getContext() {
        return appContext;
    }

    /**
     * set Application Context
     */
    public static void init(Context context) {
        appContext = context;
    }

    public static boolean checkSucess(Context context, int code) {

        if (loadConfig(context) == null) {
            return true;
        }
        return config.getSuccessCode().contains(String.valueOf(code));
    }

    public static Config loadConfig(Context context) {

        if (config != null) {
            return config;
        }
        String jsonStr = FileUtil.loadFromAssets(context, CONFIG_NAME);
        if (TextUtils.isEmpty(jsonStr)) {
            Logger.e("Charon", "缺乏默认配置 <" + CONFIG_NAME + ">文件，请加入");
            return null;
        }
        try {
            config = new Gson().fromJson(jsonStr, Config.class);
        } catch (JsonSyntaxException exception) {
            Logger.e("Charon", "loaderConfig 配置数据无法解析: 请正确配置 <" + CONFIG_NAME + ">文件");
            return null;

        }
        return config = new Gson().fromJson(jsonStr, Config.class);
    }

    public static boolean isFormat(Context context) {
        if (loadConfig(context) == null) {
            return false;
        }
        return TextUtils.equals(config.getIsFormat(), "true");
    }

    public static HashMap<String, String> getErrorConfig() {
        return config.getError();
    }



}
