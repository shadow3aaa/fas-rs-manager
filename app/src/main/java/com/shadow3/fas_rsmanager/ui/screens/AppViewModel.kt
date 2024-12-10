package com.shadow3.fas_rsmanager.ui.screens

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadow3.fas_rsmanager.AIDLConnection
import com.shadow3.fas_rsmanager.AIDLService
import com.shadow3.fas_rsmanager.IRootService
import com.shadow3.fas_rsmanager.config.ConfigV380
import com.topjohnwu.superuser.ipc.RootService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.peanuuutz.tomlkt.Toml

class AppViewModel : ViewModel() {
    private lateinit var rootConnection: AIDLConnection

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _inited = MutableStateFlow(false)
    val inited: StateFlow<Boolean> = _inited

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps

    private val _versionName = MutableStateFlow("Unknown")
    val versionName: StateFlow<String> = _versionName

    private val _workingStatus = MutableStateFlow(false)
    val workingStatus: StateFlow<Boolean> = _workingStatus

    private val _config = MutableStateFlow<ConfigV380?>(null)
    val config: StateFlow<ConfigV380?> = _config

    fun init(context: Context) {
        viewModelScope.launch {
            // try connect to root service
            rootConnection = AIDLConnection()
            RootService.bind(Intent(context, AIDLService::class.java), rootConnection)

            // wait until connected to root service
            while (rootConnection.ipc == null) delay(1000)

            refreshAll(context = context)
            _inited.value = true
        }
    }

    fun refreshAll(context: Context) {
        _isRefreshing.value = true
        viewModelScope.launch(context = Dispatchers.IO) {
            sudo {
                updateInstalledApps(context.packageManager)
                updateVersionName()
                updateWorkingStatus()
                updateConfig()
            }
            delay(500)
            _isRefreshing.value = false
        }
    }

    fun updateInstalledApps(packageManager: PackageManager) {
        _installedApps.value =
            packageManager.getInstalledApplications(0).filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }.map { appInfo ->
                AppInfo(
                    icon = appInfo.loadIcon(packageManager).toBitmap().asImageBitmap(),
                    packageName = appInfo.packageName,
                    name = appInfo.loadLabel(packageManager).toString()
                )
            }
    }

    fun updateVersionName() {
        viewModelScope.launch {
            sudo {
                _versionName.value = "$versionName($versionCode)"
            }
        }
    }

    fun updateWorkingStatus() {
        viewModelScope.launch {
            sudo {
                _workingStatus.value = getWorkingStatus()
            }
        }
    }

    fun updateConfig() {
        viewModelScope.launch {
            sudo {
                _config.value = Toml.decodeFromString(
                    deserializer = ConfigV380.serializer(),
                    string = getConfig()
                )
            }
        }
    }

    fun setConfig(action: (ConfigV380) -> Unit) {
        viewModelScope.launch {
            action(_config.value!!)
            sudo {
                setConfig(Toml.encodeToString(ConfigV380.serializer(), _config.value!!))
            }
        }
    }

    private fun sudo(action: IRootService.() -> Unit) {
        action(rootConnection.ipc!!)
    }
}

data class AppInfo(
    val icon: ImageBitmap,
    val packageName: String,
    val name: String,
)