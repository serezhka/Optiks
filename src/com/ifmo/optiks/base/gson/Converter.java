package com.ifmo.optiks.base.gson;

import com.google.gson.Gson;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */

public class Converter {

    final private static Converter converter = new Converter();
    final private Gson gson = new Gson();

    public static Converter getInstance() {
        return converter;
    }

    public String toGson(final Object object) {
        return gson.toJson(object);
    }

    public <T> T fromGson(final String source, Class<T> c) {
        return gson.fromJson(source, c);
    }
}
