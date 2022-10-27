package com.example.wmess.ui.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.example.wmess.R

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun FilePreview() {
    OutlinedTextField(value = "", onValueChange = {}, placeholder = {
        FileUploadPreview(filename = "LOL.pngdkwodkawokdwokdwokdwokdowd", 1f) {}
    })
}

@Composable
fun FileUploadPreview(filename: String, progress: Float, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .background(Color.LightGray, RoundedCornerShape(16.dp))
            .height(40.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painterResource(id = R.drawable.ic_baseline_file_present_24), null)
        Text(
            text = filename,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Close, null)
        }

        val animation by animateFloatAsState(targetValue = progress)

        if (animation == 1f)
            Icon(Icons.Default.Done, null)
        else
            CircularProgressIndicator(animation, modifier = Modifier.size(24.dp))
    }
}