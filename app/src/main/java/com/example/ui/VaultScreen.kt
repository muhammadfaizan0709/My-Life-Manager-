package com.example.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@Composable
fun VaultScreen(viewModel: MainViewModel) {
    val vaultPin by viewModel.vaultPin.collectAsStateWithLifecycle()
    var isUnlocked by remember { mutableStateOf(vaultPin.isNullOrBlank()) }
    
    // If a PIN is set but not yet unlocked, show auth screen
    if (!vaultPin.isNullOrBlank() && !isUnlocked) {
        VaultAuthScreen(correctPin = vaultPin!!) { isUnlocked = true }
    } else {
        VaultGalleryScreen(viewModel)
    }
}

@Composable
fun VaultAuthScreen(correctPin: String, onUnlock: () -> Unit) {
    var pinInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Filled.Lock, contentDescription = "Lock", modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))
        Text("Private Vault", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = pinInput,
            onValueChange = { 
                pinInput = it
                isError = false
                if (pinInput == correctPin) onUnlock()
            },
            label = { Text("Enter PIN") },
            visualTransformation = PasswordVisualTransformation(),
            isError = isError,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (isError) {
            Text("Incorrect PIN", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { 
            if (pinInput == correctPin) onUnlock() else isError = true
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Unlock")
        }
    }
}

@Composable
fun VaultGalleryScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val mediaFiles by viewModel.privateMedia.collectAsStateWithLifecycle()
    
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        uri?.let {
            val isVideo = context.contentResolver.getType(it)?.startsWith("video") == true
            val extension = if (isVideo) ".mp4" else ".jpg"
            val fileName = UUID.randomUUID().toString() + extension
            
            // Copy file to internal private storage
            val vaultDir = context.getDir("vault", Context.MODE_PRIVATE)
            val destinationFile = File(vaultDir, fileName)
            
            context.contentResolver.openInputStream(it)?.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }
            
            viewModel.addPrivateMedia(fileName, !isVideo)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                launcher.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Media")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text("Private Vault", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(16.dp))
            
            if (mediaFiles.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Vault is empty. Add private photos/videos.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(mediaFiles, key = { it.id }) { media ->
                        val vaultDir = context.getDir("vault", Context.MODE_PRIVATE)
                        val file = File(vaultDir, media.fileName)
                        
                        Box(modifier = Modifier.aspectRatio(1f).clip(RoundedCornerShape(8.dp))) {
                            AsyncImage(
                                model = file,
                                contentDescription = "Private Media",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            if (!media.isImage) {
                                Icon(
                                    Icons.Filled.PlayCircle,
                                    contentDescription = "Video",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.align(Alignment.Center).size(32.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
