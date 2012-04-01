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

    public GsonFromServer(Object id, Object errors, Object name, Object level) {
        this.id = id;
        this.errors = errors;
        this.name = name;
        this.level = level;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Object getErrors() {
        return errors;
    }

    public void setErrors(Object errors) {
        this.errors = errors;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public Object getLevel() {
        return level;
    }

    public void setLevel(Object level) {
        this.level = level;
    }
}