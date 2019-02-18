package com.kaixuan.windowtree_annotation.enums;

public enum  WindowType {

    ACTIVITY(0, "android.app.Activity"),
    FRAGMENT(1, "android.app.Fragment"),
    FRAGMENTV4(2, "android.support.v4.app.Fragment"),
    VIEW(3, "android.view.View"),
    DIALOG(4, "android.app.Dialog"),
    POPUPWINDOW(5, "android.widget.PopupWindow"),
    UNKNOWN(-1, "Unknown route type");

    int id;
    String className;

    WindowType(int id, String className) {
        this.id = id;
        this.className = className;
    }

    public int getId() {
        return id;
    }

    public WindowType setId(int id) {
        this.id = id;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public WindowType setClassName(String className) {
        this.className = className;
        return this;
    }

    public static WindowType parse(String name) {
        for (WindowType windowType : WindowType.values()) {
            if (windowType.getClassName().equals(name)) {
                return windowType;
            }
        }
        return UNKNOWN;
    }
}
