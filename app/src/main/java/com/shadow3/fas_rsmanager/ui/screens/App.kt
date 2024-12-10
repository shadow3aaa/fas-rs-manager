package com.shadow3.fas_rsmanager.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.placeholder
import com.eygraber.compose.placeholder.material3.shimmer
import com.shadow3.fas_rsmanager.ui.components.AddAppDialog
import com.shadow3.fas_rsmanager.ui.components.AppDetailsModal
import com.shadow3.fas_rsmanager.ui.components.StatusCard
import net.peanuuutz.tomlkt.TomlArray
import net.peanuuutz.tomlkt.TomlLiteral
import net.peanuuutz.tomlkt.TomlTable
import net.peanuuutz.tomlkt.asTomlArray
import net.peanuuutz.tomlkt.asTomlLiteral
import net.peanuuutz.tomlkt.toInt

@Composable
fun App(viewModel: AppViewModel = viewModel()) {
    val context = LocalContext.current

    val inited by viewModel.inited.collectAsState()
    val showAddDialog = remember { mutableStateOf(false) }
    val addGameListButtonVisibility = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.init(context)
    }

    MainContent(
        inited = inited,
        viewModel = viewModel,
        showAddDialog = showAddDialog,
        addGameListButtonVisibility = addGameListButtonVisibility
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    inited: Boolean,
    viewModel: AppViewModel,
    showAddDialog: MutableState<Boolean>,
    addGameListButtonVisibility: MutableState<Boolean>
) {
    val context = LocalContext.current
    val refreshing by viewModel.isRefreshing.collectAsState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        Box {
            PullToRefreshBox(
                isRefreshing = refreshing,
                onRefresh = { viewModel.refreshAll(context = context) }
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .imePadding()
                ) {
                    item {
                        Spacer(modifier = Modifier.height(75.dp))
                        Text(
                            modifier = Modifier.placeholder(
                                visible = !inited,
                                highlight = PlaceholderHighlight.shimmer()
                            ), text = "fas-rs", style = MaterialTheme.typography.displaySmall
                        )
                        Spacer(modifier = Modifier.height(25.dp))

                        val version by viewModel.versionName.collectAsState()
                        val isActive by viewModel.workingStatus.collectAsState()
                        StatusCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .placeholder(
                                    visible = !inited,
                                    highlight = PlaceholderHighlight.shimmer()
                                ),
                            version = version,
                            isActive = isActive
                        )
                        Spacer(modifier = Modifier.height(50.dp))
                        ExpandableList(
                            viewModel = viewModel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .placeholder(
                                    visible = !inited,
                                    highlight = PlaceholderHighlight.shimmer()
                                ),
                            addGameListButtonVisibility = addGameListButtonVisibility,
                            showAddDialog = showAddDialog
                        )
                    }
                }
            }

            AddGameListButton(addGameListButtonVisibility, showAddDialog)
        }
    }
}

@Composable
fun BoxScope.AddGameListButton(
    addGameListButtonVisibility: MutableState<Boolean>,
    showAddDialog: MutableState<Boolean>
) {
    AnimatedVisibility(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp),
        visible = addGameListButtonVisibility.value
    ) {
        FloatingActionButton(
            onClick = { showAddDialog.value = true },
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add App")
        }
    }
}

@Composable
fun ExpandableList(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier,
    addGameListButtonVisibility: MutableState<Boolean>,
    showAddDialog: MutableState<Boolean>
) {
    val expandedIndex = remember { mutableIntStateOf(-1) }
    addGameListButtonVisibility.value = expandedIndex.intValue == 0
    val installedApps by viewModel.installedApps.collectAsState()
    val config by viewModel.config.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val gameList = installedApps.filter {
        config!!.gameList.contains(key = it.packageName) && it.packageName.contains(
            searchQuery, ignoreCase = true
        )
    }
    var showModal by remember { mutableStateOf(false) }
    var selectedApp by remember { mutableStateOf<AppInfo?>(null) }

    OutlinedCard {
        Column(modifier = modifier) {
            ExpandableListItem(title = {
                Text(
                    text = "Game List", style = MaterialTheme.typography.titleMedium
                )
            }, stickyHeader = {
                Surface {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }, content = {
                Spacer(modifier = Modifier.height(16.dp))
                gameList.forEachIndexed { index, appInfo ->
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            selectedApp = appInfo
                            showModal = true
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

                    if (index != gameList.size - 1) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }, isExpanded = expandedIndex.intValue == 0, onClick = {
                expandedIndex.intValue = if (expandedIndex.intValue == 0) -1 else 0
            })

            ExpandableListItem(title = {
                Text(
                    text = "Config", style = MaterialTheme.typography.titleMedium
                )
            }, content = {
                Text("TODO HERE...")
            }, isExpanded = expandedIndex.intValue == 1, onClick = {
                expandedIndex.intValue = if (expandedIndex.intValue == 1) -1 else 1
            })
        }
    }

    if (showModal && selectedApp != null) {
        val frameRates = remember {
            try {
                val targetFps = config!!.gameList[selectedApp!!.packageName]!!.asTomlLiteral()
                try {
                    mutableStateListOf(targetFps.toInt().toString())
                } catch (e: Exception) {
                    if (targetFps.toString() == "auto") {
                        mutableStateListOf(targetFps.toString())
                    } else {
                        throw e
                    }
                }
            } catch (e: Exception) {
                config!!.gameList[selectedApp!!.packageName]!!.asTomlArray()
                    .map { it.asTomlLiteral().toInt().toString() }.toMutableStateList()
            }
        }

        AppDetailsModal(
            frameRates = frameRates,
            selectedApp = selectedApp!!,
            onDismiss = {
                val element = if (frameRates.isNotEmpty()) {
                    TomlArray(frameRates.filter { it.isNotEmpty() }
                        .map { it.toInt() }
                        .sorted()
                        .map { TomlLiteral(it) })
                } else {
                    TomlLiteral("auto")
                }

                viewModel.setConfig { config ->
                    val newGameList = config.gameList.toMutableMap()
                    newGameList[selectedApp!!.packageName] = element
                    config.gameList = TomlTable(newGameList)
                }

                showModal = false
            },
            buttons = { _, _ ->
                Button(onClick = {
                    viewModel.setConfig { config ->
                        config.gameList = TomlTable(config.gameList.filterNot {
                            it.key == selectedApp!!.packageName
                        })
                    }
                    showModal = false
                }) {
                    Text(text = "Delete")
                }
            }
        )
    }

    if (showAddDialog.value) {
        AddAppDialog(
            onDismiss = { showAddDialog.value = false },
            onAddApp = { packageName, element ->
                viewModel.setConfig { config ->
                    val newGameList = config.gameList.toMutableMap()
                    newGameList[packageName] = element
                    config.gameList = TomlTable(newGameList)
                }
                showAddDialog.value = false
            },
            installedApps = installedApps
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpandableListItem(
    title: @Composable () -> Unit,
    content: @Composable () -> Unit,
    stickyHeader: @Composable (() -> Unit) = {},
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }
        .padding(25.dp)) {
        title()
        Spacer(modifier = Modifier.height(5.dp))
        AnimatedVisibility(
            visible = isExpanded, enter = expandVertically(), exit = shrinkVertically()
        ) {
            LazyColumn(modifier = Modifier.height(350.dp)) {
                stickyHeader {
                    stickyHeader()
                }

                item {
                    content()
                }
            }
        }
    }
}