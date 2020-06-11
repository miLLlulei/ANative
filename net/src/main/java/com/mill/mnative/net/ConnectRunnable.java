package com.mill.mnative.net;


import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectRunnable implements Runnable {
    private Request mRequest;
    private ConnectDispatch mDispatch;

    public ConnectRunnable(Request mRequest, ConnectDispatch dispatch) {
        this.mRequest = mRequest;
        this.mDispatch = dispatch;
    }

    @Override
    public void run() {
        if (mRequest != null) {
            if (Request.METHOD_GET.equals(mRequest.method)) {
                doGet();
            } else if (Request.METHOD_POST.equals(mRequest.method)) {
                doPost();
            }
        }
        mDispatch.removeRequest(mRequest);
    }

    public void doGet() {
        HttpURLConnection connection = null;
        InputStream in = null;
        BufferedReader reader = null;
        try {
            if (HttpClientImp.isDebug) {
                Log.i(HttpClientImp.TAG, "Request: " + mRequest.toString());
            }
            URL url = new URL(MnativeNetUtils.buildUrl(mRequest.url, mRequest.strParams));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(mRequest.method);
            connection.setConnectTimeout(mRequest.connectTimeOut);
            connection.setReadTimeout(mRequest.readTimeOut);
            connection.connect();


            in = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while (null != (line = reader.readLine())) {
                response.append(line);
            }

            if (HttpClientImp.isDebug) {
                Log.i(HttpClientImp.TAG, "response: " + response.toString());
            }
            if (connection.getResponseCode() < 400) {
                mDispatch.notifySuccess(mRequest, response.toString());
            } else {
                mDispatch.notifyFail(mRequest, "net error");
            }
        } catch (Exception e) {
            if (HttpClientImp.isDebug) {
                Log.d(HttpClientImp.TAG, "response error" + e.toString());
            }
            mDispatch.notifyFail(mRequest, "net error" + e.toString());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public void doPost() {
        HttpURLConnection connection = null;
        InputStream in = null;
        BufferedReader reader = null;
        try {
            if (HttpClientImp.isDebug) {
                Log.i(HttpClientImp.TAG, "Request: " + mRequest.toString());
            }
            URL url = new URL(mRequest.url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(mRequest.method);
            connection.setConnectTimeout(mRequest.connectTimeOut);
            connection.setReadTimeout(mRequest.readTimeOut);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.connect();

            OutputStream outputStream = connection.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeBytes(MnativeNetUtils.buildStr(mRequest.strParams));
            dataOutputStream.close();

            in = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while (null != (line = reader.readLine())) {
                response.append(line);
            }

            if (HttpClientImp.isDebug) {
                Log.i(HttpClientImp.TAG, "response: " + response.toString());
            }
            if (connection.getResponseCode() < 400) {
                mDispatch.notifySuccess(mRequest, response.toString());
            } else {
                mDispatch.notifyFail(mRequest, "net error");
            }
        } catch (Exception e) {
            if (HttpClientImp.isDebug) {
                Log.i(HttpClientImp.TAG, "response error " + e.toString());
                e.printStackTrace();
            }
            mDispatch.notifyFail(mRequest, "net error" + e.toString());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
