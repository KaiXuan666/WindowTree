package com.kaixuan.windowtreelibrary

import android.app.Application
import android.content.Context
import com.kaixuan.windowtree_annotation.annotation.Window
import com.kaixuan.windowtreelibrary.adapter.DefaultJumpAdapter
import com.kaixuan.windowtreelibrary.template.IJumpAdapter
import com.kaixuan.windowtreelibrary.template.IMain
import com.kaixuan.windowtreelibrary.template.IWindowTreeLoad
import com.kaixuan.windowtreelibrary.util.Consts
import com.kaixuan.windowtreelibrary.util.DefaultLogger
import java.lang.RuntimeException
import java.util.*

class WindowTree{

    lateinit var allGeneratedFile :
            Map<String, Class<out IWindowTreeLoad>>;

    val defaultJumpAdapter : IJumpAdapter by lazy { DefaultJumpAdapter() }

    var windowMeta : WindowInfo<Any>? = null;

    val weakHashMap : WeakHashMap<Any,WindowInfo<*>> = WeakHashMap()

    companion object {
        lateinit var mContext : Context
        @JvmField
        @Volatile var debuggable = false
        @Volatile var hasInit = false
        @JvmField
        val logger = DefaultLogger(Consts.TAG)
        val instance : WindowTree by lazy (LazyThreadSafetyMode.SYNCHRONIZED){ WindowTree() }

        fun init(application: Application){
            if (!hasInit){
                hasInit = true
                mContext = application.applicationContext
                logger.showLog(true)
                val newInstance =
                    Class.forName( Consts.GENERATE_FILE_PATH+".Main"+Consts.GENERATE_FILE_NAME_END).getConstructor()
                        .newInstance()
                instance.allGeneratedFile = (newInstance as IMain).allGeneratedFile
                val rootWindow = instance.allGeneratedFile[Consts.GENERATE_FILE_PATH+".Window"+Consts.GENERATE_FILE_NAME_END]
                instance.windowMeta = WindowInfo<Any>(
                    Window::class.java,
                    Window::class.java.name,
                    null
                );
                rootWindow!!.getConstructor().newInstance().loadWindowTree(instance.windowMeta)
                bindWindowTree(instance.windowMeta!!)
                logger.info(Consts.TAG,instance.allGeneratedFile.toString())
            }
        }

        fun bindWindowTree(windowMeta: WindowInfo<*>){
            windowMeta.child.forEach { forItem ->
                val clazz = instance.allGeneratedFile[Consts.GENERATE_FILE_PATH + "." + forItem.getClazz()!!.simpleName + Consts.GENERATE_FILE_NAME_END]
                if (clazz == null){
                    // 找不到，跳过

                }else{
                    // 找到，继续递归
                    clazz.getConstructor().newInstance().loadWindowTree(forItem)
                    bindWindowTree(forItem)
                }
            }
        }

        fun destroy(){
            hasInit = false
            instance.windowMeta = null
        }
    }

    fun <T> with(obj: Any): WindowInfo<T>? {
        return weakHashMap[obj] as WindowInfo<T>? ?: windowMeta!!.findWindowInfoByClass(obj.javaClass)?.apply { weakHashMap[obj] = this } as WindowInfo<T>?
    }
}

fun Any.myWindowInfo() : WindowInfo<Any>{
    val with = WindowTree.instance.with<Any>(this)
    return with ?: throw RuntimeException("找不到与${this}对应的WindowInfo，请检查。在匿名内部类调用本方法时应谨慎检查this关键字的指代对象。")
}