package com.kaixuan.windowtree_annotation.model;


import java.util.ArrayList;
import java.util.List;

public class WindowInfo<T> {

    private Class<?> clazz = null;
    private String clazzName = "";
    public WindowInfo parent = null;
    public List<WindowInfo> child = new ArrayList<>();
    public int index;
    private int windowType;
    private T t;

    /**
     *
     * @param clazz
     * @param clazzName
     * @param parent
     */
    public WindowInfo(Class<?> clazz,String clazzName,WindowInfo parent) {
        this.clazz = clazz;
        this.clazzName = clazzName;
        this.parent = parent;
    }

    public void addChild(String clazzName){
        try {
            child.add(new WindowInfo(Class.forName(clazzName),clazzName,this));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public WindowInfo setClazz(Class<?> clazz) {
        this.clazz = clazz;
        return this;
    }

    public String getClazzName() {
        return clazzName;
    }

    public WindowInfo setClazzName(String clazzName) {
        this.clazzName = clazzName;
        return this;
    }

    public T getT() {
        return t;
    }

    public WindowInfo<T> setT(T t) {
        this.t = t;
        return this;
    }

    public int getWindowType() {
        return windowType;
    }

    public WindowInfo<T> setWindowType(int windowType) {
        this.windowType = windowType;
        return this;
    }

    @Override
    public String toString() {
        return "WindowInfo{" +
                "clazz=" + clazz +
                ", clazzName='" + clazzName + '\'' +
                ", parent=" + parent +
                ", child=" + child +
                '}';
    }
}
