package com.uowee.charon;

/**
 * Created by GuoWee on 2017/10/26 19:37
 */
public final class Config {
    public static String API_HOST = "http://service.1m88.net:8088/";// 默认API主机地址
    public static final int DEFAULT_TIMEOUT = 60;// 默认超时时间
    public static final int DEFAULT_MAX_IDLE_CONNECTIONS = 5;// 默认空闲连接数
    public static final long DEFAULT_KEEP_ALIVE_DURATION = 8;// 默认心跳间隔时长
}
