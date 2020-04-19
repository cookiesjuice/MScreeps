package com.cookiesjuice.mscreeps;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.net.HttpURLConnection;

public class Manager {
    private static final String TOKEN_KEY = "token";
    public static final String SP_KEY = "screeps";
    private static final String SHARD_KEY = "shard";
    private static final String ID_KEY = "id";

    @NonNull private MainActivity activity;

    public Manager(@NonNull MainActivity activity){
        this.activity = activity;
    }

    public String getToken(){
        return activity.getSharedPreferences(SP_KEY, Context.MODE_PRIVATE).getString(TOKEN_KEY, "");
    }

    public void setToken(String token){
        activity.getSharedPreferences(SP_KEY, Context.MODE_PRIVATE).edit().putString(TOKEN_KEY, token).apply();
    }

    public String getShard(){
        return activity.getSharedPreferences(SP_KEY, Context.MODE_PRIVATE).getString(SHARD_KEY, "shard0");
    }

    public void setShard(String shard){
        activity.getSharedPreferences(SP_KEY, Context.MODE_PRIVATE).edit().putString(SHARD_KEY, shard).apply();
    }

    public String getId(){
        return activity.getSharedPreferences(SP_KEY, Context.MODE_PRIVATE).getString(ID_KEY, "");
    }

    public void setId(String id){
        activity.getSharedPreferences(SP_KEY, Context.MODE_PRIVATE).edit().putString(ID_KEY, id).apply();
    }

    public String accessMemory(){
        return "";
    }

}
