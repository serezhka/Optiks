package com.ifmo.optiks.provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */

public class OptiksProviderMetaData {

    public static final String AUTHORITY = "com.ifmo.optiks.Provider";
    public static final String DATA_BASE_NAME = "AppData";
    public static final int DATA_BASE_VERSION = 1;

    final static UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static String getUriType(final Uri uri) {
        switch (TypeUri.getType(URI_MATCHER.match(uri))) {
            case COOKIE_COLLECTION_URI_ID:
                return CookieTable.CONTENT_TYPE;
            case COOKIE_SINGLE_URI_ID:
                return CookieTable.CONTENT_ITEM_TYPE;
            case LEVEL_COLLECTION_URI_ID:
                return LevelTable.CONTENT_TYPE;
            case LEVEL_SINGLE_URI_ID:
                return LevelTable.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown uri : " + uri);
        }
    }

    static {
        URI_MATCHER.addURI(AUTHORITY, CookieTable.TABLE_NAME, TypeUri.COOKIE_COLLECTION_URI_ID.v);
        URI_MATCHER.addURI(AUTHORITY, CookieTable.TABLE_NAME + "/#", TypeUri.COOKIE_SINGLE_URI_ID.v);
        URI_MATCHER.addURI(AUTHORITY, LevelTable.TABLE_NAME, TypeUri.LEVEL_COLLECTION_URI_ID.v);
        URI_MATCHER.addURI(AUTHORITY, LevelTable.TABLE_NAME + "/#", TypeUri.LEVEL_SINGLE_URI_ID.v);
    }

    TypeUri getType(final Uri uri) {
        return TypeUri.getType(URI_MATCHER.match(uri));
    }

    public static boolean isCollectionUri(final Uri uri) {
        final TypeUri t = TypeUri.getType(uri);
        return t == TypeUri.LEVEL_COLLECTION_URI_ID || t == TypeUri.COOKIE_COLLECTION_URI_ID;
    }

    public final static class CookieTable implements BaseColumns {
        public final static String TABLE_NAME = "cookie_table";
        public final static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.optiks.cookie";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.optiks.cookie";

        //  type string
        static final String NAME = "name";
        static final String DEFAULT_NAME = "default_name";

        //type string
        static final String VALUE = "value";
        static final String DEFAULT_VALUE = "default_value";
    }

    public final static class LevelTable implements BaseColumns {
        public final static String TABLE_NAME = "level_table";
        public final static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.optiks.level";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.optiks.level";

        //  type string
        public static final String NAME = "name";
        public static final String DEFAULT_NAME = "default_name";

        //type string        in json
        public static final String VALUE = "value";
        public static final String DEFAULT_VALUE = "default_value";
    }

    public enum TypeUri {

        COOKIE_COLLECTION_URI_ID(0),
        COOKIE_SINGLE_URI_ID(1),
        LEVEL_COLLECTION_URI_ID(2),
        LEVEL_SINGLE_URI_ID(3);

        final int v;

        private TypeUri(final int t) {
            v = t;
        }

        static TypeUri getType(final Uri uri) {
            return TypeUri.getType(URI_MATCHER.match(uri));
        }

        /*static TypeUri getType(final int match) {
            switch (match) {
                case 0:
                    return COOKIE_COLLECTION_URI_ID;
                case 1:
                    return COOKIE_SINGLE_URI_ID;
                case 2:
                    return LEVEL_COLLECTION_URI_ID;
                case 3:
                    return LEVEL_SINGLE_URI_ID;
                default:
                    return null;
            }
        }*/   // TODO - not so good, look at this:

        static TypeUri getType(final int match) {
            return TypeUri.values()[match];
        }
    }
}