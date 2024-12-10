package com.shadow3.fas_rsmanager

import android.annotation.SuppressLint
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.topjohnwu.superuser.ipc.RootService
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

class AIDLService : RootService() {
    override fun onBind(intent: Intent): IBinder {
        return getRootService()
    }
}

private fun getRootService(): IBinder {
    return object : IRootService.Stub() {
        override fun getVersionName() = getPowercfg().version

        override fun getVersionCode() = getPowercfg().versionCode

        override fun getWorkingStatus(): Boolean {
            val process = Runtime.getRuntime().exec("pidof fas-rs")
            val reader = process.inputStream.bufferedReader()
            val output = reader.readText()
            return output.trim().isNotEmpty()
        }

        @SuppressLint("SdCardPath")
        override fun getConfig(): String {
            val file = File("/sdcard/Android/fas-rs/games.toml")
            return file.readText()
        }

        @SuppressLint("SdCardPath")
        override fun setConfig(config: String) {
            val file = File("/sdcard/Android/fas-rs/games.toml")
            file.writeText(text = config)
        }
    }
}

private fun getPowercfg(): Powercfg {
    val file = File("/data/powercfg.json")
    return Json.decodeFromString(Powercfg.serializer(), file.readText())
}

@Serializable
private data class Powercfg(
    val name: String,
    val author: String,
    val version: String,
    val versionCode: Int,
    val features: PowercfgFeatures,
    val module: String,
    val state: String,
    val entry: String,
    val projectUrl: String
)

@Serializable
private data class PowercfgFeatures(
    val strict: Boolean,
    val pedestal: Boolean,
)
