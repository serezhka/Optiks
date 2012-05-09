package com.ifmo.optiks.connection;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import com.ifmo.optiks.provider.OptiksProviderMetaData;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Author: Dudko Alex (dududko@gmail.com)
 * Date: 21.04.12
 */
public class Connector {

    // TODO  move this to Optiks Props
    private final static String TAG = "ConnectorTAG";
    private final static String URI = "http://89.112.11.137:8028/Download";
    private final static HttpClient HTTP_CLIENT = new DefaultHttpClient();
    private final static ResponseHandler<String> RESPONSE_HANDLER = new BasicResponseHandler();
    private final static String GET_SEASONS_COUNT = "getSeasonCount=true";
    private final static String GET_LEVEL_COUNT = "getLevelCount=true";
    private final static String SEASON_ID = "season_id=";
    private final static String GET_SEASON = "getSeason=true";
    private final static String GET_LEVEL = "getLevel=true";

    private final class Fields {
        private final static String SEASON_COUNT = "seasonCount";
        private final static String LEVEL_COUNT = "levelCount";
        private final static String DESCRIPTION = "description";
        private final static String NAME = "name";
        private final static String LEVELS = "levels";
        public final static String ID = "id";

    }

    public class NameDescription {
        public final String name;
        public final String description;

        public NameDescription(final String name, final String description) {
            this.name = name;
            this.description = description;
        }

        @Override
        public String toString() {
            return "NameDescription{" +
                    "description='" + description + '\'' +
                    '}';
        }
    }

    public int getSeasonsCount() throws IOException, JSONException {
        final String url = URI + "?" + GET_SEASONS_COUNT;
        final HttpGet httpGet = new HttpGet(url);
        final String res;
        res = HTTP_CLIENT.execute(httpGet, RESPONSE_HANDLER);
        final JSONObject object = new JSONObject(res);
        return object.getInt(Fields.SEASON_COUNT);

    }

    public int getLevelCount(final int id) throws IOException, JSONException {
        final HttpGet httpGet = new HttpGet(URI + "?" + GET_LEVEL_COUNT + "&" + SEASON_ID + id);
        final String res = HTTP_CLIENT.execute(httpGet, RESPONSE_HANDLER);
        final JSONObject object = new JSONObject(res);
        return object.getInt(Fields.LEVEL_COUNT);
    }

    public NameDescription getNameDescription(final int id) throws IOException, JSONException {
        final HttpGet httpGet = new HttpGet(URI + "?" + GET_SEASON + "&" + SEASON_ID + id);
        final String res = HTTP_CLIENT.execute(httpGet, RESPONSE_HANDLER);
        final JSONObject object = new JSONObject(res);
        return new NameDescription(object.getString(Fields.NAME), object.getString(Fields.DESCRIPTION));
    }

    /**
     * save season in tellephone  from server
     */
    public void saveLevels(final int seasonId, final ContentResolver contentResolver) throws IOException, JSONException {

        final String uri = URI + "?" + GET_LEVEL + "&" + SEASON_ID + seasonId;
        final HttpGet httpGet = new HttpGet(uri);
        final String res = HTTP_CLIENT.execute(httpGet, RESPONSE_HANDLER);
        final JSONObject jsonObject = new JSONObject(res);
        final String description = jsonObject.getString(Fields.DESCRIPTION);
        final String name = jsonObject.getString(Fields.NAME);
        final JSONArray levels = new JSONArray(jsonObject.getString(Fields.LEVELS));


        ContentValues cv = new ContentValues();

        cv.put(OptiksProviderMetaData.SeasonsTable._ID, seasonId);
        cv.put(OptiksProviderMetaData.SeasonsTable.NAME, name);
        cv.put(OptiksProviderMetaData.SeasonsTable.DESCRIPTION, description);
        contentResolver.insert(OptiksProviderMetaData.SeasonsTable.CONTENT_URI, cv);

        cv.clear();


        for (int i = 0, len = levels.length(); i < len; i++) {
            Log.d(TAG, "i = " + i);
            final JSONObject level = levels.getJSONObject(i);
            cv.put(OptiksProviderMetaData.LevelsTable.SEASON_ID, seasonId);
            cv.put(OptiksProviderMetaData.LevelsTable.LEVEL_ID, level.getInt(Fields.ID));
            cv.put(OptiksProviderMetaData.LevelsTable.LEVEL, level + "");
            contentResolver.insert(OptiksProviderMetaData.LevelsTable.CONTENT_URI, cv);
            cv.clear();
        }

    }


    public void updateSeason(final int seasonId, final ContentResolver contentResolver) throws IOException, JSONException {
        final String uri = URI + "?" + GET_LEVEL + "&" + SEASON_ID + seasonId;
        final HttpGet httpGet = new HttpGet(uri);
        final String res = HTTP_CLIENT.execute(httpGet, RESPONSE_HANDLER);
        final JSONObject jsonObject = new JSONObject(res);
        final String description = jsonObject.getString(Fields.DESCRIPTION);
        final String name = jsonObject.getString(Fields.NAME);
        final JSONArray levels = new JSONArray(jsonObject.getString(Fields.LEVELS));

        ContentValues cv = new ContentValues();
        cv.put(OptiksProviderMetaData.SeasonsTable._ID, seasonId);
        cv.put(OptiksProviderMetaData.SeasonsTable.NAME, name);
        cv.put(OptiksProviderMetaData.SeasonsTable.DESCRIPTION, description);
        contentResolver.update(OptiksProviderMetaData.SeasonsTable.CONTENT_URI, cv, OptiksProviderMetaData.SeasonsTable._ID + "=" + seasonId, null);
        cv.clear();


        for (int i = 0, len = levels.length(); i < len; i++) {
            Log.d(TAG, "i = " + i);
            final JSONObject level = levels.getJSONObject(i);
            cv.put(OptiksProviderMetaData.LevelsTable.SEASON_ID, seasonId);
            cv.put(OptiksProviderMetaData.LevelsTable.LEVEL_ID, level.getInt(Fields.ID));
            cv.put(OptiksProviderMetaData.LevelsTable.LEVEL, level + "");
            contentResolver.update(OptiksProviderMetaData.LevelsTable.CONTENT_URI, cv,
                    OptiksProviderMetaData.LevelsTable.LEVEL_ID + "=" + level.getInt(Fields.ID) + " AND "
                            + OptiksProviderMetaData.LevelsTable.SEASON_ID + "=" + seasonId, null);
            cv.clear();
        }

    }

    public void saveOrUpdateLevels(final int seasonId, final ContentResolver contentResolver) throws IOException, JSONException {
        final String uri = URI + "?" + GET_LEVEL + "&" + SEASON_ID + seasonId;
        final HttpGet httpGet = new HttpGet(uri);
        final String res = HTTP_CLIENT.execute(httpGet, RESPONSE_HANDLER);
        final JSONObject jsonObject = new JSONObject(res);
        final String description = jsonObject.getString(Fields.DESCRIPTION);
        final String name = jsonObject.getString(Fields.NAME);
        final JSONArray levels = new JSONArray(jsonObject.getString(Fields.LEVELS));


        ContentValues cv = new ContentValues();
        cv.put(OptiksProviderMetaData.SeasonsTable._ID, seasonId);
        cv.put(OptiksProviderMetaData.SeasonsTable.NAME, name);
        cv.put(OptiksProviderMetaData.SeasonsTable.DESCRIPTION, description);
        final Cursor cursor =
                contentResolver.query(OptiksProviderMetaData.LevelsTable.CONTENT_URI, null, OptiksProviderMetaData.LevelsTable._ID + "=" + seasonId, null, null);
        final boolean checkInsert;
        if (cursor.getCount() == 0) {
            contentResolver.insert(OptiksProviderMetaData.SeasonsTable.CONTENT_URI, cv);
            checkInsert = true;
        } else {
            contentResolver.update(OptiksProviderMetaData.SeasonsTable.CONTENT_URI, cv, OptiksProviderMetaData.SeasonsTable._ID + "=" + seasonId, null);
            checkInsert = false;
        }
        cv.clear();

        for (int i = 0, len = levels.length(); i < len; i++) {
            Log.d(TAG, "i = " + i);
            final JSONObject level = levels.getJSONObject(i);
            cv.put(OptiksProviderMetaData.LevelsTable.SEASON_ID, seasonId);
            cv.put(OptiksProviderMetaData.LevelsTable.LEVEL_ID, level.getInt(Fields.ID));
            cv.put(OptiksProviderMetaData.LevelsTable.LEVEL, level + "");
            if (!checkInsert) {
                contentResolver.update(OptiksProviderMetaData.LevelsTable.CONTENT_URI, cv,
                        OptiksProviderMetaData.LevelsTable.LEVEL_ID + "=" + level.getInt(Fields.ID) + " AND "
                                + OptiksProviderMetaData.LevelsTable.SEASON_ID + "=" + seasonId, null);
            } else {
                contentResolver.insert(OptiksProviderMetaData.LevelsTable.CONTENT_URI, cv);
            }
            cv.clear();
        }

    }


}


