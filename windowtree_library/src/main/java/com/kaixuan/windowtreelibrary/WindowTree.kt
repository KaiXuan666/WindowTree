package com.kaixuan.windowtreelibrary

import android.app.Application
import android.content.Context
import com.kaixuan.windowtreelibrary.util.ClassUtils
import com.kaixuan.windowtreelibrary.util.Consts
import com.kaixuan.windowtreelibrary.util.DefaultLogger

class WindowTree{


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
//                hasInit = true
                mContext = application.applicationContext
                logger.showLog(true)
                val fileNameByPackageName = ClassUtils.getFileNameByPackageName(mContext, Consts.GENERATE_FILE_PATH)
                logger.error(Consts.TAG,fileNameByPackageName.toString())
//            fileNameByPackageName.find { TextUtils.getRight(it,".") }
            }
        }
    }



}