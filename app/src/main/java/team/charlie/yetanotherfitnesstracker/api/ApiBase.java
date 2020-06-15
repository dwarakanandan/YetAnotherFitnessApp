package team.charlie.yetanotherfitnesstracker.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Header;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import team.charlie.yetanotherfitnesstracker.R;

public class ApiBase {

    private static final String TAG = "ApiBase";

    static final String URL_BASE = "18.212.181.32";
    //static final String URL_BASE = "192.168.0.100:3000";
    static final String SET_COOKIE = "Set-Cookie";
    private static final String COOKIE = "Cookie";
    public static final String SET_COOKIE_REMEMBER_ME = "remember_me";
    public static final String SET_COOKIE_CONNECT_SID = "connect.sid";
    RequestQueue queue;
    SharedPreferences sharedPref;

    ApiBase(Context context) {
        queue = Volley.newRequestQueue(context);
        sharedPref = context.getApplicationContext().getSharedPreferences(
                context.getApplicationContext().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    Map<String, String> getCookies() {
        Map<String, String> headers = new HashMap<>();
        String cookie = SET_COOKIE_REMEMBER_ME + "=" + sharedPref.getString(SET_COOKIE_REMEMBER_ME, "")
                + "; " + SET_COOKIE_CONNECT_SID + "=" + sharedPref.getString(SET_COOKIE_CONNECT_SID, "");
        headers.put(COOKIE, cookie);
        headers.put("Cache-Control", "no-cache");
        headers.put("Connection", "keep-alive");
        return headers;
    }

    void parseNetworkResponseForCookies(NetworkResponse response) {
        List<Header> allHeaders = response.allHeaders;
        for (Header header:allHeaders) {
            if (header.getName().equals(SET_COOKIE)) {
                SharedPreferences.Editor editor = sharedPref.edit();
                if(header.getValue().startsWith(SET_COOKIE_REMEMBER_ME)) {
                    String[] headerValues = header.getValue().split(";")[0].split("=");
                    if (headerValues.length >= 2) {
                        String  headerValue = headerValues[1];
                        editor.putString(SET_COOKIE_REMEMBER_ME, headerValue);
                        Log.d(TAG, "parseNetworkResponse: remember_me=" + headerValue);
                    }
                }
                if(header.getValue().startsWith(SET_COOKIE_CONNECT_SID)) {
                    String  headerValue = header.getValue().split(";")[0].split("=")[1];
                    editor.putString(SET_COOKIE_CONNECT_SID, headerValue);
                    Log.d(TAG, "parseNetworkResponse: connect.sid=" + headerValue);
                }
                editor.apply();
            }
        }
    }

    void removeCookies() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(SET_COOKIE_CONNECT_SID);
        editor.remove(SET_COOKIE_REMEMBER_ME);
        editor.apply();
    }
}

