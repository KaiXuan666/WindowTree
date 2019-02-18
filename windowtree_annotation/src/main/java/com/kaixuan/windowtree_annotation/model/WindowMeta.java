package com.kaixuan.windowtree_annotation.model;


import com.kaixuan.windowtree_annotation.enums.WindowType;

import java.util.ArrayList;
import java.util.List;


public class WindowMeta<T> {

    private Class<?> clazz = null;
    private String clazzName = "";
    public WindowMeta parent = null;
    public List<WindowMeta> child = new ArrayList<>();
    public String name;
    public int index;
    private WindowType windowType;
    private T t;

    public WindowMeta(Class<?> clazz, String clazzName, WindowMeta parent, String name, int index) {
        this(clazz,clazzName,parent,name,index,WindowType.UNKNOWN);
    }
    public WindowMeta(Class<?> clazz, String clazzName, WindowMeta parent, String name, int index, WindowType windowType) {
        this.index = index;
        this.name = name;
        this.clazz = clazz;
        this.clazzName = clazzName;
        this.parent = parent;
        this.windowType = windowType;
    }

    public void addChild(String clazzName,String name,int index,WindowType windowType){
        try {
            child.add(new WindowMeta(Class.forName(clazzName),clazzName,this,name,index,windowType));
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

    public WindowType getWindowType() {
        return windowType;
    }

    public WindowMeta<T> setWindowType(WindowType windowType) {
        this.windowType = windowType;
        return this;
    }

    public WindowMeta<T> findWindowInfoByClass(Class clazz){
        return findWindowInfoByClass(clazz.getName());
    }
    public WindowMeta<T> findWindowInfoByClass(String clazzName){
        if (this.clazzName.equals(clazzName)){return this;}
        for (WindowMeta windowMeta : child) {
            return windowMeta.findWindowInfoByClass(windowMeta.clazz);
        }
        return null;
    }

    @Override
    public String toString() {
        return "WindowMeta{" +
                "clazz=" + clazz +
                ", clazzName='" + clazzName + '\'' +
                ", index=" + index +
                ", windowType=" + windowType +
                ", t=" + t +
                '}';
    }
}
