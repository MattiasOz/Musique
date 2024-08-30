package com.matzuu.musique

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.matzuu.musique.ui.screens.SongListScreen
import com.matzuu.musique.utils.fileSearch
import com.matzuu.musique.viewmodels.MusiqueViewModel
import com.matzuu.musique.workers.SongSyncWorker


@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
) {
    val musiqueViewModel: MusiqueViewModel = viewModel(factory = MusiqueViewModel.Factory)
    SongSyncWorker.viewmodel = musiqueViewModel
    musiqueViewModel.scheduleWorker()

    Scaffold { innerPadding ->
        NavHost(
            navController = rememberNavController(),
            startDestination = Screen.Home.name,
            modifier = modifier
                .padding(innerPadding)
        ) {
            composable(route = Screen.Home.name) {
                SongListScreen(musiqueViewModel)
            }
        }
    }
}