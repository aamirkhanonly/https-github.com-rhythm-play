package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.model.Song

@Composable
fun ChangePictureDialog(
    song: Song,
    onDismiss: () -> Unit,
    onSavePicture: (songId: String, newImageUri: String) -> Unit
) {
    val context = LocalContext.current
    var selectedUri by remember { mutableStateOf(song.displayImageUri ?: "") }
    var urlInput by remember { mutableStateOf("") }
    var showUrlField by remember { mutableStateOf(false) }

    // Android Photo Picker launcher (zero permission, Google Play compliant)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            selectedUri = it.toString()
        }
    }

    val presetArtworks = listOf(
        "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_album_synth" to "Synthwave Sunset",
        "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_album_acoustic" to "Warm Acoustic",
        "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_album_orchestral" to "Symphony Hall",
        "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_hero_banner" to "Equalizer Waves",
        "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_app_icon" to "Rhythm Play Badge"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("change_picture_dialog"),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Change Music Picture",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Custom album cover for \"${song.title}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Current / Preview Image
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedUri.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(selectedUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Selected Cover Preview",
                            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons: Pick from Gallery / Enter URL
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.weight(1f).testTag("pick_from_photos_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Gallery Photo", style = MaterialTheme.typography.labelMedium)
                    }

                    OutlinedButton(
                        onClick = { showUrlField = !showUrlField },
                        modifier = Modifier.weight(1f).testTag("image_url_button")
                    ) {
                        Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Image URL", style = MaterialTheme.typography.labelMedium)
                    }
                }

                if (showUrlField) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = {
                            urlInput = it
                            if (it.startsWith("http")) selectedUri = it
                        },
                        label = { Text("Paste Image URL (https://...)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Or choose from featured presets:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Preset covers grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier.fillMaxWidth().height(70.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(presetArtworks) { (uri, title) ->
                        val isSelected = selectedUri == uri
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedUri = uri }
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context).data(uri).build(),
                                contentDescription = title,
                                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                                contentScale = ContentScale.Crop
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .background(Color.Black.copy(alpha = 0.4f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSavePicture(song.id, selectedUri)
                    onDismiss()
                },
                modifier = Modifier.testTag("save_picture_confirm_button")
            ) {
                Text("Apply Artwork")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
