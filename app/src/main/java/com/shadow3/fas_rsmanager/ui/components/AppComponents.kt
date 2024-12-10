package com.shadow3.fas_rsmanager.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import com.shadow3.fas_rsmanager.ui.screens.AppInfo
import net.peanuuutz.tomlkt.TomlArray
import net.peanuuutz.tomlkt.TomlElement
import net.peanuuutz.tomlkt.TomlLiteral

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetailsModal(
    frameRates: SnapshotStateList<String>,
    selectedApp: AppInfo,
    onDismiss: () -> Unit,
    buttons: @Composable (SnapshotStateList<String>, AppInfo) -> Unit = { _, _ -> },
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Package Details", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(50.dp))
            Icon(
                painter = BitmapPainter(image = selectedApp.icon),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                text = selectedApp.name, style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = selectedApp.packageName, style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(25.dp))
            FrameRateInput(frameRates = frameRates)
            Spacer(modifier = Modifier.height(25.dp))
            Row {
                buttons(frameRates, selectedApp)
                Spacer(modifier = Modifier.width(10.dp))
                Button(onClick = onDismiss) {
                    Text(text = "Cancel")
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun AppSearchDialog(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filteredApps: List<AppInfo>,
    onAppSelected: (AppInfo) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Add App") }, text = {
        Column {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text(text = "Search") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(items = filteredApps) { appInfo ->
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            onAppSelected(appInfo)
                        }) {
                        Icon(
                            painter = BitmapPainter(
                                image = appInfo.icon
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = appInfo.packageName)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }, confirmButton = {}, dismissButton = {
        Button(onClick = onDismiss) {
            Text(text = "Cancel")
        }
    })
}

@Composable
fun AddAppDialog(
    onDismiss: () -> Unit, onAddApp: (String, TomlElement) -> Unit, installedApps: List<AppInfo>
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredApps = installedApps.filter {
        it.packageName.contains(searchQuery, ignoreCase = true)
    }
    var showModal by remember { mutableStateOf(false) }
    var selectedApp by remember { mutableStateOf<AppInfo?>(null) }

    if (showModal && selectedApp != null) {
        val frameRates = remember { mutableStateListOf<String>() }
        AppDetailsModal(
            frameRates = frameRates,
            selectedApp = selectedApp!!,
            onDismiss = { showModal = false },
            buttons = { _, _ ->
                Button(onClick = {
                    val element = if (frameRates.isNotEmpty()) {
                        TomlArray(frameRates.filter { it.isNotEmpty() }
                            .map { it.toInt() }
                            .sorted()
                            .map { TomlLiteral(it) })
                    } else {
                        TomlLiteral("auto")
                    }
                    onAddApp(selectedApp!!.packageName, element)
                    onDismiss()
                }) {
                    Text(text = "Add")
                }
            }
        )
    } else {
        AppSearchDialog(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            filteredApps = filteredApps,
            onAppSelected = {
                selectedApp = it
                showModal = true
            },
            onDismiss = onDismiss
        )
    }
}
