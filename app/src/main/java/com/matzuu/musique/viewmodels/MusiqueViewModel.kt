package com.matzuu.musique.viewmodels

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.matzuu.musique.MusiqueApplication
import com.matzuu.musique.database.SongRepository
import com.matzuu.musique.models.Album
import com.matzuu.musique.models.Song
import com.matzuu.musique.uiStates.AlbumListUiState
import com.matzuu.musique.uiStates.CurrentPlaylistUiState
import com.matzuu.musique.uiStates.CurrentSongUiState
import com.matzuu.musique.uiStates.HistoryListUiState
import com.matzuu.musique.uiStates.MusicListUiState
import com.matzuu.musique.utils.ListPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi

private const val TAG = "MusiqueViewModel"

class MusiqueViewModel(
    private val songRepository: SongRepository,
    val mediaPlayer: MediaPlayer
) : ViewModel() {

    //var musicListUiState: MusicListUiState by mutableStateOf(MusicListUiState.Loading)
    //    private set

    private val _musicListUiState = MutableStateFlow<MusicListUiState>(MusicListUiState.Loading)
    val musicListUiState: StateFlow<MusicListUiState> = _musicListUiState.asStateFlow()

    private val _musicSubListUiState = MutableStateFlow<MusicListUiState>(MusicListUiState.Loading)
    val musicSubListUiState: StateFlow<MusicListUiState> = _musicSubListUiState.asStateFlow()

    var currentSongUiState: CurrentSongUiState by mutableStateOf(CurrentSongUiState.Unset)
        private set

    var currentPlaylistUiState: CurrentPlaylistUiState by mutableStateOf(CurrentPlaylistUiState.Unset)
        private set

    var albumListUiState: AlbumListUiState by mutableStateOf(AlbumListUiState.Loading)
        private set

    var historyListUiState: HistoryListUiState by mutableStateOf(HistoryListUiState.Loading)
        private set

    var songSliderPosition by mutableFloatStateOf(0f)
    var isPlaying by mutableStateOf(false)
    var currentTime by mutableIntStateOf(0)
    var totalTime by mutableIntStateOf(0)

    var currentPlaylist by mutableStateOf<List<Song>>(listOf())
    var currentPlaylistIdx by mutableIntStateOf(0)



    @OptIn(ExperimentalCoroutinesApi::class)
    val pagedSongsFlow: Flow<PagingData<Song>> = _musicListUiState
        .filterIsInstance<MusicListUiState.Success>()
        .flatMapLatest { state ->
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    prefetchDistance = 20,
                    initialLoadSize = 40
                ),
                pagingSourceFactory = {
                    ListPagingSource(state.songs)
                }
            ).flow
        }.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagedSubSongsFlow: Flow<PagingData<Song>> = _musicSubListUiState
        .filterIsInstance<MusicListUiState.Success>()
        .flatMapLatest { state ->
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    prefetchDistance = 20,
                    initialLoadSize = 10
                ),
                pagingSourceFactory = {
                    ListPagingSource(state.songs)
                }
            ).flow
        }.cachedIn(viewModelScope)

    init {
        fetchFullSongList()
        fetchAlbumList()
        updateHistoryEntries()
    }

    fun insertFullSongList(songs: List<Song>) {
        viewModelScope.launch {
            songRepository.insertFullSongList(songs)
        }
    }

    fun insertFullAlbumList(albums: List<Album>) {
        viewModelScope.launch {
            songRepository.insertFullAlbumList(albums)
        }
    }

    fun createHistoryEntry(playlistName: String, songs: List<Song>) {
        viewModelScope.launch {
            songRepository.createHistoryEntry(playlistName, songs)
        }
    }

    fun fetchFullSongList() {
        _musicListUiState.value = MusicListUiState.Loading
        viewModelScope.launch {
            val songs = songRepository.getFullSongList()
            _musicListUiState.value = MusicListUiState.Success(songs = songs)
            Log.d(TAG, "Set songs ${musicListUiState.value}")
        }
    }

    fun fetchAlbumList() {
        albumListUiState = AlbumListUiState.Loading
        viewModelScope.launch {
            val albums = songRepository.getAlbumList()
            albumListUiState = AlbumListUiState.Success(albums = albums)
            Log.d(TAG, "Albums set $albumListUiState")
        }
    }

    fun setSongs(songs: List<Song>) {
        viewModelScope.launch(
            context = Dispatchers.IO
        ) {
            _musicListUiState.value = MusicListUiState.Success(songs = songs)
            Log.d(TAG, "Set songs $musicListUiState")
        }
    }

    fun setSubSongs(songs: List<Song>) {
        viewModelScope.launch(
        ) {
            _musicSubListUiState.value = MusicListUiState.Success(songs = songs)
            Log.d(TAG, "Set sublist songs $musicSubListUiState")
        }
    }

    fun setSubSongsFromHistoryEntryId(id: Long) {
        _musicSubListUiState.value = MusicListUiState.Loading
        currentPlaylistUiState = CurrentPlaylistUiState.Unset
        viewModelScope.launch(
        ) {
            val songs = songRepository.getHistorySongs(id)
            _musicSubListUiState.value = MusicListUiState.Success(songs = songs)
            Log.d(TAG, "Set sublist songs from history $musicSubListUiState")
            val historyEntry = songRepository.getHistoryEntry(id)
            currentPlaylistUiState = CurrentPlaylistUiState.Success(historyEntry.name)
        }
    }

    fun setSubSongsFromAlbum(album: String) {
        _musicSubListUiState.value = MusicListUiState.Loading
        currentPlaylistUiState = CurrentPlaylistUiState.Unset
        viewModelScope.launch {
            val songs = songRepository.getSongsFromAlbum(album)
            _musicSubListUiState.value = MusicListUiState.Success(songs = songs)
            Log.d(TAG, "Set sublist songs from album ${songs.forEach { "${it.title}\n}"}}")
            currentPlaylistUiState = CurrentPlaylistUiState.Success(album)
        }
    }

    fun setAlbums(albums: List<Album>) {
        viewModelScope.launch(
            context = Dispatchers.IO
        ) {
            albumListUiState = AlbumListUiState.Success(albums = albums)
            Log.d(TAG, "Set albums $albumListUiState")
        }
    }

    fun setCurrentSong(song: Song) {
        viewModelScope.launch(
            context = Dispatchers.IO
        ) {
            Log.d(TAG, "Song set to $song")
            currentSongUiState = CurrentSongUiState.Success(song = song)
        }
    }

    fun updateHistoryEntries() {
        viewModelScope.launch {
            val historyEntries = songRepository.getHistoryEntries()
            historyListUiState = HistoryListUiState.Success(historyEntries = historyEntries)
        }
    }

    fun scheduleWorker() {
        viewModelScope.launch {
            songRepository.enqueueWorker()
        }
    }

    fun updateValues(){
        viewModelScope.launch {
            while (isPlaying) {
                songSliderPosition = mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration.toFloat()
                currentTime = mediaPlayer.currentPosition
                totalTime = mediaPlayer.duration
                delay(100)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as MusiqueApplication
                val songRepository = application.container.songRepository
                val player = MediaPlayer()
                MusiqueViewModel(
                    songRepository = songRepository,
                    mediaPlayer = player
                )
            }
        }
    }
}