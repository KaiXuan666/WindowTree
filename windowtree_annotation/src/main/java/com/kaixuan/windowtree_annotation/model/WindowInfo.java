package com.kaixuan.windowtree_annotation.model;


import java.util.ArrayList;
import java.util.List;

public class WindowInfo<T> {

    private Class<?> clazz = null;
    private String clazzName = "";
    public WindowInfo parent = null;
    public List<WindowInfo> child = new ArrayList<>();
    public String name;
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
        this(clazz,clazzName,parent,0);
    }

    public WindowInfo(Class<?> clazz,String clazzName,WindowInfo parent,int index) {
        this(clazz,clazzName,parent,"",0);
    }
    public WindowInfo(Class<?> clazz,String clazzName,WindowInfo parent,String name,int index) {
        this.index = index;
        this.name = name;
        this.clazz = clazz;
        this.clazzName = clazzName;
        this.parent = parent;
    }

    public void addChild(String clazzName,String name,int index){
        try {
            child.add(new WindowInfo(Class.forName(clazzName),clazzName,this,name,index));
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

    public WindowInfo<T> findWindowInfoByClass(Class clazz){
        return findWindowInfoByClass(clazz.getName());
    }
    public WindowInfo<T> findWindowInfoByClass(String clazzName){
        if (this.clazzName.equals(clazzName)){return this;}
        for (WindowInfo windowInfo : child) {
            return windowInfo.findWindowInfoByClass(windowInfo.clazz);
        }
        return null;
    }

    @Override
    public String toString() {
        return "WindowInfo{" +
                "clazz=" + clazz +
                ", clazzName='" + clazzName + '\'' +
                ", parent=" + parent +
                ", child=" + child +
                ", index=" + index +
                ", windowType=" + windowType +
                ", t=" + t +
                '}';
    }
}
