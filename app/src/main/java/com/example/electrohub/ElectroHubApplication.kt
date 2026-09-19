package com.example.electrohub

import android.app.Application
import com.cloudinary.android.MediaManager
import com.example.electrohub.base.CloudinaryConfig

class ElectroHubApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        MediaManager.init(
            this,
            mapOf(
                "cloud_name" to CloudinaryConfig.CLOUD_NAME
            )
        )
    }
}