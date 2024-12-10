package com.shadow3.fas_rsmanager

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log

class AIDLConnection : ServiceConnection {
    var ipc: IRootService? = null

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.d("root service", "connected")
        ipc = IRootService.Stub.asInterface(service)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        Log.d("root service", "disconnected")
    }
}
