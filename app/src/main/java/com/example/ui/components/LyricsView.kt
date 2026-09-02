package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Song
import com.example.player.LyricsParser

@Composable
fun LyricsView(
    song: Song,
    currentPositionMs: Long,
    onSeekTo: (Long) -> Unit,
    onSaveLyrics: (songId: String, newLyrics: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val lyricsLines = remember(song.lyricsLrc) {
        LyricsParser.parseLrc(song.lyricsLrc)
    }

    val activeIndex = remember(lyricsLines, currentPositionMs) {
        LyricsParser.getActiveLineIndex(lyricsLines, currentPositionMs)
    }

    val listState = rememberLazyListState()
    var isEditing by remember { mutableStateOf(false) }
    var editedLyricsText by remember(song.lyricsLrc) { mutableStateOf(song.lyricsLrc) }

    // Auto-scroll to active lyric line smoothly
    LaunchedEffect(activeIndex) {
        if (activeIndex >= 0 && activeIndex < lyricsLines.size && !isEditing) {
            val targetScroll = (activeIndex - 2).coerceAtLeast(0)
            listState.animateScrollToItem(targetScroll)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                        MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                    )
                )
            )
            .padding(16.dp)
            .testTag("lyrics_container")
    ) {
        if (isEditing) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Lyrics (LRC Format)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row {
                        IconButton(onClick = { isEditing = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel")
                        }
                        IconButton(onClick = {
                            onSaveLyrics(song.id, editedLyricsText)
                            isEditing = false
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Save", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                Text(
                    text = "Format: [mm:ss.xx] Lyric line here",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = editedLyricsText,
                    onValueChange = { editedLyricsText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        onSaveLyrics(song.id, editedLyricsText)
                        isEditing = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save & Synchronize")
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header with Sync Indicator & Edit button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.FormatQuote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Live Synchronized Lyrics",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = {
                            editedLyricsText = song.lyricsLrc
                            isEditing = true
                        },
                        modifier = Modifier.testTag("edit_lyrics_button")
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Lyrics",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (lyricsLines.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No lyrics found for this song",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { isEditing = true }) {
                                Text("Add Synchronized Lyrics")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(vertical = 40.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        itemsIndexed(lyricsLines) { index, line ->
                            val isActive = index == activeIndex
                            val isPast = index < activeIndex
                            val scale by animateFloatAsState(targetValue = if (isActive) 1.08f else 1.0f)
                            val alpha by animateFloatAsState(
                                targetValue = when {
                                    isActive -> 1.0f
                                    isPast -> 0.5f
                                    else -> 0.35f
                                }
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .scale(scale)
                                    .alpha(alpha)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        else Color.Transparent
                                    )
                                    .clickable { onSeekTo(line.timestampMs) }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = line.text,
                                    style = if (isActive) {
                                        MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    } else {
                                        MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
