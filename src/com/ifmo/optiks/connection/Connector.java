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
    private final static String SEASONS_COUNT = "seasonsCount=true";
    private final static String GET_LEVEL_COUNT = "getLevelCount=true";
    private final static String SEASON_ID = "season_id=";
    private final static String GET_SEASON = "getSeason=true";

    public class NameDescription {
        public final String name;
        public final String Description;

        public NameDescription(final String name, final String description) {
            this.name = name;
            Description = description;
        }
    }

    public int getSeasonsCount() throws IOException, JSONException {
        final HttpGet httpGet = new HttpGet(URI + "?" + SEASONS_COUNT);
        final String res;
        res = HTTP_CLIENT.execute(httpGet, RESPONSE_HANDLER);
        final JSONObject object = new JSONObject(res);
        return -1;

    }

    /**
     *
     * @param id  id Season
     * @return
     * @throws IOException
     * @throws JSONException
     */

    public int getLevelCount(final int id) throws IOException, JSONException {
        final HttpGet httpGet = new HttpGet(URI + "?" + GET_LEVEL_COUNT + "&" + SEASON_ID + id);
        final String res = HTTP_CLIENT.execute(httpGet, RESPONSE_HANDLER);
        final JSONObject object = new JSONObject(res);
        return -1;
    }

    /**
     *
     * @param id   id Season
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public NameDescription getNameDescription(final int id) throws IOException, JSONException {
        final HttpGet httpGet = new HttpGet(URI + "?" +GET_SEASON + "&" + SEASON_ID + id);
        final String res = HTTP_CLIENT.execute(httpGet, RESPONSE_HANDLER);
        final JSONObject object = new JSONObject(res);
        return null;
    }




}
