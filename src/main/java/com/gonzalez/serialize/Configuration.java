package com.gonzalez.serialize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Configuration implements Serializable {

    private String name;
    private List<Target> targetList;

    public Configuration() {
        targetList = new ArrayList<>();
    }

    public Configuration(String name) {
        this.name = name;
        targetList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Target> getTargetList() {
        return targetList;
    }

    public void setTargetList(List<Target> targetList) {
        this.targetList = targetList;
    }

    public void addTarget(Target target) {
        this.targetList.add(target);
    }
}
