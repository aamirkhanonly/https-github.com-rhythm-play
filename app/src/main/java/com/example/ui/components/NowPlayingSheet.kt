package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.example.player.PlaybackState
import com.example.player.RepeatMode
import com.example.ui.theme.PlayMusicOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingSheet(
    playbackState: PlaybackState,
    showLyrics: Boolean,
    onToggleLyrics: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onToggleFavorite: (Song) -> Unit,
    onChangePicture: (Song) -> Unit,
    onOpenEqualizer: () -> Unit,
    onAddToPlaylist: (Song) -> Unit,
    onSaveLyrics: (songId: String, newLyrics: String) -> Unit,
    onPlayQueueItem: (Song) -> Unit,
    onDismiss: () -> Unit
) {
    val song = playbackState.currentSong ?: return
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var showQueueDrawer by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.background,
                        Color(0xFF140F0B)
                    )
                )
            )
            .statusBarsPadding()
            .testTag("now_playing_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("dismiss_now_playing")) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse Player",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PLAYING FROM ${song.album.uppercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (song.sourceType == AudioSourceType.YOUTUBE_ONLINE) "YouTube Stream • Online" else "Offline High Fidelity",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More Options",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Change Cover Picture") },
                            leadingIcon = { Icon(Icons.Default.AddPhotoAlternate, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onChangePicture(song)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Audio Equalizer & FX") },
                            leadingIcon = { Icon(Icons.Default.Equalizer, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onOpenEqualizer()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Add to Playlist") },
                            leadingIcon = { Icon(Icons.Default.PlaylistAdd, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onAddToPlaylist(song)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Visual View: Album Art OR Synchronized Lyrics View
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = showLyrics,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "LyricsArtTransition"
                ) { isLyricsVisible ->
                    if (isLyricsVisible) {
                        LyricsView(
                            song = song,
                            currentPositionMs = playbackState.currentPositionMs,
                            onSeekTo = onSeekTo,
                            onSaveLyrics = onSaveLyrics,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Large 2016 Style Album Art Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .aspectRatio(1f)
                                .shadow(
                                    elevation = 16.dp,
                                    shape = RoundedCornerShape(28.dp),
                                    spotColor = PlayMusicOrange.copy(alpha = 0.5f)
                                )
                                .clip(RoundedCornerShape(28.dp))
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(28.dp)
                                )
                                .clickable { onToggleLyrics() }
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(song.displayImageUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Album Cover Art",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title, Artist, Favorite & Extra Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${song.artist} • ${song.album}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Favorite Heart Button
                    IconButton(
                        onClick = { onToggleFavorite(song) },
                        modifier = Modifier.testTag("toggle_favorite_button")
                    ) {
                        Icon(
                            imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (song.isFavorite) PlayMusicOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Change picture button
                    IconButton(
                        onClick = { onChangePicture(song) },
                        modifier = Modifier.testTag("quick_change_picture_button")
                    ) {
                        Icon(
                            Icons.Default.AddPhotoAlternate,
                            contentDescription = "Change Art",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Slider & Timestamps
            val progress = if (playbackState.durationMs > 0) {
                (playbackState.currentPositionMs.toFloat() / playbackState.durationMs.toFloat()).coerceIn(0f, 1f)
            } else 0f

            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = progress,
                    onValueChange = { newPercent ->
                        val targetMs = (newPercent * playbackState.durationMs).toLong()
                        onSeekTo(targetMs)
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("playback_progress_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(playbackState.currentPositionMs),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatTime(playbackState.durationMs),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Media Controls Row (Shuffle, Prev, Play/Pause, Next, Repeat)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle Button
                IconButton(onClick = onToggleShuffle, modifier = Modifier.testTag("shuffle_button")) {
                    Icon(
                        Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (playbackState.isShuffle) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Previous Button
                IconButton(onClick = onPrevious, modifier = Modifier.testTag("prev_button")) {
                    Icon(
                        Icons.Default.SkipPrevious,
                        contentDescription = "Previous Track",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Large Primary Play/Pause Button
                FloatingActionButton(
                    onClick = onTogglePlayPause,
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp),
                    modifier = Modifier
                        .size(68.dp)
                        .testTag("now_playing_play_pause_fab")
                ) {
                    Icon(
                        imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Next Button
                IconButton(onClick = onNext, modifier = Modifier.testTag("next_button")) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "Next Track",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Repeat Mode Button
                IconButton(onClick = onCycleRepeat, modifier = Modifier.testTag("repeat_button")) {
                    val (icon, tint) = when (playbackState.repeatMode) {
                        RepeatMode.ONE -> Icons.Default.RepeatOne to MaterialTheme.colorScheme.primary
                        RepeatMode.ALL -> Icons.Default.Repeat to MaterialTheme.colorScheme.primary
                        RepeatMode.OFF -> Icons.Default.Repeat to MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = "Repeat Mode",
                        tint = tint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Quick Bar: Lyrics Toggle, Equalizer, and Queue
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Lyrics Toggle
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (showLyrics) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.clickable { onToggleLyrics() }.testTag("toggle_lyrics_bar_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.FormatQuote,
                            contentDescription = null,
                            tint = if (showLyrics) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Lyrics",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (showLyrics) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Equalizer Button
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.clickable { onOpenEqualizer() }.testTag("open_equalizer_bar_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Equalizer,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Equalizer",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Queue Button
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.clickable { showQueueDrawer = true }.testTag("open_queue_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.QueueMusic,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Queue (${playbackState.queue.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Queue Bottom Sheet
        if (showQueueDrawer) {
            ModalBottomSheet(
                onDismissRequest = { showQueueDrawer = false },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "Now Playing Queue",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                        itemsIndexed(playbackState.queue) { index, queueSong ->
                            val isCurrent = index == playbackState.queueIndex
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isCurrent) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                        else Color.Transparent
                                    )
                                    .clickable {
                                        onPlayQueueItem(queueSong)
                                        showQueueDrawer = false
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(queueSong.displayImageUri)
                                        .build(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = queueSong.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = queueSong.artist,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isCurrent) {
                                    Icon(
                                        Icons.Default.Equalizer,
                                        contentDescription = "Playing",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatTime(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
