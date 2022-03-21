package com.yuanren.dribbbo.dribbble.auth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Auth {

    public static final int REQ_CODE = 100;

    private static final String KEY_CLIENT_ID = "client_id";
    private static final String KEY_CLIENT_SECRET = "client_secret";
    private static final String KEY_REDIRECT_URI = "redirect_uri";
    private static final String KEY_SCOPE = "scope";
    private static final String KEY_CODE = "code";
    private static final String KEY_ACCESS_TOKEN = "access_token";

    private static final String CLIENT_ID = "52d6de5071dc59eaf940db4975328bd68a0ed29598656284323d3b39f9fc3dd2";
    private static final String CLIENT_SECRET = "007ad0dbf96f216a3fee232982f2d5f6e955b891871f0cf89f0fb69b2d2c3106";
    private static final String SCOPE = "public+write";

    private static final String URI_AUTHORIZE = "https://dribbble.com/oauth/authorize";
    private static final String URI_TOKEN = "https://dribbble.com/oauth/token";
//    public static final String REDIRECT_URI = "http://www.dribbbo.com";
    public static final String REDIRECT_URI = "http://www.google.com";

    public static void openAuthActivity(@NonNull Activity activity){
        Intent intent = new Intent(activity, AuthActivity.class);
        intent.putExtra(AuthActivity.KEY_URL, getAuthorizeUrl());
        activity.startActivityForResult(intent, REQ_CODE);
    }

    private static String getAuthorizeUrl() {
        String url = Uri.parse(URI_AUTHORIZE)
                .buildUpon()
                .appendQueryParameter(KEY_CLIENT_ID, CLIENT_ID)
                .build()
                .toString();

        url += "&" + KEY_REDIRECT_URI + "=" + REDIRECT_URI;
        url += "&" + KEY_SCOPE + "=" + SCOPE;

        System.out.println(url);
        return url;
    }

    public static String fetchAccessToken(String authCode)throws IOException {
        OkHttpClient client = new OkHttpClient();

        RequestBody postBody = new FormBody.Builder()
                .add(KEY_CLIENT_ID, CLIENT_ID)
                .add(KEY_CLIENT_SECRET, CLIENT_SECRET)
                .add(KEY_CODE, authCode)
                .add(KEY_REDIRECT_URI, REDIRECT_URI)
                .build();

        Request request = new Request.Builder()
                .url(URI_TOKEN)
                .post(postBody)
                .build();

        Response response = client.newCall(request).execute();
        String responseString = response.body().string();

        try {
            JSONObject obj = new JSONObject(responseString);

            return obj.getString(KEY_ACCESS_TOKEN);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

}
