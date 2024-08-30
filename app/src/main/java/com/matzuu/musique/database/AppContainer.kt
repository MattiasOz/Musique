package com.matzuu.musique.database

import android.content.Context

interface AppContainer {
    val songRepository: SongRepository
}

class DefaultAppContainer(
    private val context: Context
) : AppContainer {
    override val songRepository: SongRepository by lazy {
        SongRepositoryImpl(context)
    }
}