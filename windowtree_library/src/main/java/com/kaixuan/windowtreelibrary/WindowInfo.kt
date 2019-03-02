package com.kaixuan.windowtreelibrary


import android.content.Context
import android.os.Bundle
import com.kaixuan.windowtree_annotation.enums.WindowType
import com.kaixuan.windowtreelibrary.model.UnReadCountEvent
import com.kaixuan.windowtreelibrary.template.IJumpAdapter
import com.kaixuan.windowtreelibrary.util.WindowTreeUtil
import java.lang.RuntimeException

import java.util.ArrayList
import kotlin.properties.Delegates
import kotlin.reflect.KProperty


class WindowInfo<T> @JvmOverloads constructor (

    clazz: Class<*>,
    clazzName: String,
    parent: WindowInfo<*>?,
    var name: String? = null,
    var index: Int = 0,
    var windowType: WindowType = WindowType.UNKNOWN
) {

    companion object {
         const val TAG = "WindowInfo"
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

    /**
     * 该值发生任意变化时，都会通知到当前窗口以及当前窗口的所有父节点
     */
    var unReadMsgCount : Int by Delegates.observable(0) {
            prop, old, new ->
        var notifyTaget : WindowInfo<*>?
        notifyTaget = this
        while (notifyTaget != null){
            this.sendData(UnReadCountEvent(this,new - old),notifyTaget)
            notifyTaget = notifyTaget.parent
        }
    }

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
        WindowTree.logger.info("sendData","this = ${this.clazzName}, receiver = ${receiver.clazzName}, data = $data")
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

    /**
     * 计算当前的所有子节点的未读消息数量（包含当前节点）
     */
    fun calcChildUnReadCount() : Int{
        var count = 0
        findWindowInfoByCondition {
            count += it.unReadMsgCount
            return@findWindowInfoByCondition false
        }
        return count
    }

    fun getContext(): Context? = WindowTreeUtil.findContextByInfo(this)

    fun findWindowInfoByClass(clazzName: String): WindowInfo<*>? {
        return findWindowInfoByCondition{
            return@findWindowInfoByCondition it.clazzName == clazzName
        }
    }
    tailrec fun findWindowInfoByCondition(condition : (WindowInfo<*>) -> Boolean): WindowInfo<*>? {
        WindowTree.logger.info("findWindowInfoByCondition",this.toString())
        if (condition(this)) {
            return this
        }
        for (windowMeta in child) {
            val findWindowInfoByClass = windowMeta.findWindowInfoByCondition(condition)
            if (findWindowInfoByClass == null){

            }else{
                return findWindowInfoByClass
            }
        }
        return null
    }
    tailrec fun findWindowInfoByConditionUp(condition : (WindowInfo<*>) -> Boolean): WindowInfo<*>? {
        WindowTree.logger.info("findWindowInfoByCondition",this.toString())
        if (condition(this)) {
            return this
        }

        if (parent == null){
            return null
        }else{
            return parent!!.findWindowInfoByConditionUp(condition)
        }
    }

    /**
     * 如果当前节点有设置过onEventListener则需要手动调用该方法释放资源
     *
     *  @param clearReadCount 是否清理未读消息数量，可根据业务需求传参
     */
    fun release(clearReadCount : Boolean = false){
        if (clearReadCount) unReadMsgCount = 0
        setEventListener(null)
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

