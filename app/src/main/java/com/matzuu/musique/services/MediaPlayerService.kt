package com.matzuu.musique.services

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.matzuu.musique.viewmodels.MusiqueViewModel

private const val TAG = "MediaPlayerService"

private const val COMMAND_REMOVE_PLAYER = "REMOVE_PLAYER"

class MediaPlayerService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    private val removePlayerCommand = SessionCommand(COMMAND_REMOVE_PLAYER, Bundle.EMPTY)

    private val removePlayerButton by lazy {
        CommandButton.Builder()
            .setDisplayName("Remove Player")
            .setIconResId(androidx.media3.session.R.drawable.media3_icon_block)
            .setSessionCommand(removePlayerCommand)
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        val callback = object : MediaSession.Callback {
            @OptIn(UnstableApi::class)
            override fun onConnect(
                session: MediaSession,
                controller: MediaSession.ControllerInfo
            ): MediaSession.ConnectionResult {
                val availableCommands = MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                    .add(removePlayerCommand)
                    .build()
                
                return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                    .setAvailableSessionCommands(availableCommands)
                    .setCustomLayout(listOf(removePlayerButton))
                    .build()
            }

            @OptIn(UnstableApi::class)
            override fun onCustomCommand(
                session: MediaSession,
                controller: MediaSession.ControllerInfo,
                customCommand: SessionCommand,
                args: Bundle
            ): ListenableFuture<SessionResult> {
                if (customCommand.customAction == COMMAND_REMOVE_PLAYER) {
                    onRemovePlayerRequested()
                    return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                }
                return Futures.immediateFuture(SessionResult(SessionError.ERROR_NOT_SUPPORTED))
            }
        }

        val player = ExoPlayer.Builder(this).build()
        val session = MediaSession.Builder(this, player)
            .setCallback(callback)
            .build()
        mediaSession = session
    }

    private fun onRemovePlayerRequested() {
        mediaSession?.player?.let { player ->
            player.stop()
            player.clearMediaItems()
        }

        Log.d(TAG, "onRemovePlayerRequested")

        // TODO: Save the current state in playlist

        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
        }
        mediaSession = null
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        mediaSession?.player?.release()
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }
}
