package com.kaixuan.windowtreelibrary.model

import com.kaixuan.windowtreelibrary.WindowInfo

data class UnReadCountEvent(val fromWindowInfo: WindowInfo<*>,val change : Int)