package com.kaixuan.windowtreelibrary

import android.app.Application
import android.content.Context
import com.kaixuan.windowtree_annotation.annotation.Window
import com.kaixuan.windowtree_annotation.model.WindowMeta
import com.kaixuan.windowtreelibrary.template.IMain
import com.kaixuan.windowtreelibrary.template.IWindowTreeLoad
import com.kaixuan.windowtreelibrary.util.Consts
import com.kaixuan.windowtreelibrary.util.DefaultLogger

class WindowTree{

    lateinit var allGeneratedFile :
            Map<String, Class<out IWindowTreeLoad>>;

    var windowMeta : WindowInfo<Any>? = null;

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
                logger.error(Consts.TAG,instance.allGeneratedFile.toString())
                logger.error(Consts.TAG,rootWindow.toString())
//            fileNameByPackageName.find { TextUtils.getRight(it,".") }
            }
        }

        tailrec fun bindWindowTree(windowMeta: WindowInfo<*>){
            windowMeta.child.forEach { forItem ->
                val clazz = instance.allGeneratedFile[Consts.GENERATE_FILE_PATH + "." + forItem.getClazz()!!.simpleName + Consts.GENERATE_FILE_NAME_END]
                if (clazz == null){
                    // 找不到，停止

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

    fun <T : Any> with(obj: T): WindowInfo<out Any?>? {
        return windowMeta!!.findWindowInfoByClass(obj.javaClass)
    }


}