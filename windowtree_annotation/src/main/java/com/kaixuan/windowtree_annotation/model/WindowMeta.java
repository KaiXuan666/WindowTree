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
    public long pageAuthority;

    public WindowMeta(Class<?> clazz, String clazzName, WindowMeta parent, String name, int index) {
        this(clazz,clazzName,parent,name,index,WindowType.UNKNOWN);
    }
    public WindowMeta(Class<?> clazz, String clazzName, WindowMeta parent, String name, int index, WindowType windowType) {
        this(clazz,clazzName,parent,name,index,WindowType.UNKNOWN,-1);
    }
    public WindowMeta(Class<?> clazz, String clazzName, WindowMeta parent, String name, int index, WindowType windowType,long pageAuthority) {
        this.index = index;
        this.name = name;
        this.clazz = clazz;
        this.clazzName = clazzName;
        this.parent = parent;
        this.windowType = windowType;
        this.pageAuthority = pageAuthority;
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

    public WindowType getWindowType() {
        return windowType;
    }

    public WindowMeta<T> setWindowType(WindowType windowType) {
        this.windowType = windowType;
        return this;
    }

    @Override
    public String toString() {
        return "WindowMeta{" +
                "clazz=" + clazz +
                ", clazzName='" + clazzName + '\'' +
                ", name='" + name + '\'' +
                ", index=" + index +
                ", windowType=" + windowType +
                ", pageAuthority=" + pageAuthority +
                '}';
    }
}
