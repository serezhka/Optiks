package com.ifmo.optiks.connection;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Author: Dudko Alex (dududko@gmail.com)
 * Date: 21.04.12
 */
public class Connector {
    private final static String URI = "http://89.112.11.137:8028/Dowload";
    private final static HttpClient HTTP_CLIENT = new DefaultHttpClient();
    private final static ResponseHandler<String> RESPONSE_HANDLER = new BasicResponseHandler();
    private final static String SEASONS_COUNT = "seasonsCount=1";
    private final static String GET_LEVEL_COUNT = "getLevelCount=1";
    private final static String SEASON_ID = "season_id";


    public int getCountSeasons() {
        final HttpGet httpGet = new HttpGet(URI + "?"+SEASONS_COUNT);
        final String res;
        try {
            res = HTTP_CLIENT.execute(httpGet, RESPONSE_HANDLER);
        } catch (IOException e) {
            return -1;
        }
        try {
            final JSONObject object = new JSONObject(res);
            return object.getInt(SEASONS_COUNT);
        } catch (JSONException e) {
            return -1;
        }
    }

    public int   getLevelCount(final int id){
        final HttpGet httpGet = new HttpGet(URI + "?"+GET_LEVEL_COUNT+"&season_id="+id);
        final String res;
        try {
            res = HTTP_CLIENT.execute(httpGet, RESPONSE_HANDLER);
        } catch (IOException e) {
            return -1;
        }
        try {
            final JSONObject object = new JSONObject(res);
            return object.getInt(SEASONS_COUNT);
        } catch (JSONException e) {
            return -1;
        }
    }


    /*private String execute(final HttpGet httpGet){

    }*/
}
