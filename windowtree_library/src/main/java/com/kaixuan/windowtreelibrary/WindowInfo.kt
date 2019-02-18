package com.kaixuan.windowtreelibrary


import android.os.Bundle
import com.kaixuan.windowtree_annotation.enums.WindowType

import java.util.ArrayList


class WindowInfo<T> @JvmOverloads constructor (
    clazz: Class<*>,
    clazzName: String,
    parent: WindowInfo<*>?,
    var name: String? = null,
    var index: Int = 0,
    var windowType: WindowType = WindowType.UNKNOWN
) {

    private var clazz: Class<*>? = null
    private var clazzName = ""
    var parent: WindowInfo<*>? = null
    val bundle: Bundle by lazy { Bundle() }
    val child: MutableList<WindowInfo<*>> = ArrayList()
    private var t: T? = null

    init {
        this.clazz = clazz
        this.clazzName = clazzName
        this.parent = parent
    }

    fun addChild(clazzName: String, name: String, index: Int, windowType: WindowType) {
        try {
            child.add(WindowInfo<Any>(Class.forName(clazzName), clazzName, this, name, index, windowType))
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }

    }

    fun getClazz(): Class<*>? {
        return clazz
    }

    fun setClazz(clazz: Class<*>): WindowInfo<*> {
        this.clazz = clazz
        return this
    }

    fun getClazzName(): String {
        return clazzName
    }

    fun setClazzName(clazzName: String): WindowInfo<*> {
        this.clazzName = clazzName
        return this
    }

    fun getT(): T? {
        return t
    }

    fun setT(t: T): WindowInfo<T> {
        this.t = t
        return this
    }

    fun findWindowInfoByClass(clazz: Class<*>?): WindowInfo<out Any?>? {
        return findWindowInfoByClass(clazz!!.name)
    }

    tailrec fun findWindowInfoByClass(clazzName: String): WindowInfo<out Any?>? {
        WindowTree.logger.info("findWindowInfoByClass",this.toString())
        if (this.clazzName == clazzName) {
            return this
        }
        for (windowMeta in child) {
            return windowMeta.findWindowInfoByClass(windowMeta.clazz)
        }
        return null
    }

    /**
     * 切忌不要打印this或父或子节点，否则将造成
     */
    override fun toString(): String {
        var parentClazzName  = "null str"
        parent?.run { parentClazzName = this.clazzName }
        return "WindowMeta{" +
                "clazz=" + clazz +
                ", clazzName='" + clazzName + '\''.toString() +
                ", parent=" + parentClazzName +
                ", child.size=" + child.size +
                ", index=" + index +
                ", windowType=" + windowType +
                ", t=" + t +
                '}'.toString()
    }
}

