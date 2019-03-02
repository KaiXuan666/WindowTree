package com.kaixuan.windowtreelibrary


import android.content.Context
import android.os.Bundle
import com.kaixuan.windowtree_annotation.enums.WindowType
import com.kaixuan.windowtreelibrary.template.IJumpAdapter
import com.kaixuan.windowtreelibrary.util.WindowTreeUtil
import java.lang.RuntimeException

import java.util.ArrayList


class WindowInfo<T> @JvmOverloads constructor (

    clazz: Class<*>,
    clazzName: String,
    parent: WindowInfo<*>?,
    var name: String? = null,
    var index: Int = 0,
    var windowType: WindowType = WindowType.UNKNOWN
) {

    companion object {
         val TAG = "WindowInfo"
    }
    private var clazz: Class<*>? = null
    private var clazzName = ""
    var parent: WindowInfo<*>? = null
    val child: MutableList<WindowInfo<*>> = ArrayList()
    val bundle: Bundle by lazy { Bundle() }
    private var tag : T? = null
    /**
     * 如当前节点需跳转到Fragment或填充View，则需设置该属性
     */
    var frameLayoutId = -1
    var mJumpAdapter: IJumpAdapter? = null
    /**
     * 当前节点需要接收其他窗口发来的消息时，请设置该监听
     */
    private var onEventListener : ((
        sender:WindowInfo<*>,
        sendData:Any?
    ) -> Any? )? = null

    var unReadMsgCount : Int = 0

    var pageAuthority : Long = -1

    init {
        this.clazz = clazz
        this.clazzName = clazzName
        this.parent = parent
    }

    fun addChild(clazzName: String, name: String, index: Int, windowType: WindowType,pageAuthority : Long) {
        try {
            child.add(WindowInfo<Any>(Class.forName(clazzName), clazzName, this, name, index, windowType).apply { this.pageAuthority = pageAuthority })
        } catch (e: ClassNotFoundException) {
            WindowTree.logger.error("addChild",e.toString())
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

    fun getTag(): T? {
        return tag
    }

    fun setTag(tag: T): WindowInfo<T> {
        this.tag = tag
        return this
    }

    fun sendData(data : Any,receiverClass : Class<*>) : Any?{
        val findWindowInfoByClass = WindowTree.instance.windowMeta!!.findWindowInfoByClass(receiverClass) ?: WindowTree.logger.error(TAG,"receiverClass，找不到$receiverClass").run { return null }
        findWindowInfoByClass.onEventListener ?: WindowTree.logger.error(TAG,"发送失败，目标${receiverClass}未设置监听").run { return null }
        return findWindowInfoByClass.onEventListener!!.invoke(this, data)
    }

    fun sendData(data : Any,receiverClazzName: String) : Any?{
        val findWindowInfoByClass = WindowTree.instance.windowMeta!!.findWindowInfoByClass(receiverClazzName) ?: WindowTree.logger.error(TAG,"发送失败，找不到$receiverClazzName").run { return null }
        findWindowInfoByClass.onEventListener ?: WindowTree.logger.error(TAG,"发送失败，目标${receiverClazzName}未设置监听").run { return null }
        return findWindowInfoByClass.onEventListener!!.invoke(this, data)
    }

    fun sendData(data : Any,receiver: WindowInfo<*>) : Any?{
        receiver.onEventListener ?: WindowTree.logger.error(TAG,"发送失败，目标${receiver}未设置监听").run { return null }
        return receiver.onEventListener!!.invoke(this, data)
    }

    fun setEventListener(a : (( sender:WindowInfo<*>, sendData:Any?) -> Any? )?){
        onEventListener = a
    }

    fun getEventListener() = onEventListener

    /**
     * 跳转至第几个什么类型的子界面
     * @param index 第几个子界面，该索引值受windowType影响
     * @param windowType 什么类型的子界面，如不传入windowType，默认以所有类型的子界面获取index
     */
    fun jump(index : Int,windowType: WindowType = WindowType.UNKNOWN):Boolean{
        val tempAdapter = mJumpAdapter ?: WindowTree.instance.defaultJumpAdapter
        val taget = findChildByIndex<Any>(index, windowType) ?: throw RuntimeException("找不到${windowType}类型的第${index}个窗口")
        val context = WindowTreeUtil.findContextByInfo(this)
            ?: throw RuntimeException("当前${this}对应的window未处于打开状态，无法从当前节点获取context，无法跳转")
        return tempAdapter.jump(context, taget)
    }

    fun jump(windowInfo: WindowInfo<*>):Boolean{
        val tempAdapter = mJumpAdapter ?: WindowTree.instance.defaultJumpAdapter
        val context = WindowTreeUtil.findContextByInfo(this)
            ?: throw RuntimeException("当前${this}对应的window未处于打开状态，无法从当前节点获取context，无法跳转")
        return tempAdapter.jump(context, windowInfo)
    }

    fun <T> findChildByIndex(index : Int,windowType: WindowType = WindowType.UNKNOWN): WindowInfo<T>? {
        val filter = filterChildByWindowType(windowType)
        return if (index >= filter.size) null else filter[index] as WindowInfo<T>
    }

    fun filterChildByWindowType(windowType: WindowType): List<WindowInfo<*>> {
        return child.filter { windowType == WindowType.UNKNOWN || it.windowType == windowType }
    }

    fun findWindowInfoByClass(clazz: Class<*>?): WindowInfo<*>? {
        return findWindowInfoByClass(clazz!!.name)
    }

    fun getContext(): Context? = WindowTreeUtil.findContextByInfo(this)

    tailrec fun findWindowInfoByClass(clazzName: String): WindowInfo<*>? {
        WindowTree.logger.info("findWindowInfoByClass",this.toString())
        if (this.clazzName == clazzName) {
            return this
        }
        for (windowMeta in child) {
            val findWindowInfoByClass = windowMeta.findWindowInfoByClass(clazzName)
            if (findWindowInfoByClass == null){

            }else{
                return findWindowInfoByClass
            }
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
                ", tag=" + tag +
                '}'.toString()
    }
}

