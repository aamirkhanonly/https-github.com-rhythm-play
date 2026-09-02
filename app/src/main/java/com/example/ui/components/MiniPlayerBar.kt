package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.example.player.PlaybackState
import com.example.ui.theme.PlayMusicOrange

@Composable
fun MiniPlayerBar(
    playbackState: PlaybackState,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val song = playbackState.currentSong ?: return
    val context = LocalContext.current

    val progress = if (playbackState.durationMs > 0) {
        (playbackState.currentPositionMs.toFloat() / playbackState.durationMs.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .shadow(12.dp, shape = RoundedCornerShape(16.dp), spotColor = PlayMusicOrange.copy(alpha = 0.4f))
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("mini_player_bar"),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.98f),
        tonalElevation = 6.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Linear Progress Line
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Album Art Thumbnail
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(song.displayImageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Track Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (song.sourceType == AudioSourceType.YOUTUBE_ONLINE) {
                            Text(
                                text = "ONLINE • ",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = song.artist,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Playback Controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onTogglePlayPause,
                        modifier = Modifier.testTag("mini_player_play_pause")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(22.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onNext,
                        modifier = Modifier.testTag("mini_player_next")
                    ) {
                        Icon(
                            Icons.Default.SkipNext,
                            contentDescription = "Next",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}
