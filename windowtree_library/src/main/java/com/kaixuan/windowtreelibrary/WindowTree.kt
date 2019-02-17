package com.kaixuan.windowtreelibrary

import android.app.Application
import android.content.Context
import com.kaixuan.windowtree_annotation.annotation.Window
import com.kaixuan.windowtree_annotation.model.WindowInfo
import com.kaixuan.windowtreelibrary.template.IMain
import com.kaixuan.windowtreelibrary.template.IWindowTreeLoad
import com.kaixuan.windowtreelibrary.util.ClassUtils
import com.kaixuan.windowtreelibrary.util.Consts
import com.kaixuan.windowtreelibrary.util.DefaultLogger
import com.kaixuan.windowtreelibrary.util.TextUtils

class WindowTree{

    lateinit var allGeneratedFile :
            Map<String, Class<out IWindowTreeLoad>>;

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
                val windowInfo = WindowInfo<Any>(null,null,null);
                rootWindow!!.getConstructor().newInstance().loadWindowTree(windowInfo)
                bindWindowTree(windowInfo)
                logger.error(Consts.TAG,instance.allGeneratedFile.toString())
                logger.error(Consts.TAG,rootWindow.toString())
//            fileNameByPackageName.find { TextUtils.getRight(it,".") }
            }
        }

        fun bindWindowTree(windowInfo: WindowInfo<Any>){
            windowInfo.child.forEach { forItem ->
                val clazz = instance.allGeneratedFile[Consts.GENERATE_FILE_PATH + "." + TextUtils.getRight(
                    forItem.clazzName,
                    "."
                ) + Consts.GENERATE_FILE_NAME_END]
                clazz?.let {
                    it.getConstructor().newInstance().loadWindowTree(forItem)
                }
                bindWindowTree(forItem);
            }
        }

        fun destroy(){
            hasInit = false
        }
    }



}