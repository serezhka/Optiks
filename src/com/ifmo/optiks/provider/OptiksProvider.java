package com.ifmo.optiks.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.ifmo.optiks.R;
import com.ifmo.optiks.base.gson.Fields;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */

public class OptiksProvider extends ContentProvider {

    private final static String TAG = "OptiksProviderTAG";

    private DatabaseHelper openHelper;

    private class DatabaseHelper extends SQLiteOpenHelper {

        /**
         * Create a helper object to create, open, and/or manage a database.
         * This method always returns very quickly.  The database is not actually
         * created or opened until one of {@link #getWritableDatabase} or
         * {@link #getReadableDatabase} is called.
         *
         * @param context to use to open or create the database
         * @param name    of the database file, or null for an in-memory database
         * @param factory to use for creating cursor OBJECTS, or null for the default
         * @param version number of the database (starting at 1); if the database is older,
         *                {@link #onUpgrade} will be used to upgrade the database
         */
        public DatabaseHelper(final Context context, final String name, final SQLiteDatabase.CursorFactory factory, final int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(final SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + OptiksProviderMetaData.CookieTable.TABLE_NAME + " (" +
                    OptiksProviderMetaData.CookieTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + OptiksProviderMetaData.CookieTable.NAME + " TEXT, " +
                    OptiksProviderMetaData.CookieTable.VALUE + " TEXT);");

            db.execSQL("CREATE TABLE " + OptiksProviderMetaData.SeasonsTable.TABLE_NAME + " (" +
                    OptiksProviderMetaData.SeasonsTable._ID + " INTEGER PRIMARY KEY , "   +
                     OptiksProviderMetaData.SeasonsTable.NAME + " TEXT, " +
                    OptiksProviderMetaData.SeasonsTable.MAX_LEVEL_REACHED +" INTEGER ,"+
                    OptiksProviderMetaData.SeasonsTable.DESCRIPTION + " TEXT);");

            db.execSQL("CREATE TABLE " + OptiksProviderMetaData.LevelsTable.TABLE_NAME + " (" +
                    OptiksProviderMetaData.LevelsTable._ID + " INTEGER PRIMARY KEY , "
                    + OptiksProviderMetaData.LevelsTable.LEVEL + " TEXT , " +
                    OptiksProviderMetaData.LevelsTable.SEASON_ID + " INTEGER );");

            final InputStream is = getContext().getResources().openRawResource(R.raw.levels);
            final Scanner sc = new Scanner(is);
            sc.useDelimiter("\n");
            final StringBuffer sb = new StringBuffer();
            while (sc.hasNext()) {
                sb.append(sc.next());
            }
            final ContentValues cv = new ContentValues();
            try {
                final JSONArray seasonArray = new JSONArray(sb.toString());
                for (int i = 0, len = seasonArray.length(); i < len; ++i) {
                    final JSONObject seasonObj = seasonArray.getJSONObject(i);
                    final String nameSeason = seasonObj.getString(Fields.NAME);
                    final String description = seasonObj.getString(Fields.DESCRIPTION);
                    final int idSeason = seasonObj.getInt(Fields.ID);

                    cv.put(OptiksProviderMetaData.SeasonsTable._ID, idSeason);
                    cv.put(OptiksProviderMetaData.SeasonsTable.NAME, nameSeason);
                    cv.put(OptiksProviderMetaData.SeasonsTable.DESCRIPTION, description);
                    cv.put(OptiksProviderMetaData.SeasonsTable.MAX_LEVEL_REACHED, 1);
                    db.insert(OptiksProviderMetaData.SeasonsTable.TABLE_NAME,OptiksProviderMetaData.SeasonsTable._ID, cv);
                    cv.clear();

                    final JSONArray levelsArray = new JSONArray(seasonObj.getString(Fields.LEVELS));
                    for (int j = 0, len2 = levelsArray.length(); j < len2; ++j) {
                        final JSONObject levelObj = levelsArray.getJSONObject(j);
                        final String levelData = levelObj.getString(Fields.OBJECTS);
                        final int idLevel = levelObj.getInt(Fields.ID);

                        cv.put(OptiksProviderMetaData.LevelsTable._ID, idLevel);
                        cv.put(OptiksProviderMetaData.LevelsTable.LEVEL, levelData);
                        cv.put(OptiksProviderMetaData.LevelsTable.SEASON_ID, idSeason);
                        db.insert(OptiksProviderMetaData.LevelsTable.TABLE_NAME,OptiksProviderMetaData.LevelsTable._ID, cv);
                        cv.clear();
                    }
                }
            } catch (JSONException e) {

                throw new RuntimeException(e);
            }
        }

        @Override
        public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
            Log.w(TAG, "onUpgrade data base on " + oldVersion + "to" + newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + OptiksProviderMetaData.CookieTable.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + OptiksProviderMetaData.SeasonsTable.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + OptiksProviderMetaData.LevelsTable.TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {

        openHelper = new DatabaseHelper(getContext(), OptiksProviderMetaData.DATA_BASE_NAME, null, OptiksProviderMetaData.DATA_BASE_VERSION);
        return true;
    }

    @Override
    public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder) {
        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (OptiksProviderMetaData.TypeUri.getType(uri)) {
            case COOKIE_COLLECTION_URI_ID: {
                qb.setTables(OptiksProviderMetaData.CookieTable.TABLE_NAME);
                break;
            }
            case COOKIE_SINGLE_URI_ID: {
                qb.setTables(OptiksProviderMetaData.CookieTable.TABLE_NAME);
                qb.appendWhere(OptiksProviderMetaData.CookieTable._ID + "=" + uri.getPathSegments().get(1));
                break;
            }
            case SEASONS_COLLECTION_URI_ID: {
                qb.setTables(OptiksProviderMetaData.SeasonsTable.TABLE_NAME);
                break;
            }
            case SEASONS_SINGLE_URI_ID: {
                qb.setTables(OptiksProviderMetaData.SeasonsTable.TABLE_NAME);
                qb.appendWhere(OptiksProviderMetaData.SeasonsTable._ID + "=" + uri.getPathSegments().get(1));
                break;
            }
            case LEVEL_COLLECTION_URI_ID: {
                qb.setTables(OptiksProviderMetaData.LevelsTable.TABLE_NAME);
                break;
            }
            case LEVEL_SINGLE_URI_ID: {
                qb.setTables(OptiksProviderMetaData.LevelsTable.TABLE_NAME);
                qb.appendWhere(OptiksProviderMetaData.LevelsTable._ID + "=" + uri.getPathSegments().get(1));
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown uri : " + uri);
        }

        final SQLiteDatabase db = openHelper.getReadableDatabase();
        final Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(final Uri uri) {
        return OptiksProviderMetaData.getUriType(uri);
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final long row;
        final Uri insertUrl;
        switch (OptiksProviderMetaData.TypeUri.getType(uri)) {
            case COOKIE_COLLECTION_URI_ID: {
                row = db.insert(OptiksProviderMetaData.CookieTable.TABLE_NAME, OptiksProviderMetaData.CookieTable._ID, values);
                Log.d(TAG,"row = "+row);
                if (row > 0) {
                    insertUrl = ContentUris.withAppendedId(OptiksProviderMetaData.CookieTable.CONTENT_URI, row);
                } else {
                    throw new SQLException("Failed to insert uri : " + uri);
                }
                break;
            }

            case SEASONS_COLLECTION_URI_ID: {
                row = db.insert(OptiksProviderMetaData.SeasonsTable.TABLE_NAME, OptiksProviderMetaData.SeasonsTable._ID, values);
                Log.d(TAG,"row="+row);
                if (row > 0) {
                    insertUrl = ContentUris.withAppendedId(OptiksProviderMetaData.SeasonsTable.CONTENT_URI, row);
                } else {
                    throw new SQLException("Failed to insert uri : " + uri);
                }
                break;
            }
            case LEVEL_COLLECTION_URI_ID: {
                row = db.insert(OptiksProviderMetaData.LevelsTable.TABLE_NAME, OptiksProviderMetaData.LevelsTable._ID, values);
                if (row > 0) {
                    insertUrl = ContentUris.withAppendedId(OptiksProviderMetaData.LevelsTable.CONTENT_URI, row);
                } else {
                    throw new SQLException("Failed to insert uri : " + uri + " row = "+ row);
                }
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown uri : " + uri);

        }
        getContext().getContentResolver().notifyChange(insertUrl, null);
        return insertUrl;
    }

    @Override
    public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int col;
        switch (OptiksProviderMetaData.TypeUri.getType(uri)) {
            case COOKIE_COLLECTION_URI_ID: {
                col = db.delete(OptiksProviderMetaData.CookieTable.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case COOKIE_SINGLE_URI_ID: {
                final String id = uri.getPathSegments().get(1);
                col = db.delete(OptiksProviderMetaData.CookieTable.TABLE_NAME,
                        OptiksProviderMetaData.CookieTable._ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs);
                break;
            }

            case SEASONS_COLLECTION_URI_ID: {
                col = db.delete(OptiksProviderMetaData.SeasonsTable.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case SEASONS_SINGLE_URI_ID: {
                final String id = uri.getPathSegments().get(1);
                col = db.delete(OptiksProviderMetaData.SeasonsTable.TABLE_NAME,
                        OptiksProviderMetaData.SeasonsTable._ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs);
                break;
            }
            case LEVEL_COLLECTION_URI_ID: {
                col = db.delete(OptiksProviderMetaData.LevelsTable.TABLE_NAME, selection, selectionArgs);
                break;
            }

            default:
                throw new IllegalArgumentException("Unknown uri : " + uri);
        }
        return col;
    }

    @Override
    public int update(final Uri uri, final ContentValues values, final String selection,
                      final String[] selectionArgs) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int col;
        switch (OptiksProviderMetaData.TypeUri.getType(uri)) {
            case COOKIE_COLLECTION_URI_ID: {
                col = db.update(OptiksProviderMetaData.CookieTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case COOKIE_SINGLE_URI_ID: {
                final String id = uri.getPathSegments().get(1);
                col = db.update(OptiksProviderMetaData.CookieTable.TABLE_NAME, values,
                        OptiksProviderMetaData.CookieTable._ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs);
                break;
            }

            case SEASONS_COLLECTION_URI_ID: {
                col = db.update(OptiksProviderMetaData.SeasonsTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case SEASONS_SINGLE_URI_ID: {
                final String id = uri.getPathSegments().get(1);
                col = db.update(OptiksProviderMetaData.SeasonsTable.TABLE_NAME, values,
                        OptiksProviderMetaData.SeasonsTable._ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs);
                break;
            }
            case LEVEL_COLLECTION_URI_ID: {
                col = db.update(OptiksProviderMetaData.SeasonsTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            }

            default:
                throw new IllegalArgumentException("Unknown uri : " + uri);
        }
        return col;
    }
}
