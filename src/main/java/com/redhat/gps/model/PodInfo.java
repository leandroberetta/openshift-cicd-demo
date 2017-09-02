package com.redhat.gps.model;

/**
 * Created by lberetta on 7/15/17.
 */
public class PodInfo {

    private String name;
    private String namespace;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
