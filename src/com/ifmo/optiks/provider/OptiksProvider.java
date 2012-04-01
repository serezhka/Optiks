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

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */

public class OptiksProvider extends ContentProvider {

    private final static String TAG = "OptiksProviderTAG";

    //todo projection Map??
    private DatabaseHelper openHelper;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        /**
         * Create a helper object to create, open, and/or manage a database.
         * This method always returns very quickly.  The database is not actually
         * created or opened until one of {@link #getWritableDatabase} or
         * {@link #getReadableDatabase} is called.
         *
         * @param context to use to open or create the database
         * @param name    of the database file, or null for an in-memory database
         * @param factory to use for creating cursor objects, or null for the default
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

            db.execSQL("CREATE TABLE " + OptiksProviderMetaData.LevelTable.TABLE_NAME + " (" +
                    OptiksProviderMetaData.LevelTable._ID + " INTEGER PRIMARY KEY , "                       //AUTOINCREMENT
                    + OptiksProviderMetaData.LevelTable.NAME + " TEXT, " +
                    OptiksProviderMetaData.LevelTable.VALUE + " TEXT);");

        }

        @Override
        public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
            //todo
            Log.w(TAG, "onUpgrade data base on " + oldVersion + "to" + newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + OptiksProviderMetaData.CookieTable.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + OptiksProviderMetaData.LevelTable.TABLE_NAME);
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
            case LEVEL_COLLECTION_URI_ID: {
                qb.setTables(OptiksProviderMetaData.LevelTable.TABLE_NAME);
                break;
            }
            case LEVEL_SINGLE_URI_ID: {
                qb.setTables(OptiksProviderMetaData.LevelTable.TABLE_NAME);
                qb.appendWhere(OptiksProviderMetaData.LevelTable._ID + "=" + uri.getPathSegments().get(1));
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown uri : " + uri);
        }

        // final String orderBy = TextUtils.isEmpty(sortOrder)?
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
        if (!OptiksProviderMetaData.isCollectionUri(uri)) {
            throw new IllegalArgumentException("Unknown uri : " + uri);
        }
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final long row;
        final Uri insertUrl;
        switch (OptiksProviderMetaData.TypeUri.getType(uri)) {
            case COOKIE_COLLECTION_URI_ID: {
                row = db.insert(OptiksProviderMetaData.CookieTable.TABLE_NAME, OptiksProviderMetaData.CookieTable._ID, values);
                if (row > 0) {
                    insertUrl = ContentUris.withAppendedId(OptiksProviderMetaData.CookieTable.CONTENT_URI, row);
                } else {
                    throw new SQLException("Failed to insert uri : " + uri);
                }
                break;
            }

            case LEVEL_COLLECTION_URI_ID: {
                row = db.insert(OptiksProviderMetaData.LevelTable.TABLE_NAME, OptiksProviderMetaData.LevelTable._ID, values);
                if (row > 0) {
                    insertUrl = ContentUris.withAppendedId(OptiksProviderMetaData.LevelTable.CONTENT_URI, row);
                } else {
                    throw new SQLException("Failed to insert uri : " + uri);
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

            case LEVEL_COLLECTION_URI_ID: {
                col = db.delete(OptiksProviderMetaData.LevelTable.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case LEVEL_SINGLE_URI_ID: {
                final String id = uri.getPathSegments().get(1);
                col = db.delete(OptiksProviderMetaData.LevelTable.TABLE_NAME,
                        OptiksProviderMetaData.LevelTable._ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs);
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

            case LEVEL_COLLECTION_URI_ID: {
                col = db.update(OptiksProviderMetaData.LevelTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case LEVEL_SINGLE_URI_ID: {
                final String id = uri.getPathSegments().get(1);
                col = db.update(OptiksProviderMetaData.LevelTable.TABLE_NAME, values,
                        OptiksProviderMetaData.LevelTable._ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs);
                break;
            }

            default:
                throw new IllegalArgumentException("Unknown uri : " + uri);
        }
        return col;
    }
}
