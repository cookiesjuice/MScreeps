package com.cookiesjuice.mscreeps;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class HttpClient {
    private final String GET = "GET";
    private final String POST = "POST";

    private final String BASE_URL = "https://screeps.com";
    private final String MEMORY_SEGMENT = "/api/user/memory-segment";
    private final String CONSOLE = "/api/user/console";
    private final String USER_DATA = "/api/auth/me";

    private MainActivity activity;

    private static ArrayList<HttpClient> instances;

    static{
        instances = new ArrayList<>();
    }

    public HttpClient(MainActivity activity){
        this.activity = activity;
    }

    public String console(String expression) throws IOException {
        URL url = new URL(BASE_URL + CONSOLE);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(POST);
        connection.setRequestProperty("X-Token", activity.getManager().getToken());
        // sending request
        connection.setDoOutput(true);
        OutputStream oStream = connection.getOutputStream();
        oStream.write(("expression=" + expression + "&shard=" + activity.getManager().getShard()).getBytes());
        oStream.flush();
        oStream.close();
        //receiving data
        int responseCode = connection.getResponseCode();

        if(responseCode == HttpURLConnection.HTTP_OK){
            InputStream iStream = connection.getInputStream();
            StringBuilder stringBuilder = new StringBuilder();
            while(true){
                int c = iStream.read();
                if(c == -1){
                    break;
                }
                stringBuilder.append((char) c);
            }
            return stringBuilder.toString();
        }
        throw new IOException(String.valueOf(responseCode));
    }

    public String getUserId() throws Exception {
        URL url = new URL(BASE_URL + USER_DATA);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(GET);
        connection.setRequestProperty("X-Token", activity.getManager().getToken());
        connection.setRequestProperty("X-Username", activity.getManager().getToken());
        int responseCode = connection.getResponseCode();
        if(responseCode == HttpURLConnection.HTTP_OK){
            InputStream iStream = connection.getInputStream();
            StringBuilder stringBuilder = new StringBuilder();
            while(true){
                int c = iStream.read();
                if(c == -1){
                    break;
                }
                stringBuilder.append((char) c);
            }
            JSONObject object = new JSONObject(stringBuilder.toString());
            return object.getString("_id");
        }
        throw new IOException(String.valueOf(responseCode));
    }

    public String readMemorySegment() throws Exception {
        URL url = new URL(BASE_URL + MEMORY_SEGMENT + "?segment=0&shard="+activity.getManager().getShard());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(GET);
        connection.setRequestProperty("X-Token", activity.getManager().getToken());
        int responseCode = connection.getResponseCode();
        if(responseCode == HttpURLConnection.HTTP_OK){
            InputStream iStream = connection.getInputStream();
            StringBuilder stringBuilder = new StringBuilder();
            while(true){
                int c = iStream.read();
                if(c == -1){
                    break;
                }
                stringBuilder.append((char) c);
            }
            iStream.close();
            return stringBuilder.toString();
        }
        throw new IOException(String.valueOf(responseCode));
    }
}

