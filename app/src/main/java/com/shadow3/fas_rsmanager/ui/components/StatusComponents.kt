package com.shadow3.fas_rsmanager.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StatusCard(modifier: Modifier = Modifier, version: String, isActive: Boolean) {
    val statusText = if (isActive) "Working" else "Not working"
    val icon = if (isActive) Icons.Filled.CheckCircle else Icons.Default.Close
    val cardColor =
        if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = modifier
    ) {
        Row(modifier = Modifier.padding(25.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(modifier = Modifier.size(30.dp), imageVector = icon, contentDescription = null)

            Spacer(modifier = Modifier.width(25.dp))

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
                Text(
                    text = statusText, style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = version, style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
