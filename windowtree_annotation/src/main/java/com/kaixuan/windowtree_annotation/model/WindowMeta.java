package com.kaixuan.windowtree_annotation.model;

import java.util.ArrayList;
import java.util.List;

public class WindowMeta<T> {

    private Class<?> clazz = null;
    private String clazzName = "";
    public WindowMeta parent = null;
    public List<WindowMeta> child = new ArrayList<>();
    public int index;
    private int windowType;
    private T t;

    public WindowMeta(Class<?> clazz, String clazzName, WindowMeta parent) {
        this.clazz = clazz;
        this.clazzName = clazzName;
        this.parent = parent;
    }

    public void addChild(String clazzName){
        try {
            child.add(new WindowMeta(Class.forName(clazzName),clazzName,this));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public WindowMeta setClazz(Class<?> clazz) {
        this.clazz = clazz;
        return this;
    }

    public String getClazzName() {
        return clazzName;
    }

    public WindowMeta setClazzName(String clazzName) {
        this.clazzName = clazzName;
        return this;
    }

    public T getT() {
        return t;
    }

    public WindowMeta<T> setT(T t) {
        this.t = t;
        return this;
    }

    public int getWindowType() {
        return windowType;
    }

    public WindowMeta<T> setWindowType(int windowType) {
        this.windowType = windowType;
        return this;
    }

    @Override
    public String toString() {
        return "WindowMeta{" +
                "clazz=" + clazz +
                ", clazzName='" + clazzName + '\'' +
                ", parent=" + parent +
                ", child=" + child +
                '}';
    }
}
