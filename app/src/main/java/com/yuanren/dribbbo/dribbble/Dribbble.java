package com.yuanren.dribbbo.dribbble;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;

import com.yuanren.dribbbo.model.Bucket;
import com.yuanren.dribbbo.model.Comment;
import com.yuanren.dribbbo.model.Like;
import com.yuanren.dribbbo.model.Shot;
import com.yuanren.dribbbo.model.User;
import com.yuanren.dribbbo.utils.ModelUtils;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Dribbble {
    private static final String TAG = "Dribbble API";

    // Dribbble loads everything in a 12-per-page manner
    public static final int COUNT_PER_PAGE = 12;

    private static final String API_URL = "https://api.dribbble.com/v2/";
    private static final String USER_END_POINT = API_URL + "user";
//    private static final String SHOTS_END_POINT = API_URL + "shots";
    private static final String SHOTS_END_POINT = API_URL;
    private static final String BUCKETS_END_POINT = API_URL + "buckets";

    private static final String SP_AUTH = "auth";

    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USER = "user";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_SHOT_ID = "shot_id";

    private static final TypeToken<User> USER_TYPE = new TypeToken<User>(){};
    private static final TypeToken<Shot> SHOT_TYPE = new TypeToken<Shot>(){};
    private static final TypeToken<Bucket> BUCKET_TYPE = new TypeToken<Bucket>(){};
    private static final TypeToken<Like> LIKE_TYPE = new TypeToken<Like>(){};
    private static final TypeToken<List<Shot>> SHOT_LIST_TYPE = new TypeToken<List<Shot>>(){};
    private static final TypeToken<List<Bucket>> BUCKET_LIST_TYPE = new TypeToken<List<Bucket>>(){};
    private static final TypeToken<List<Like>> LIKE_LIST_TYPE = new TypeToken<List<Like>>(){};
    private static final TypeToken<List<Comment>> COMMENT_LIST_TYPE = new TypeToken<List<Comment>>(){};

    private static OkHttpClient client = new OkHttpClient();
    private static String accessToken;
    private static User user;



    /* ---------------------------------------------------------------------------------- */
    /* ---------------------------- init, log in & log out ------------------------------ */
    /* ---------------------------------------------------------------------------------- */

    public static void init(@NonNull Context context){
        accessToken = loadAccessToken(context);
        if (accessToken != null){
            user = loadUser(context);
        }
    }

    public static void login(@NonNull Context context, @NonNull String accessToken) throws IOException {
        Dribbble.accessToken = accessToken;
        storeAccessToken(context, accessToken);

        Dribbble.user = getUser();
        storeUser(context, user);
    }

    public static void logout(@NonNull Context context){
        storeUser(context, null);
        storeAccessToken(context, null);

        accessToken = null;
        user = null;
    }

    public static User getCurrentUser() {
        return user;
    }


    /* ---------------------------------------------------------------------------------- */
    /* ------------------ read or write user & access token from local ------------------ */
    /* ---------------------------------------------------------------------------------- */

    public static String loadAccessToken(@NonNull Context context) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(SP_AUTH, Context.MODE_PRIVATE);
        return sp.getString(KEY_ACCESS_TOKEN, null);
    }

    public static void storeAccessToken(@NonNull Context context, String token){
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(SP_AUTH, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_ACCESS_TOKEN, token).apply();
    }

    public static User loadUser(@NonNull Context context){
        return ModelUtils.read(context, KEY_USER, new TypeToken<User>(){});
    }

    public static void storeUser(@NonNull Context context, User user){
        ModelUtils.save(context, KEY_USER, user);
    }

    public static boolean isLoggedIn(){
        return accessToken != null;
    }


    /* ---------------------------------------------------------------------------------- */
    /* ------------------------------------  user --------------------------------------- */
    /* ---------------------------------------------------------------------------------- */

    public static User getUser() throws IOException, JsonSyntaxException {
        return parseResponse(makeGetRequest(USER_END_POINT), USER_TYPE);
    }

    /* ---------------------------  bucket ------------------------------ */
    /**
     * @return All the buckets for the logged in user
     * @throws IOException, JsonSyntaxException
     */
    public static List<Bucket> getUserBuckets() throws IOException, JsonSyntaxException{
        String url = USER_END_POINT + "/buckets?per_page=" + Integer.MAX_VALUE;

        return new ArrayList<Bucket>();
//        return parseResponse(makeGetRequest(url), BUCKET_LIST_TYPE);
    }

    public  static List<Bucket> getUserBuckets(int page) throws IOException, JsonSyntaxException {
        String url = USER_END_POINT + "/buckets?page=" + page;
//        return parseResponse(makeGetRequest(url), BUCKET_LIST_TYPE);
        return new ArrayList<Bucket>();
    }

    /* ----------------------------  like ------------------------------- */
    public static List<Like> getLikes (int page) throws IOException, JsonSyntaxException{
        String url = USER_END_POINT + "/likes?page=" + page;
//        return parseResponse(makeGetRequest(url), LIKE_LIST_TYPE);
        return new ArrayList<Like>();
    }

    public static List<Shot> getLikedShots(int page) throws IOException, JsonSyntaxException{
        List<Like> likes = getLikes(page);
        List<Shot> likedShots = new ArrayList<>();

        for (Like like : likes){
            likedShots.add(like.getShot());
        }

        return likedShots;
    }

    /* ---------------------------------------------------------------------------------- */
    /* ------------------------------------ shots --------------------------------------- */
    /* ---------------------------------------------------------------------------------- */

    public static Shot getShot(String shotId) throws IOException, JsonSyntaxException{
        String url = SHOTS_END_POINT + "/shots/" + shotId;
        return parseResponse(makeGetRequest(url), SHOT_TYPE);
    }

    public static List<Shot> getShots(int page) throws IOException, JsonSyntaxException {
        String url = SHOTS_END_POINT + "user/shots?page=" + page;
        List<Shot> shots = parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
        User u = user == null ? getUser() : user;

        for (Shot shot: shots) {
            shot.setUser(u);
        }
        return shots;
    }

    /* ---------------------------  bucket ------------------------------ */
    public static List<Bucket> getShotBuckets(@NonNull String shotId) throws IOException, JsonSyntaxException{
        String url = SHOTS_END_POINT + "/" + shotId + "/buckets?per_page=" + Integer.MAX_VALUE;
//        return parseResponse(makeGetRequest(url), BUCKET_LIST_TYPE);
        return new ArrayList<Bucket>();
    }

    /* ----------------------------  like -------------------------------- */
    public static Boolean checkShotLiked(String shotId) throws IOException, JsonSyntaxException{
        String url = SHOTS_END_POINT + "/" + shotId + "/like";
//        Response response = makeGetRequest(url);
//        return response.code() == HttpURLConnection.HTTP_OK ? new Boolean(true) : new Boolean(false);
        return new Boolean(false);
    }

    public static Like likeShot(String shotId) throws IOException, JsonSyntaxException{
        String url = SHOTS_END_POINT + "/" + shotId + "/like";
//        Response response = makePostRequest(url, new FormBody.Builder().build());
//        checkStatusCode(response, HttpURLConnection.HTTP_CREATED);
//        return parseResponse(response, LIKE_TYPE);
        return null;
    }

    public static void unlikeShot(String shotId) throws IOException, JsonSyntaxException{
        String url = SHOTS_END_POINT + "/" + shotId + "/like";
//        Response response = makeDeleteRequest(url, new FormBody.Builder().build());
//        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
    }

    /* -------------------------  comment ------------------------------ */
    public static List<Comment> getShotComments(String shotId, int page) throws IOException, JsonSyntaxException{
        String url = SHOTS_END_POINT + "/" + shotId + "/comments?page=" + page;
//        return parseResponse(makeGetRequest(url), COMMENT_LIST_TYPE);
        Comment c = new Comment();
        c.setId("1");
        c.setBody("Amazing work! Placeholder here...");
        c.setUser(user);
        c.setLikesCount(4);
        c.setCreatedAt(new Date());
        c.setLiked(false);

        List<Comment> list = new ArrayList<Comment>();
        list.add(c);
        return list;
    }

    public static Boolean checkCommentLiked(String shotId, String commentId) throws IOException, JsonSyntaxException{
        String url = SHOTS_END_POINT + "/" + shotId + "/comments/" + commentId + "/like";
//        Response response = makeGetRequest(url);
//        return response.code() == HttpURLConnection.HTTP_OK ? new Boolean(true) : new Boolean(false);
        return new Boolean(false);
    }

    public static Like likeShotComment(String shotId, String commentId) throws IOException, JsonSyntaxException{
        String url = SHOTS_END_POINT + "/" + shotId + "/comments/" + commentId + "/like";
//        Response response = makePostRequest(url, new FormBody.Builder().build());
//        checkStatusCode(response, HttpURLConnection.HTTP_CREATED);
//        return parseResponse(response, LIKE_TYPE);
        return null;
    }

    public static void unlikeShotComment(String shotId, String commentId) throws IOException, JsonSyntaxException{
        String url = SHOTS_END_POINT + "/" + shotId + "/comments/" + commentId + "/like";
//        Response response = makeDeleteRequest(url, new FormBody.Builder().build());
//        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
    }

    /* ---------------------------------------------------------------------------------- */
    /* ------------------------------------ buckets ------------------------------------- */
    /* ---------------------------------------------------------------------------------- */

    public static Bucket newBucket(String name, String description) throws IOException, JsonSyntaxException{
        FormBody formBody = new FormBody.Builder()
                .add(KEY_NAME, name)
                .add(KEY_DESCRIPTION, description)
                .build();
//        return parseResponse(makePostRequest(BUCKETS_END_POINT, formBody), BUCKET_TYPE);
        return null;
    }

    /* ----------------------------  shot ------------------------------- */
    public static List<Shot> getBucketShots(String bucketId, int page) throws IOException, JsonSyntaxException{
        String url = BUCKETS_END_POINT + "/" + bucketId + "/shots?page=" + page;
//        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
        return new ArrayList<Shot>();
    }

    /**
     * Add a shot to a bucket
     * @param bucketId
     * @param shotId
     * @throws IOException
     * @throws JsonSyntaxException
     */
    public static void addShotToBucket(String shotId, String bucketId) throws IOException, JsonSyntaxException{
        String url = BUCKETS_END_POINT + "/" + bucketId + "/shots";

        FormBody formBody = new FormBody.Builder()
                .add(KEY_SHOT_ID, shotId)
                .build();

//        Response response = makePutRequest(url, formBody);
//        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
    }

    /**
     * Remove a shot from a bucket
     * @param bucketId
     * @param shotId
     * @throws IOException
     * @throws JsonSyntaxException
     */
    public static void removeShotFromBucket(String shotId, String bucketId) throws IOException, JsonSyntaxException{
        String url = BUCKETS_END_POINT + "/" + bucketId + "/shots";

        FormBody formBody = new FormBody.Builder()
                .add(KEY_SHOT_ID, shotId)
                .build();

//        Response response = makeDeleteRequest(url, formBody);
//        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
    }


    /* ---------------------------------------------------------------------------------- */
    /* -------------------------------- make http request ------------------------------- */
    /* ---------------------------------------------------------------------------------- */

    private static Response makeGetRequest(String url) throws IOException {
        // build info for GET request
        Request request = authRequestBuilder(url).build();
        return makeRequest(request);
    }

    private static Response makePostRequest(String url, RequestBody requestBody) throws IOException{
        Request request = authRequestBuilder(url)
                .post(requestBody)
                .build();
        return makeRequest(request);
    }

    private static Response makePutRequest(String url, RequestBody requestBody) throws IOException{
        Request request = authRequestBuilder(url)
                .put(requestBody)
                .build();
        return makeRequest(request);
    }

    private static Response makeDeleteRequest(String url, RequestBody requestBody) throws IOException {
        Request request = authRequestBuilder(url)
                .delete(requestBody)
                .build();
        return makeRequest(request);
    }

    private static Request.Builder authRequestBuilder(String url) {
        return new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(url);
    }

    private static Response makeRequest(Request request) throws IOException {
        Response response = client.newCall(request).execute();
        Log.d(TAG, response.header("X-RateLimit-Remaining"));
        return response;
    }

    private static <T> T parseResponse(Response response,
                                       TypeToken<T> typeToken) throws IOException, JsonSyntaxException {
        String responseString = response.body().string();
        Log.d(TAG, responseString);
        return ModelUtils.toObject(responseString, typeToken);
    }

    private static void checkStatusCode(Response response,
                                        int statusCode) throws IOException {
        if (response.code() != statusCode) {
            throw new IOException(response.message());
        }
    }
}

