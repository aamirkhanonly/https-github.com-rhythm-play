package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.AudioSourceType
import com.example.model.Song
import com.example.ui.theme.PlayMusicOrange

@Composable
fun YouTubeOnlineScreen(
    onlineTracks: List<Song>,
    searchQuery: String,
    isSearching: Boolean,
    onSearchChange: (String) -> Unit,
    onPlayOnlineTrack: (Song, List<Song>) -> Unit,
    onAddTrackToLibrary: (Song) -> Unit,
    currentPlayingSongId: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var customStreamUrl by remember { mutableStateOf("") }
    var customTitle by remember { mutableStateOf("") }
    var showCustomStreamInput by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Online Header Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF261208),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFF0000),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "YouTube Music Online",
                                tint = Color.White,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "YouTube Music Online",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Continuous Stream • Offscreen Playback",
                                style = MaterialTheme.typography.bodySmall,
                                color = PlayMusicOrange
                            )
                        }
                    }

                    IconButton(onClick = { showCustomStreamInput = !showCustomStreamInput }) {
                        Icon(
                            Icons.Default.Language,
                            contentDescription = "Custom Stream",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Online Search input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search YouTube Music streams, lofi, radio...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    trailingIcon = {
                        if (isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 2.dp
                            )
                        } else if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("online_search_field")
                )
            }
        }

        // Custom Stream URL collapsible input
        if (showCustomStreamInput) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Play Direct Audio URL / Web Radio",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = customTitle,
                        onValueChange = { customTitle = it },
                        label = { Text("Stream Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = customStreamUrl,
                        onValueChange = { customStreamUrl = it },
                        label = { Text("Stream URL (MP3/AAC/HTTPS)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (customStreamUrl.isNotBlank()) {
                                val customSong = Song(
                                    id = "stream_${System.currentTimeMillis()}",
                                    title = customTitle.ifBlank { "Custom Web Stream" },
                                    artist = "Online Audio",
                                    album = "Web Stream",
                                    durationMs = 300000,
                                    albumArtUri = "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_hero_banner",
                                    streamOrFilePath = customStreamUrl,
                                    sourceType = AudioSourceType.YOUTUBE_ONLINE,
                                    lyricsLrc = "[00:00.00] Streaming live from $customStreamUrl"
                                )
                                onPlayOnlineTrack(customSong, listOf(customSong))
                                showCustomStreamInput = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Stream Now")
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Featured Stations Horizontal Row
            item {
                Text(
                    text = "Featured Live Music Stations",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                val featuredStations = listOf(
                    Triple("24/7 Lofi Study", "Peaceful beats & vinyl crackles", "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_album_synth"),
                    Triple("Synthwave Radio", "Cyberpunk highway night drive", "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_hero_banner"),
                    Triple("Acoustic Sanctuary", "Warm acoustic guitar relaxation", "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_album_acoustic"),
                    Triple("Royal Orchestra Live", "Grand symphonic concert hall", "android.resource://com.aistudio.rhythmplay.kmpz/drawable/img_album_orchestral")
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(featuredStations) { (title, subtitle, coverUri) ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .width(180.dp)
                                .clickable {
                                    val matched = onlineTracks.firstOrNull { it.title.contains(title.take(6), ignoreCase = true) }
                                        ?: onlineTracks.first()
                                    onPlayOnlineTrack(matched, onlineTracks)
                                }
                        ) {
                            Column {
                                Box(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context).data(coverUri).build(),
                                        contentDescription = title,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(bottomStart = 8.dp),
                                        color = Color.Red,
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    ) {
                                        Text(
                                            text = "LIVE",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Online Streams List
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Trending YouTube Audio Streams",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(onlineTracks, key = { it.id }) { track ->
                val isCurrent = track.id == currentPlayingSongId
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPlayOnlineTrack(track, onlineTracks) }
                        .testTag("online_track_${track.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Artwork
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(track.displayImageUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = track.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Info
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = track.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${track.artist} • YouTube Online",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Add to Library Button
                        IconButton(
                            onClick = { onAddTrackToLibrary(track) },
                            modifier = Modifier.testTag("add_online_to_library_${track.id}")
                        ) {
                            Icon(
                                Icons.Default.BookmarkAdd,
                                contentDescription = "Add to Library",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Play stream button
                        IconButton(
                            onClick = { onPlayOnlineTrack(track, onlineTracks) }
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = "Play Stream",
                                    tint = Color.White,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
