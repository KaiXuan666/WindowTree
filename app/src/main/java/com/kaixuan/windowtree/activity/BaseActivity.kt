package com.kaixuan.windowtree.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

open class BaseActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(this.javaClass.simpleName,"onCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.i(this.javaClass.simpleName,"onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.i(this.javaClass.simpleName,"onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.i(this.javaClass.simpleName,"onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.i(this.javaClass.simpleName,"onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(this.javaClass.simpleName,"onDestroy")
    }
}