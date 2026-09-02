package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.model.Playlist
import com.example.model.Song
import com.example.ui.components.formatTime
import com.example.ui.theme.PlayMusicOrange
import com.example.viewmodel.LibraryTab

@Composable
fun LibraryScreen(
    songs: List<Song>,
    favoriteSongs: List<Song>,
    playlists: List<Playlist>,
    selectedTab: LibraryTab,
    searchQuery: String,
    currentPlayingSongId: String?,
    isPlaying: Boolean,
    onTabSelected: (LibraryTab) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onPlaySong: (Song, List<Song>) -> Unit,
    onShuffleAll: (List<Song>) -> Unit,
    onToggleFavorite: (Song) -> Unit,
    onChangePicture: (Song) -> Unit,
    onAddToPlaylist: (Song) -> Unit,
    onCreatePlaylist: () -> Unit,
    onDeletePlaylist: (String) -> Unit,
    onScanStorage: () -> Unit,
    onOpenEqualizer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Filter songs based on search query
    val filteredSongs = remember(songs, searchQuery) {
        if (searchQuery.isBlank()) songs
        else songs.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.artist.contains(searchQuery, ignoreCase = true) ||
            it.album.contains(searchQuery, ignoreCase = true) ||
            it.genre.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Header (2016 Google Play Music style with orange accents)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        Icons.Default.GraphicEq,
                        contentDescription = "App Icon",
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Rhythm Play",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Music Library & Offline Player",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onOpenEqualizer, modifier = Modifier.testTag("library_equalizer_button")) {
                    Icon(
                        Icons.Default.Equalizer,
                        contentDescription = "Equalizer",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = onScanStorage, modifier = Modifier.testTag("scan_storage_button")) {
                    Icon(
                        Icons.Default.Sync,
                        contentDescription = "Scan Storage",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Instant Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search songs, artists, albums, genres...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
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
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .testTag("search_text_field")
        )

        // 2016 Google Play Music Scrollable Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab.ordinal,
            edgePadding = 16.dp,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                if (selectedTab.ordinal < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                        color = MaterialTheme.colorScheme.primary,
                        height = 3.dp
                    )
                }
            },
            divider = {}
        ) {
            LibraryTab.values().forEach { tab ->
                val isSelected = selectedTab == tab
                Tab(
                    selected = isSelected,
                    onClick = { onTabSelected(tab) },
                    text = {
                        Text(
                            text = tab.title,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 15.sp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tab Content
        when (selectedTab) {
            LibraryTab.SONGS -> {
                SongsListTab(
                    songs = filteredSongs,
                    currentPlayingSongId = currentPlayingSongId,
                    isPlaying = isPlaying,
                    onPlaySong = { song -> onPlaySong(song, filteredSongs) },
                    onShuffleAll = { onShuffleAll(filteredSongs) },
                    onToggleFavorite = onToggleFavorite,
                    onChangePicture = onChangePicture,
                    onAddToPlaylist = onAddToPlaylist
                )
            }
            LibraryTab.ALBUMS -> {
                AlbumsGridTab(
                    songs = filteredSongs,
                    onPlaySong = onPlaySong,
                    onChangePicture = onChangePicture
                )
            }
            LibraryTab.ARTISTS -> {
                ArtistsListTab(
                    songs = filteredSongs,
                    onPlaySong = onPlaySong
                )
            }
            LibraryTab.PLAYLISTS -> {
                PlaylistsTab(
                    playlists = playlists,
                    allSongs = songs,
                    onCreatePlaylist = onCreatePlaylist,
                    onDeletePlaylist = onDeletePlaylist,
                    onPlaySong = onPlaySong
                )
            }
            LibraryTab.GENRES -> {
                GenresTab(
                    songs = filteredSongs,
                    onPlaySong = onPlaySong
                )
            }
            LibraryTab.FAVORITES -> {
                val favList = favoriteSongs.filter {
                    if (searchQuery.isBlank()) true
                    else it.title.contains(searchQuery, ignoreCase = true) || it.artist.contains(searchQuery, ignoreCase = true)
                }
                SongsListTab(
                    songs = favList,
                    currentPlayingSongId = currentPlayingSongId,
                    isPlaying = isPlaying,
                    onPlaySong = { song -> onPlaySong(song, favList) },
                    onShuffleAll = { onShuffleAll(favList) },
                    onToggleFavorite = onToggleFavorite,
                    onChangePicture = onChangePicture,
                    onAddToPlaylist = onAddToPlaylist,
                    emptyMessage = "No favorite songs yet! Tap the heart on any song to add."
                )
            }
        }
    }
}

@Composable
fun SongsListTab(
    songs: List<Song>,
    currentPlayingSongId: String?,
    isPlaying: Boolean,
    onPlaySong: (Song) -> Unit,
    onShuffleAll: () -> Unit,
    onToggleFavorite: (Song) -> Unit,
    onChangePicture: (Song) -> Unit,
    onAddToPlaylist: (Song) -> Unit,
    emptyMessage: String = "No songs found in library."
) {
    val context = LocalContext.current

    if (songs.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Quick Action Bar: Shuffle All & Count
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${songs.size} tracks",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onShuffleAll,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("shuffle_all_button")
                ) {
                    Icon(Icons.Default.Shuffle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Shuffle All", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(songs, key = { it.id }) { song ->
            val isCurrent = song.id == currentPlayingSongId
            SongListItem(
                song = song,
                isCurrentPlaying = isCurrent,
                isPlaying = isCurrent && isPlaying,
                onPlay = { onPlaySong(song) },
                onToggleFavorite = { onToggleFavorite(song) },
                onChangePicture = { onChangePicture(song) },
                onAddToPlaylist = { onAddToPlaylist(song) }
            )
        }
    }
}

@Composable
fun SongListItem(
    song: Song,
    isCurrentPlaying: Boolean,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onToggleFavorite: () -> Unit,
    onChangePicture: () -> Unit,
    onAddToPlaylist: () -> Unit
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentPlaying) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlay() }
            .testTag("song_item_${song.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album Art
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(song.displayImageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = song.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (isCurrentPlaying) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.45f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.GraphicEq else Icons.Default.PlayArrow,
                            contentDescription = "Playing",
                            tint = PlayMusicOrange,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title & Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isCurrentPlaying) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isCurrentPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (song.sourceType == AudioSourceType.YOUTUBE_ONLINE) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text(
                                text = "ONLINE",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = "${song.artist} • ${song.album}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Duration
            Text(
                text = formatTime(song.durationMs),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 6.dp)
            )

            // Favorite Button
            IconButton(onClick = onToggleFavorite, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (song.isFavorite) PlayMusicOrange else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Overflow Menu (Change Picture, Add to Playlist)
            Box {
                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(36.dp)) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Change Music Picture") },
                        leadingIcon = { Icon(Icons.Default.AddPhotoAlternate, contentDescription = null) },
                        onClick = {
                            showMenu = false
                            onChangePicture()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Add to Playlist") },
                        leadingIcon = { Icon(Icons.Default.PlaylistAdd, contentDescription = null) },
                        onClick = {
                            showMenu = false
                            onAddToPlaylist()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AlbumsGridTab(
    songs: List<Song>,
    onPlaySong: (Song, List<Song>) -> Unit,
    onChangePicture: (Song) -> Unit
) {
    val context = LocalContext.current
    val albums = remember(songs) {
        songs.groupBy { it.album }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(albums.entries.toList(), key = { it.key }) { (albumName, albumSongs) ->
            val firstSong = albumSongs.first()
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPlaySong(firstSong, albumSongs) }
                    .testTag("album_card_${albumName.hashCode()}")
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(firstSong.displayImageUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = albumName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Play Album",
                                tint = Color.White,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = albumName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${albumSongs.size} tracks • ${firstSong.artist}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ArtistsListTab(
    songs: List<Song>,
    onPlaySong: (Song, List<Song>) -> Unit
) {
    val context = LocalContext.current
    val artists = remember(songs) {
        songs.groupBy { it.artist }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(artists.entries.toList(), key = { it.key }) { (artistName, artistSongs) ->
            val firstSong = artistSongs.first()
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPlaySong(firstSong, artistSongs) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(firstSong.displayImageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = artistName,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = artistName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${artistSongs.size} tracks available",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { onPlaySong(firstSong, artistSongs) }) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Play Artist",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlaylistsTab(
    playlists: List<Playlist>,
    allSongs: List<Song>,
    onCreatePlaylist: () -> Unit,
    onDeletePlaylist: (String) -> Unit,
    onPlaySong: (Song, List<Song>) -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Create Playlist Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCreatePlaylist() }
                    .testTag("create_playlist_button")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.padding(10.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Create New Playlist",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Personalized custom playlists & mixes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        items(playlists, key = { it.id }) { pl ->
            val playlistSongs = allSongs.filter { pl.songIds.contains(it.id) }
            val firstSong = playlistSongs.firstOrNull() ?: allSongs.firstOrNull()

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (playlistSongs.isNotEmpty()) {
                            onPlaySong(playlistSongs.first(), playlistSongs)
                        }
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(pl.coverUri ?: firstSong?.displayImageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = pl.name,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = pl.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${pl.songIds.size} songs • ${pl.description}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }

                    IconButton(onClick = { onDeletePlaylist(pl.id) }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Playlist",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GenresTab(
    songs: List<Song>,
    onPlaySong: (Song, List<Song>) -> Unit
) {
    val genres = remember(songs) {
        songs.groupBy { it.genre }
    }

    val genreColors = mapOf(
        "Pop" to Color(0xFFFF6F00),
        "Rock" to Color(0xFFD32F2F),
        "Classic" to Color(0xFF1976D2),
        "Jazz" to Color(0xFF7B1FA2),
        "Online" to Color(0xFF00796B),
        "Offline" to Color(0xFF388E3C)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(genres.entries.toList(), key = { it.key }) { (genreName, genreSongs) ->
            val color = genreColors[genreName] ?: MaterialTheme.colorScheme.primary
            val firstSong = genreSongs.first()

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.25f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clickable { onPlaySong(firstSong, genreSongs) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column {
                        Text(
                            text = genreName.uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = color
                        )
                        Text(
                            text = "${genreSongs.size} tracks",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
