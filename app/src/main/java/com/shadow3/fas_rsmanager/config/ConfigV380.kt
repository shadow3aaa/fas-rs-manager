package com.shadow3.fas_rsmanager.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.peanuuutz.tomlkt.TomlLiteral
import net.peanuuutz.tomlkt.TomlTable

@Serializable
data class ConfigV380(
    val config: ConfigConfigV380,
    @SerialName("game_list")
    var gameList: TomlTable,
    val powersave: ConfigModeV380,
    val balance: ConfigModeV380,
    val performance: ConfigModeV380,
    val fast: ConfigModeV380,
)

@Serializable
data class ConfigConfigV380(
    @SerialName("keep_std")
    val keepStd: Boolean,
    @SerialName("scene_game_list")
    val sceneGameList: Boolean,
)

@Serializable
data class ConfigModeV380(
    val margin: Int,
    @SerialName("core_temp_thresh")
    val coreTempThresh: TomlLiteral,
)
