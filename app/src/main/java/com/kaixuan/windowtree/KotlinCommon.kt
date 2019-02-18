package com.kaixuan.windowtree

import android.widget.Toast


fun showToast(msg : String){
    Toast.makeText(MyApp.instance,msg,Toast.LENGTH_SHORT).show()
}
