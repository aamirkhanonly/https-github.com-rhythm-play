package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.OnlinePrediction
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.model.Song
import com.example.ui.components.AddToPlaylistDialog
import com.example.ui.components.ChangePictureDialog
import com.example.ui.components.CreatePlaylistDialog
import com.example.ui.components.EqualizerSheet
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.NowPlayingSheet
import com.example.ui.theme.PlayMusicOrange
import com.example.viewmodel.MusicViewModel

enum class RootNavTab(val title: String) {
    LIBRARY("Library"),
    YOUTUBE_ONLINE("YouTube Music")
}

@Composable
fun MainScreen(viewModel: MusicViewModel) {
    val context = LocalContext.current
    var activeNavTab by remember { mutableStateOf(RootNavTab.LIBRARY) }

    // State collection
    val allSongs by viewModel.allSongs.collectAsState()
    val favoriteSongs by viewModel.favoriteSongs.collectAsState()
    val allPlaylists by viewModel.allPlaylists.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()
    val equalizerSettings by viewModel.equalizerSettings.collectAsState()

    val selectedLibraryTab by viewModel.selectedTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isNowPlayingExpanded by viewModel.isNowPlayingExpanded.collectAsState()
    val showLyrics by viewModel.showLyrics.collectAsState()
    val showEqualizerSheet by viewModel.showEqualizerSheet.collectAsState()
    val changePictureSong by viewModel.showChangePictureSong.collectAsState()
    val addToPlaylistSong by viewModel.showAddToPlaylistSong.collectAsState()
    val showCreatePlaylistDialog by viewModel.showCreatePlaylistDialog.collectAsState()
    val showScanDialog by viewModel.showScanDialog.collectAsState()
    val scanCount by viewModel.scanResultCount.collectAsState()

    val onlineQuery by viewModel.onlineSearchQuery.collectAsState()
    val onlineTracks by viewModel.onlineStreamingTracks.collectAsState()
    val isSearchingOnline by viewModel.isSearchingOnline.collectAsState()

    // Permission launcher for local storage audio scanning
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.scanDeviceAudio()
        } else {
            Toast.makeText(context, "Storage access is needed to scan your downloaded audio files.", Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Mini Player Bar
                    if (playbackState.currentSong != null && !isNowPlayingExpanded) {
                        MiniPlayerBar(
                            playbackState = playbackState,
                            onTogglePlayPause = { viewModel.togglePlayPause() },
                            onNext = { viewModel.next() },
                            onClick = { viewModel.isNowPlayingExpanded.value = true }
                        )
                    }

                    // Bottom Navigation Bar (Material 3 style)
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp,
                        modifier = Modifier.navigationBarsPadding().testTag("bottom_nav_bar")
                    ) {
                        NavigationBarItem(
                            selected = activeNavTab == RootNavTab.LIBRARY,
                            onClick = { activeNavTab = RootNavTab.LIBRARY },
                            icon = {
                                Icon(
                                    Icons.Default.LibraryMusic,
                                    contentDescription = "Library"
                                )
                            },
                            label = { Text("Play Music", fontWeight = if (activeNavTab == RootNavTab.LIBRARY) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PlayMusicOrange,
                                selectedTextColor = PlayMusicOrange,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_library_tab")
                        )

                        NavigationBarItem(
                            selected = activeNavTab == RootNavTab.YOUTUBE_ONLINE,
                            onClick = { activeNavTab = RootNavTab.YOUTUBE_ONLINE },
                            icon = {
                                Icon(
                                    Icons.Default.Radio,
                                    contentDescription = "YouTube Music Online"
                                )
                            },
                            label = { Text("YouTube Online", fontWeight = if (activeNavTab == RootNavTab.YOUTUBE_ONLINE) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PlayMusicOrange,
                                selectedTextColor = PlayMusicOrange,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_youtube_online_tab")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (activeNavTab) {
                    RootNavTab.LIBRARY -> {
                        LibraryScreen(
                            songs = allSongs,
                            favoriteSongs = favoriteSongs,
                            playlists = allPlaylists,
                            selectedTab = selectedLibraryTab,
                            searchQuery = searchQuery,
                            currentPlayingSongId = playbackState.currentSong?.id,
                            isPlaying = playbackState.isPlaying,
                            onTabSelected = { viewModel.selectedTab.value = it },
                            onSearchQueryChange = { viewModel.searchQuery.value = it },
                            onPlaySong = { song, queue -> viewModel.playSong(song, queue) },
                            onShuffleAll = { queue ->
                                viewModel.playerEngine.toggleShuffle()
                                if (queue.isNotEmpty()) viewModel.playSong(queue.random(), queue)
                            },
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            onChangePicture = { viewModel.showChangePictureSong.value = it },
                            onAddToPlaylist = { viewModel.showAddToPlaylistSong.value = it },
                            onCreatePlaylist = { viewModel.showCreatePlaylistDialog.value = true },
                            onDeletePlaylist = { viewModel.deletePlaylist(it) },
                            onScanStorage = {
                                val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    Manifest.permission.READ_MEDIA_AUDIO
                                } else {
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                }
                                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                                    viewModel.scanDeviceAudio()
                                } else {
                                    permissionLauncher.launch(permission)
                                }
                            },
                            onOpenEqualizer = { viewModel.showEqualizerSheet.value = true }
                        )
                    }
                    RootNavTab.YOUTUBE_ONLINE -> {
                        YouTubeOnlineScreen(
                            onlineTracks = onlineTracks,
                            searchQuery = onlineQuery,
                            isSearching = isSearchingOnline,
                            onSearchChange = { viewModel.searchOnlineYouTube(it) },
                            onPlayOnlineTrack = { track, queue -> viewModel.playSong(track, queue) },
                            onAddTrackToLibrary = {
                                viewModel.addOnlineTrackToLibrary(it)
                                Toast.makeText(context, "Added \"${it.title}\" to Library!", Toast.LENGTH_SHORT).show()
                            },
                            currentPlayingSongId = playbackState.currentSong?.id
                        )
                    }
                }
            }
        }

        // Full-screen Now Playing Overlay
        AnimatedVisibility(
            visible = isNowPlayingExpanded && playbackState.currentSong != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            NowPlayingSheet(
                playbackState = playbackState,
                showLyrics = showLyrics,
                onToggleLyrics = { viewModel.showLyrics.value = !viewModel.showLyrics.value },
                onTogglePlayPause = { viewModel.togglePlayPause() },
                onSeekTo = { viewModel.seekTo(it) },
                onNext = { viewModel.next() },
                onPrevious = { viewModel.previous() },
                onToggleShuffle = { viewModel.toggleShuffle() },
                onCycleRepeat = { viewModel.cycleRepeatMode() },
                onToggleFavorite = { viewModel.toggleFavorite(it) },
                onChangePicture = { viewModel.showChangePictureSong.value = it },
                onOpenEqualizer = { viewModel.showEqualizerSheet.value = true },
                onAddToPlaylist = { viewModel.showAddToPlaylistSong.value = it },
                onSaveLyrics = { id, lyrics -> viewModel.updateLyrics(id, lyrics) },
                onPlayQueueItem = { viewModel.playSong(it) },
                onDismiss = { viewModel.isNowPlayingExpanded.value = false }
            )
        }

        // Equalizer Modal Sheet
        if (showEqualizerSheet) {
            EqualizerSheet(
                settings = equalizerSettings,
                onEnabledChange = { viewModel.setEqualizerEnabled(it) },
                onPresetSelect = { viewModel.setEqualizerPreset(it) },
                onBandLevelChange = { index, level -> viewModel.setEqualizerBandLevel(index, level) },
                onBassBoostChange = { viewModel.setBassBoost(it) },
                onVirtualizerChange = { viewModel.setVirtualizer(it) },
                onReverbChange = { viewModel.setReverbPreset(it) },
                onDismiss = { viewModel.showEqualizerSheet.value = false }
            )
        }

        // Change Picture Dialog
        changePictureSong?.let { song ->
            ChangePictureDialog(
                song = song,
                onDismiss = { viewModel.showChangePictureSong.value = null },
                onSavePicture = { songId, newUri ->
                    viewModel.changeSongPicture(songId, newUri)
                    Toast.makeText(context, "Artwork updated for ${song.title}", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Add to Playlist Dialog
        addToPlaylistSong?.let { song ->
            AddToPlaylistDialog(
                song = song,
                playlists = allPlaylists,
                onDismiss = { viewModel.showAddToPlaylistSong.value = null },
                onAddToPlaylist = { playlistId, songId ->
                    viewModel.addSongToPlaylist(playlistId, songId)
                    Toast.makeText(context, "Added to playlist!", Toast.LENGTH_SHORT).show()
                },
                onCreateNewPlaylistClick = {
                    viewModel.showCreatePlaylistDialog.value = true
                }
            )
        }

        // Create Playlist Dialog
        if (showCreatePlaylistDialog) {
            CreatePlaylistDialog(
                onDismiss = { viewModel.showCreatePlaylistDialog.value = false },
                onCreate = { name, desc ->
                    viewModel.createPlaylist(name, desc, null, emptyList())
                    Toast.makeText(context, "Playlist created!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Scan Results Dialog
        if (showScanDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.showScanDialog.value = false },
                title = { Text("Storage Scan Complete") },
                text = {
                    Text("Found and added ${scanCount ?: 0} local audio tracks to your Rhythm Play library.")
                },
                confirmButton = {
                    Button(onClick = { viewModel.showScanDialog.value = false }) {
                        Text("Awesome")
                    }
                }
            )
        }
    }
}
