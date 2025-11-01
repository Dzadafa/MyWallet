package com.dzadafa.mywallet

import android.app.Application

class MyWalletApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        ThemeManager.applyTheme(this)
    }
}
