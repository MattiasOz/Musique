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
import com.matzuu.musique.uiStates.CurrentSongUiState
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

    var currentSongUiState: CurrentSongUiState by mutableStateOf(CurrentSongUiState.Unset)
        private set

    var albumListUiState: AlbumListUiState by mutableStateOf(AlbumListUiState.Loading)
        private set

    val historyListUiState: AlbumListUiState by mutableStateOf(AlbumListUiState.Loading)
        private set

    var songSliderPosition by mutableFloatStateOf(0f)
    var isPlaying by mutableStateOf(false)
    var currentTime by mutableIntStateOf(0)
    var totalTime by mutableIntStateOf(0)

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

    init {
        //TODO the init stuff
    }

    fun setSongs(songs: List<Song>) {
        viewModelScope.launch(
            context = Dispatchers.IO
        ) {
            _musicListUiState.value = MusicListUiState.Success(songs = songs)
            Log.d(TAG, "Set songs $musicListUiState")
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