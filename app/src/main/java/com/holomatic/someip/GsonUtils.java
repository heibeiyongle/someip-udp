package com.holomatic.someip;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class GsonUtils {
    private static final String TAG = "GsonUtils";
    private static volatile GsonUtils hmiGsonUtil = new GsonUtils();
    private Gson gson;
    private GsonBuilder gsonBuilder;

    private GsonUtils() {
        gson = new Gson();
    }

    public static GsonUtils getInstance() {
        return hmiGsonUtil;
    }

    public <T> T fromJson(final String json, final Class<T> type) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            Object object = gson.fromJson(json, (Type) type);
            return Primitives.wrap(type).cast(object);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toJson(Object object) {
        if (null == object) {
            return "";
        }
        if (gsonBuilder==null){
            gsonBuilder = new GsonBuilder();
        }
        try {
            return gsonBuilder.serializeSpecialFloatingPointValues().create().toJson(object);
        } catch (Exception e) {
            e.printStackTrace();
//            HLog.i(TAG, "### toJson: " + e.getMessage());
//            HLog.i(TAG, "### toJson object== " + object);
            return "";
        }
    }

    public <T> T fromJsonByType(final String json, final TypeToken<T> type) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            return gson.fromJson(json, type.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
