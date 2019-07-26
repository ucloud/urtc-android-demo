package com.urtcdemo.whiteboard;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RemoteAPI {

    public static final RemoteAPI instance = new RemoteAPI();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final Gson gson = new Gson();

    private final String sdkToken;
    private final String host = "https://cloudcapiv4.herewhite.com";
    private final OkHttpClient client = new OkHttpClient();

    private RemoteAPI() {
        // 请将如下 sdkToken 替换成您自己的
        this.sdkToken = "WHITEcGFydG5lcl9pZD1oWnNnc2I3VUJTZHZNdE1hejhLUGlsVGVIVHd0QmRzeWtJWDUmc2lnPTNhMzI5NWJiMDRhYTA4MWNkYjllNmY1NWY4ZGQ1MjcxYWE4MDg4MjA6YWRtaW5JZD0zMDAmcm9sZT1taW5pJmV4cGlyZV90aW1lPTE1OTQzMDc1MTQmYWs9aFpzZ3NiN1VCU2R2TXRNYXo4S1BpbFRlSFR3dEJkc3lrSVg1JmNyZWF0ZV90aW1lPTE1NjI3NTA1NjImbm9uY2U9MTU2Mjc1MDU2MTk3MDAw";
    }

    public interface Callback {
        void success(String uuid, String roomToken);
        void fail(String errorMessage);
    }

    public void createRoom(String name, final Callback callback) {
        Map<String, Object> params = new HashMap<>();

        params.put("name", name);
        params.put("limit", 0); // 0 表示没有限制
        params.put("mode", "historied");

        Request request = new Request.Builder().url(this.host + "/room?token=" + this.sdkToken)
                                               .post(RequestBody.create(JSON, gson.toJson(params)))
                                               .build();
        Call call = this.client.newCall(request);
        call.enqueue(new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                callback.fail(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    JsonObject roomJSON = gson.fromJson(response.body().string(), JsonObject.class);

                    String uuid = roomJSON.getAsJsonObject("msg").getAsJsonObject("room").get("uuid").getAsString();
                    String roomToken = roomJSON.getAsJsonObject("msg").get("roomToken").getAsString();

                    callback.success(uuid, roomToken);

                } else {
                    callback.fail(response.body().string());
                }
            }
        });
    }

    public void getRoom(final String uuid, final Callback callback) {
        RequestBody body = RequestBody.create(JSON, gson.toJson(new HashMap<>()));
        Request request = new Request.Builder().url(this.host + "/room/join?uuid=" + uuid + "&token=" + this.sdkToken)
                                               .post(body).build();
        Call call = this.client.newCall(request);
        call.enqueue(new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                callback.fail(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    JsonObject roomJSON = gson.fromJson(response.body().string(), JsonObject.class);
                    String roomToken = roomJSON.getAsJsonObject("msg").get("roomToken").getAsString();

                    callback.success(uuid, roomToken);

                } else {
                    callback.fail(response.body().string());
                }
            }
        });
    }
}
