package com.faceunity.nama.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.faceunity.nama.entity.Filter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreferenceUtil {

    private static final String FACEUNITY_PARAMS = "faceunity_params";
    public static final String KEY_FILTER_MAP = "filterMap";
    public static final String KEY_FILTER = "filter";

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public static void init(Context context) {
        sharedPreferences = context.getSharedPreferences(FACEUNITY_PARAMS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static void setParam(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public static void setParam(String key, float value) {
        editor.putFloat(key, value);
        editor.apply();

    }

    public static String getStringParam(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public static float getFloatParam(String key, float defValue) {
        return sharedPreferences.getFloat(key, defValue);
    }

    public static void setFilterParams(Map<String, Float> params) {
        List<FilterParam> data = new ArrayList<>();
        for (String key : params.keySet()) {
            FilterParam filter = new FilterParam(key, params.get(key));
            data.add(filter);
        }
        editor.putString(KEY_FILTER_MAP, new Gson().toJson(data));
        editor.apply();
    }

    public static Map<String, Float> getFilterParams() {
        String json = sharedPreferences.getString(KEY_FILTER_MAP, "");
        if (TextUtils.isEmpty(json)) {
            return new HashMap<>(16);
        } else {
            List<FilterParam> data = new Gson().fromJson(json, new TypeToken<List<FilterParam>>(){}.getType());
            if (data == null) {
                return new HashMap<>(16);
            }
            int size = (int) ((int)data.size() / 0.75 + 1);
            Map<String, Float> params = new HashMap<>(size);

            for (FilterParam param : data) {
                params.put(param.name, param.level);
            }
            return params;
        }
    }

    public static void setFilter(Filter filter) {
        editor.putString(KEY_FILTER, new Gson().toJson(filter));
        editor.apply();
    }

    public static Filter getFilter(Filter defValue) {
        String json = sharedPreferences.getString(KEY_FILTER, "");
        if (TextUtils.isEmpty(json)) {
            return defValue;
        }else {
            return new Gson().fromJson(json, Filter.class);
        }
    }

    static class FilterParam {
        private String name;
        private float level;

        public FilterParam(String name, float level) {
            this.name = name;
            this.level = level;
        }
    }
}
