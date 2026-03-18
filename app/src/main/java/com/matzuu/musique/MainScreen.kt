package com.matzuu.musique

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.matzuu.musique.models.Song
import com.matzuu.musique.ui.components.MusiqueBottomBar
import com.matzuu.musique.ui.components.TabTopBar
import com.matzuu.musique.ui.screens.AlbumGridScreen
import com.matzuu.musique.ui.screens.CurrentPlaylistScreen
import com.matzuu.musique.ui.screens.HistoryScreen
import com.matzuu.musique.ui.screens.SongListScreen
import com.matzuu.musique.ui.screens.SongSubListScreen
import com.matzuu.musique.viewmodels.MusiqueViewModel
import com.matzuu.musique.workers.SongSyncWorker
import kotlinx.coroutines.launch

private const val TAG = "MainScreen"

@OptIn(ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
) {
    val musiqueViewModel: MusiqueViewModel = viewModel(factory = MusiqueViewModel.Factory)
    LaunchedEffect(musiqueViewModel) {
        SongSyncWorker.viewmodel = musiqueViewModel
        //musiqueViewModel.scheduleWorker()
        //musiqueViewModel.updateValues()
    }

    val mediaController = musiqueViewModel.mediaController
    val sheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )
    var sheetSize by remember { mutableStateOf(100.dp) }
    val animatedSheetSize by animateDpAsState(
        targetValue = sheetSize
    )

    /*
    mediaController.setOnCompletionListener { player ->
        val songs = musiqueViewModel.currentPlaylist
        val idx = musiqueViewModel.currentPlaylistIdx
        player.pause()
        if (idx >= songs.size - 1) {
            musiqueViewModel.isPlaying = false
            return@setOnCompletionListener
        }
        val nextSong = songs[idx+1]
        player.reset()
        player.setDataSource(nextSong.path)
        player.prepare() //TODO Async?
        player.start()
        musiqueViewModel.isPlaying = true
        musiqueViewModel.setCurrentSong(nextSong)
        musiqueViewModel.currentPlaylistIdx += 1
    }
    */
    DisposableEffect(mediaController) {
        val listener = object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)

                Log.d(TAG, "onMediaItemTransition, reason $reason")
                /*
                when(reason) {
                    // media has been repeated
                    Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT -> {}
                    // automatically moving to the next item
                    Player.MEDIA_ITEM_TRANSITION_REASON_AUTO -> {}
                    // manual "seek" meaning >| button is pressed
                    Player.MEDIA_ITEM_TRANSITION_REASON_SEEK -> {}
                    // the playlist has changed
                    Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED -> return
                    else -> {
                        Log.e(TAG, "Unknown reason for media item transition")
                        return
                    }
                }
                 */
                if (mediaItem == null) {
                    musiqueViewModel.unsetCurrentSong()
                    musiqueViewModel.currentPlaylistIdx = -1
                    return@onMediaItemTransition
                }

                val songs = musiqueViewModel.currentPlaylist
                val idx = musiqueViewModel.currentPlaylistIdx
                mediaController?.run {
                    val nextIdx = mediaController.currentMediaItemIndex
                    if (nextIdx >= songs.size) {
                        return@run
                    }
                    val nextSong = songs[nextIdx]
                    musiqueViewModel.setCurrentSong(nextSong)
                    musiqueViewModel.currentPlaylistIdx = nextIdx
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                musiqueViewModel.isPlaying = isPlaying
            }
        }.also {
            mediaController?.addListener(it)
        }

        onDispose {
            mediaController?.removeListener(listener)
        }
    }


    /*
    val onSongClick = { songs: List<Song>, idx: Int ->
        Log.d(TAG, "Song clicked ${songs[idx]}")
        musiqueViewModel.currentPlaylist = songs
        musiqueViewModel.currentPlaylistIdx = idx
        val song = songs[idx]
        mediaController.stop()
        mediaController.reset()
        mediaController.setDataSource(song.path)
        mediaController.prepare() //TODO Async?
        mediaController.start()
        musiqueViewModel.isPlaying = true
        musiqueViewModel.setCurrentSong(song)
        musiqueViewModel.setCurrentPlaylistScrollState(idx)
    }
     */
    val onSongClick: (songs: List<Song>, idx: Int) -> Unit = { songs: List<Song>, idx: Int ->
        Log.d(TAG, "Song clicked ${songs[idx]}")

        val mediaItems: List<MediaItem> = songs.map { song ->
            MediaItem.Builder()
                .setMediaId(song.id.toString())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist)
                        .build()
                )
                .setUri(song.path)
                .build()
        }
        mediaController?.run {
            musiqueViewModel.currentPlaylist = songs
            musiqueViewModel.currentPlaylistIdx = idx
            stop()
            setMediaItems(mediaItems, idx, 0)
            prepare()
            play()
            //musiqueViewModel.isPlaying = true
            musiqueViewModel.setCurrentSong(songs[idx])
            musiqueViewModel.setCurrentPlaylistScrollState(idx)
        }
    }
    /*
    val onSongClickInPlaylist = { idx: Int ->
        musiqueViewModel.currentPlaylistIdx = idx
        val song = musiqueViewModel.currentPlaylist[idx]
        mediaController.stop()
        mediaController.reset()
        mediaController.setDataSource(song.path)
        mediaController.prepare() //TODO Async?
        mediaController.start()
        musiqueViewModel.isPlaying = true
        musiqueViewModel.setCurrentSong(song)
    }
     */
    val onSongClickInPlaylist: (songs: List<Song>, idx: Int) -> Unit = { songs: List<Song>, idx: Int ->
        musiqueViewModel.currentPlaylistIdx = idx
        val song = musiqueViewModel.currentPlaylist[idx]
        /*
        val mediaItem = MediaItem.Builder()
            .setMediaId(song.id.toString())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(song.title)
                    .setArtist(song.artist)
                    .build()
            )
            .setUri(song.path)
            .build()
         */
        mediaController?.run {
            //stop()
            if (mediaItemCount <= 0) { // playlist is cleared and needs to be reinitialized
                val mediaItems: List<MediaItem> = songs.map { song ->
                    MediaItem.Builder()
                        .setMediaId(song.id.toString())
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle(song.title)
                                .setArtist(song.artist)
                                .build()
                        )
                        .setUri(song.path)
                        .build()
                }
                setMediaItems(mediaItems)
                prepare()
            }
            seekToDefaultPosition(idx)
            play()
            //musiqueViewModel.isPlaying = true
            musiqueViewModel.setCurrentSong(song)
        }
    }
    /*val onSongClick = { song: Song ->
        Log.d(TAG, "Song clicked: $song")
        player.stop()
        player.reset()
        player.setDataSource(song.path)
        player.prepare() //TODO Async?
        player.start()
        musiqueViewModel.isPlaying = true
        musiqueViewModel.setCurrentSong(song)
    }*/
    val changeSongSliderPos: (Float) -> Unit = { position: Float ->
        mediaController?.run {
            seekTo((position * duration).toLong())
            musiqueViewModel.songSliderPosition = position
        }
    }
    
    val onPlayPauseClick: () -> Unit = {
        mediaController?.let { controller ->
            if(controller.isPlaying) {
                controller.pause()
                //musiqueViewModel.isPlaying = false
            } else {
                controller.play()
                //musiqueViewModel.isPlaying = true
            }
        }
    }

    val navController = rememberNavController()

    val onHistoryEntryClick = { historyEntryId: Long ->
        Log.d(TAG, "History entry clicked")
        musiqueViewModel.setSubSongsFromHistoryEntryId(historyEntryId)
        navController.navigate(Screen.SubList.name)
    }

    val onAlbumClick = { albumName: String ->
        Log.d(TAG, "Album clicked")
        musiqueViewModel.setSubSongsFromAlbum(albumName)
        navController.navigate(Screen.SubList.name)
    }

    val topBarRoute = when(navController.currentBackStackEntryAsState().value?.destination?.route) {
        Screen.Home.name -> 0
        Screen.Albums.name -> 1
        Screen.History.name -> 2
        else -> -1
    }

    // --- Pager Setup ---
    val pagerScreens = listOf(Screen.Home, Screen.Albums, Screen.History)
    val pagerState = rememberPagerState(pageCount = { pagerScreens.size })
    val coroutineScope = rememberCoroutineScope()

    val onPlayerTextClick = {
        coroutineScope.launch {
            sheetState.bottomSheetState.expand()
        }
    }
    // This effect syncs the Pager's state to the NavController
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            navController.navigate(
                route = pagerScreens[pagerState.currentPage].name,
            ) {
                launchSingleTop = true
                restoreState = true
                popUpTo(pagerScreens[pagerState.currentPage].name)
            }
        }
    }

    // This effect syncs the NavController's state to the Pager
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(navBackStackEntry) {
        val currentPageIndex = pagerScreens.indexOfFirst { it.name == navBackStackEntry?.destination?.route }
        if (currentPageIndex != -1 && currentPageIndex != pagerState.currentPage) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(currentPageIndex)
            }
        }
    }
    // --- End Pager Setup ---
    val pager = @Composable {
        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> SongListScreen(
                    musiqueViewModel,
                    onSongClick = onSongClick
                )

                1 -> AlbumGridScreen(
                    musiqueViewModel,
                    onClick = onAlbumClick
                )

                2 -> HistoryScreen(
                    musiqueViewModel,
                    onClick = onHistoryEntryClick
                )
            }
        }
    }

    val density = LocalDensity.current
    BottomSheetScaffold(
        scaffoldState = sheetState,
        sheetDragHandle = {},
        sheetShape = RectangleShape,
        sheetPeekHeight = animatedSheetSize,
        sheetContent = {
            MusiqueBottomBar(
                sliderValue = musiqueViewModel.songSliderPosition,
                onValueChange = changeSongSliderPos,
                onPlayPauseClick = onPlayPauseClick,
                onTextClick = onPlayerTextClick,
                modifier = Modifier
                    .onGloballyPositioned{ layoutCoordinates ->
                        val heightInDp = with(density) {
                            layoutCoordinates.size.height.toDp()
                        }
                        if (sheetSize != heightInDp) {
                            sheetSize = heightInDp
                        }
                    }
            )
            CurrentPlaylistScreen(
                musiqueViewModel,
                onSongClick = onSongClickInPlaylist,
                scrollState = musiqueViewModel.currentPlaylistScrollState.value
            )
        },
        topBar = {
            Surface { // I need the surface cause the BottomSheetScaffold broke the topbar coloring
                TabTopBar(
                    buttonNames = listOf("Songs", "Albums", "History"),
                    currentRoute = topBarRoute,
                    imageVectorsFilled = listOf(
                        Icons.Filled.MusicNote,
                        Icons.Filled.LibraryMusic,
                        Icons.Filled.History
                    ),
                    imageVectorsOutlined = listOf(
                        Icons.Outlined.MusicNote,
                        Icons.Outlined.LibraryMusic,
                        Icons.Outlined.History
                    ),
                    onClicks = listOf(
                        {
                            navController.navigate(
                                route = Screen.Home.name,
                                navOptions = NavOptions.Builder()
                                    .setPopUpTo(
                                        route = Screen.Home.name,
                                        inclusive = true
                                    ).build()
                            )
                        },
                        {
                            navController.navigate(Screen.Albums.name,
                                navOptions = NavOptions.Builder()
                                    .setPopUpTo(
                                        route = Screen.Albums.name,
                                        inclusive = true
                                    ).build()
                            )
                        },
                        {
                            navController.navigate(Screen.History.name,
                                navOptions = NavOptions.Builder()
                                    .setPopUpTo(
                                        route = Screen.History.name,
                                        inclusive = true
                                    ).build()
                            )
                        }
                    ),
                    updateOnClick = {
                        Log.d(TAG, "Update clicked")
                        musiqueViewModel.scheduleWorker()
                    }
                )

            }
            //Text(
            //    text = "Musique",
            //    fontFamily = FontFamily.Serif,
            //    fontSize = 50.sp,
            //    textAlign = TextAlign.Center,
            //    modifier = Modifier
            //        .fillMaxWidth()
            //        .padding(top = 8.dp)
            //)
            }
        ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.name,
            modifier = modifier
                .padding(innerPadding)
        ) {
            composable(route = Screen.Home.name) {
                pager()
            }
            composable(route = Screen.Albums.name) {
                pager()
            }
            composable(route = Screen.History.name) {
                pager()
            }
            /*
            composable(route = Screen.Home.name) {
                SongListScreen(
                    musiqueViewModel,
                    onSongClick = onSongClick
                )
            }
            composable(route = Screen.Albums.name) {
                AlbumGridScreen(
                    musiqueViewModel,
                    onClick = onAlbumClick
                )
            }
            composable(route = Screen.History.name) {
                HistoryScreen(
                    musiqueViewModel,
                    onClick = onHistoryEntryClick
                )
            }
             */
            composable(route = Screen.SubList.name) {
                SongSubListScreen(
                    musiqueViewModel,
                    onSongClick = onSongClick,
                )
            }
        }
        BackHandler(sheetState.bottomSheetState.currentValue == SheetValue.Expanded) {
            coroutineScope.launch {
                sheetState.bottomSheetState.partialExpand()
            }
        }
    }

    /*
    Scaffold(
        topBar = {
            TabTopBar(
                buttonNames = listOf("Songs", "Albums", "History"),
                currentRoute = topBarRoute,
                imageVectorsFilled = listOf(
                    Icons.Filled.MusicNote,
                    Icons.Filled.LibraryMusic,
                    Icons.Filled.History
                ),
                imageVectorsOutlined = listOf(
                    Icons.Outlined.MusicNote,
                    Icons.Outlined.LibraryMusic,
                    Icons.Outlined.History
                ),
                onClicks = listOf(
                    {
                        navController.navigate(
                            route = Screen.Home.name,
                            navOptions = NavOptions.Builder()
                                .setPopUpTo(
                                    route = Screen.Home.name,
                                    inclusive = true
                                ).build()
                        )
                    },
                    {
                        navController.navigate(Screen.Albums.name,
                            navOptions = NavOptions.Builder()
                            .setPopUpTo(
                                route = Screen.Albums.name,
                                inclusive = true
                            ).build()
                        )
                    },
                    {
                        navController.navigate(Screen.History.name,
                            navOptions = NavOptions.Builder()
                            .setPopUpTo(
                                route = Screen.History.name,
                                inclusive = true
                            ).build()
                        )
                    }
                ),
                updateOnClick = {
                    Log.d(TAG, "Update clicked")
                    musiqueViewModel.scheduleWorker()
                }
            )
            //Text(
            //    text = "Musique",
            //    fontFamily = FontFamily.Serif,
            //    fontSize = 50.sp,
            //    textAlign = TextAlign.Center,
            //    modifier = Modifier
            //        .fillMaxWidth()
            //        .padding(top = 8.dp)
            //)
        },
        bottomBar = {
            MusiqueBottomBar(
                sliderValue = musiqueViewModel.songSliderPosition,
                onValueChange = changeSongSliderPos,
                onPlayPauseClick = onPlayPauseClick
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.name,
            modifier = modifier
                .padding(innerPadding)
        ) {
            composable(route = Screen.Home.name) {
                pager()
            }
            composable(route = Screen.Albums.name) {
                pager()
            }
            composable(route = Screen.History.name) {
                pager()
            }
            /*
            composable(route = Screen.Home.name) {
                SongListScreen(
                    musiqueViewModel,
                    onSongClick = onSongClick
                )
            }
            composable(route = Screen.Albums.name) {
                AlbumGridScreen(
                    musiqueViewModel,
                    onClick = onAlbumClick
                )
            }
            composable(route = Screen.History.name) {
                HistoryScreen(
                    musiqueViewModel,
                    onClick = onHistoryEntryClick
                )
            }
             */
            composable(route = Screen.SubList.name) {
                SongSubListScreen(
                    musiqueViewModel,
                    onSongClick = onSongClick,
                )
            }
        }
    }
     */
}