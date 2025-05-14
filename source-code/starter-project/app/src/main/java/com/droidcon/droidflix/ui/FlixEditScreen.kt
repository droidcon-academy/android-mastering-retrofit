package com.droidcon.droidflix.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.droidcon.droidflix.data.model.Flix

@Composable
fun FlixEditScreen(
    viewModel: FlixViewModel,
    onFinish: () -> Unit,
    flixToUpdate: Flix = Flix(poster = "", video = "")
) {
    var flix by remember { mutableStateOf(flixToUpdate) }
    val uiState = viewModel.uiState

    LaunchedEffect(uiState) {
        if (uiState is FlixUiState.Success) {
            viewModel.clearState()
            onFinish()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { flix = flix.copy(poster = it.toString()) }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { flix = flix.copy(video = it.toString()) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Add Flix", style = MaterialTheme.typography.headlineSmall)

        if (uiState is FlixUiState.Error) {
            Text(uiState.message, color = MaterialTheme.colorScheme.error)
        }

        OutlinedTextField(
            value = flix.title,
            onValueChange = { flix = flix.copy(title = it) },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = flix.year,
            onValueChange = { flix = flix.copy(year = it) },
            label = { Text("Year") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = flix.plot,
            onValueChange = { flix = flix.copy(plot = it) },
            label = { Text("Plot") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text(if (flix.poster != null) "Change Poster" else "Choose Poster")
        }

        Button(onClick = { videoPickerLauncher.launch("video/*") }) {
            Text(if (flix.video != null) "Change Video" else "Choose Video")
        }

        if (uiState is FlixUiState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Button(
                onClick = {
                    if (flix.id.isBlank()) {
                        viewModel.updateFlix(flixToUpdate, flix)
                    } else {
                        viewModel.createFlix(flix)
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save")
            }
        }
    }
}
