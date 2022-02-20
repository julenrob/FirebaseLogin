package com.julenrob.firebaselogin

import android.app.Application
import com.google.android.gms.ads.MobileAds

class FirebaseLoginapp: Application() {

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
    }
}