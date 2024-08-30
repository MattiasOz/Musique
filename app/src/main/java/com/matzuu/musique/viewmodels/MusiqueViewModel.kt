package com.matzuu.musique.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.matzuu.musique.MusiqueApplication
import com.matzuu.musique.database.SongRepository
import com.matzuu.musique.models.Song
import com.matzuu.musique.uiStates.MusicListUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "MusiqueViewModel"

class MusiqueViewModel(
    private val songRepository: SongRepository
) : ViewModel() {

    var musicListUiState: MusicListUiState by mutableStateOf(MusicListUiState.Loading)
        private set

    fun setSongs(songs: List<Song>) {
        viewModelScope.launch(
            context = Dispatchers.IO
        ) {
            musicListUiState = MusicListUiState.Success(songs = songs)
            Log.d(TAG, "Set songs $musicListUiState")
        }
    }

    fun scheduleWorker() {
        viewModelScope.launch {
            songRepository.enqueueWorker()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as MusiqueApplication
                val songRepository = application.container.songRepository
                MusiqueViewModel(
                    songRepository = songRepository
                )
            }
        }
    }
}