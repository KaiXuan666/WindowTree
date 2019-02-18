package com.kaixuan.windowtreelibrary.template

import android.content.Context
import com.kaixuan.windowtreelibrary.WindowInfo

interface IJumpAdapter{

    fun jump(formContext: Context ,to : WindowInfo<Any>)

}