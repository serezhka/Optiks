package com.ifmo.optiks.base.gson;

/**
 * User: dududko@gmail.com
 * Date: 30.03.12
 */

public class GsonFromServer {

    public Object id;
    public Object errors;
    public Object name;
    public Object level;

    public GsonFromServer(final Object id, final Object errors, final Object name, final Object level) {
        this.id = id;
        this.errors = errors;
        this.name = name;
        this.level = level;
    }

    public Object getId() {
        return id;
    }

    public void setId(final Object id) {
        this.id = id;
    }

    public Object getErrors() {
        return errors;
    }

    public void setErrors(final Object errors) {
        this.errors = errors;
    }

    public Object getName() {
        return name;
    }

    public void setName(final Object name) {
        this.name = name;
    }

    public Object getLevel() {
        return level;
    }

    public void setLevel(final Object level) {
        this.level = level;
    }
}