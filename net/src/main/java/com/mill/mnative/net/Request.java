package com.mill.mnative.net;

import java.util.Map;
import java.util.concurrent.Future;

public class Request {
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";

    public Object tag;
    public String url;
    public String method;
    public int connectTimeOut = 5000;
    public int readTimeOut = 10000;
    public Map<String, String> strParams;
    public NetCallback callback;
    public Future future;




    @Override
    public String toString() {
        return "{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", connectTimeOut=" + connectTimeOut +
                ", readTimeOut=" + readTimeOut +
                ", strParams=" + strParams +
                '}';
    }
}
