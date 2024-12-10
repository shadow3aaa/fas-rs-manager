package com.shadow3.fas_rsmanager.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FrameRateInput(frameRates: SnapshotStateList<String>) {
    FlowRow(verticalArrangement = Arrangement.Center) {
        frameRates.forEachIndexed { index, rate ->
            var text by remember { mutableStateOf(rate) }

            InputChip(
                modifier = Modifier.height(30.dp),
                selected = false,
                onClick = { /* No action needed */ },
                label = {
                    BasicTextField(
                        value = text,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        onValueChange = {
                            if (it.all { char -> char.isDigit() }) {
                                text = it
                                frameRates[index] = it
                            }
                        },
                        modifier = Modifier
                            .widthIn(min = 30.dp, max = 35.dp)
                    )
                }, trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete Frame Rate",
                        modifier = Modifier
                            .size(16.dp)
                            .clickable {
                                frameRates.removeAt(index = index)
                                text = frameRates[index]
                            }
                    )
                })

            Spacer(modifier = Modifier.width(10.dp))
        }

        Icon(
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.CenterVertically)
                .clickable {
                    if (frameRates.isEmpty() || frameRates.last() != "") {
                        frameRates.add("")
                    }
                },
            imageVector = Icons.Default.Add,
            contentDescription = null,
        )
    }
}

@Composable
@Preview
fun FrameRateChipAuto(onDelete: (() -> Unit)? = { }) {
    AssistChip(
        label = {
            Text(text = "auto")
        },
        trailingIcon = {
            if (onDelete != null) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onDelete() }
                )
            }
        },
        onClick = {},
        colors = AssistChipDefaults.assistChipColors()
            .copy(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier
            .height(30.dp)
    )
}
