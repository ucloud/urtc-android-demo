package com.urtcdemo.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyStore;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AppHttpUtil{
    private static final String TAG = "AppHttpUtil";
    private static final String ROOM_GATEWAY_ADDR = "https://pre.urtc.com.cn/uteach";
    private static AppHttpUtil mInstance = new AppHttpUtil() ;

    private AppHttpUtil(){}

    public static AppHttpUtil getInstance() {
        return mInstance;
    }

    private HostnameVerifier hostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    } ;

    public String getTestRoomToken(String userId, String roomid, String appid) throws Exception  {
        String url = ROOM_GATEWAY_ADDR ;
        try {
            MediaType mediaType = MediaType.parse("application/json;charset=utf-8") ;
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(3*1000, TimeUnit.MILLISECONDS)
                    .writeTimeout(3*1000, TimeUnit.MILLISECONDS)
                    .readTimeout(3*1000, TimeUnit.MILLISECONDS)
                    .sslSocketFactory(new SslFactory(null), getTrustManager()).build();

            try {
                JSONObject json = new JSONObject() ;
                json.put("rpc_id", UUID.randomUUID().toString()) ;
                json.put("Action", "rsusergetroomtoken") ;
                json.put("user_id", userId) ;
                json.put("room_id", roomid) ;
                json.put("app_id", appid) ;
                RequestBody postbody  =  RequestBody.create(mediaType, json.toString());
                Log.d(TAG, " jsonbody "+ json.toString()) ;
                Request request = new Request.Builder()
                        .url(url)
                        .post(postbody)
                        .build() ;
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();
            }catch (JSONException e) {
                throw  e ;
            }

        } catch (Exception e) {
           throw  e ;
        }
    }

    private static X509TrustManager getTrustManager() {
        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore)null);
            for (TrustManager tm : tmf.getTrustManagers()) {
                if (tm instanceof X509TrustManager) {
                    return (X509TrustManager) tm;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
