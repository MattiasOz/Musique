package com.matzuu.musique.services

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.matzuu.musique.MainActivity

private const val TAG = "MediaPlayerService"

private const val COMMAND_REMOVE_PLAYER = "REMOVE_PLAYER"
private const val COMMAND_SEEK_BACK = "SEEK_BACKWARD"
private const val COMMAND_SEEK_FORWARD = "SEEK_FORWARD"

class MediaPlayerService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    private val removePlayerCommand = SessionCommand(COMMAND_REMOVE_PLAYER, Bundle.EMPTY)
    private val seekBackCommand = SessionCommand(COMMAND_SEEK_BACK, Bundle().apply { putInt("androidx.media3.session.command.COMPACT_VIEW_INDEX", 1) })
    private val seekForwardCommand = SessionCommand(COMMAND_SEEK_FORWARD, Bundle.EMPTY)

    private val removePlayerButton by lazy {
        CommandButton.Builder()
            .setDisplayName("Remove Player")
            .setIconResId(androidx.media3.session.R.drawable.media3_icon_block)
            .setSessionCommand(removePlayerCommand)
            .build()
    }
    private val seekBackPlayerButton by lazy {
        CommandButton.Builder()
            .setDisplayName("Seek Back")
            .setIconResId(androidx.media3.session.R.drawable.media3_icon_skip_back_5)
            .setSessionCommand(seekBackCommand)
            //.setPlayerCommand(Player.COMMAND_SEEK_BACK)
            .build()
    }
    private val seekForwardPlayerButton by lazy {
        CommandButton.Builder()
            .setDisplayName("Seek Forward")
            .setIconResId(androidx.media3.session.R.drawable.media3_icon_skip_forward_15)
            .setSessionCommand(seekForwardCommand)
            //.setPlayerCommand(Player.COMMAND_SEEK_FORWARD)
            .build()
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        setMediaNotificationProvider(object : DefaultMediaNotificationProvider(this) {
            override fun getMediaButtons(
                session: MediaSession,
                playerCommands: Player.Commands,
                customLayout: ImmutableList<CommandButton>,
                showCustomActionsInCompactView: Boolean
            ): ImmutableList<CommandButton> {
                // Here you can manually assemble the list of ALL buttons
                // the notification should try to display.
                val defaultButtons = super.getMediaButtons(session, playerCommands, customLayout, showCustomActionsInCompactView)
                val pausePauseButton = defaultButtons.find { it.displayName in arrayOf("Pause", "Play") }
                //val skipPrevButton = defaultButtons.first{ it.displayName == "Seek to previous item"}
                //val skipNextButton = defaultButtons.first{ it.displayName == "Seek to next item"}
                //val dumbCompactMenuCheat = skipPrevButton.extras
                //val bud = Bundle().apply { putInt("androidx.media3.session.command.COMPACT_VIEW_INDEX", -1) }


                //val tmp  = seekForwardPlayerButton.setExtras(dumbCompactMenuCheat).build()
                val buttons = mutableListOf<CommandButton>()
                pausePauseButton?.run{
                    buttons.add(pausePauseButton)
                }
                buttons.add(seekBackPlayerButton)
                buttons.add(seekForwardPlayerButton)
                buttons.add(removePlayerButton)
                // Add play/pause manually or it might disappear
                return ImmutableList.copyOf(buttons)
                //return super.getMediaButtons(session, playerCommands, customLayout, showCustomActionsInCompactView)
            }
        })

        val callback = object : MediaSession.Callback {
            @OptIn(UnstableApi::class)
            override fun onConnect(
                session: MediaSession,
                controller: MediaSession.ControllerInfo
            ): MediaSession.ConnectionResult {
                val availableCommands = MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                    .add(removePlayerCommand)
                    .add(seekBackCommand)
                    .add(seekForwardCommand)
                    .build()

                //val availablePlayerCommands = MediaSession.ConnectionResult.DEFAULT_PLAYER_COMMANDS
                //    .buildUpon()
                //    .addAll(Player.COMMAND_SEEK_BACK, Player.COMMAND_SEEK_FORWARD)
                //    .build()

                return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                    .setAvailableSessionCommands(availableCommands)
                    //.setAvailablePlayerCommands(availablePlayerCommands)
                    .setCustomLayout(listOf(removePlayerButton, seekBackPlayerButton, seekForwardPlayerButton))
                    .build()
            }

            @OptIn(UnstableApi::class)
            override fun onCustomCommand(
                session: MediaSession,
                controller: MediaSession.ControllerInfo,
                customCommand: SessionCommand,
                args: Bundle
            ): ListenableFuture<SessionResult> {
                //if (customCommand.customAction == COMMAND_REMOVE_PLAYER) {
                //    onRemovePlayerRequested()
                //    return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                //}
                when(customCommand.customAction) {
                    COMMAND_REMOVE_PLAYER -> {
                        onRemovePlayerRequested()
                        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                    }
                    COMMAND_SEEK_BACK -> {
                        onSeekBackRequested()
                        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                    }
                    COMMAND_SEEK_FORWARD -> {
                        onSeekForwardRequested()
                        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                    }
                }
                return Futures.immediateFuture(SessionResult(SessionError.ERROR_NOT_SUPPORTED))
            }
        }

        val intent = Intent(this, MainActivity::class.java).apply{
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val player = ExoPlayer.Builder(this).build()
        val session = MediaSession.Builder(this, player)
            .setCallback(callback)
            .setSessionActivity(pendingIntent)
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

    private fun onSeekBackRequested() {
        mediaSession?.player?.seekBack()
    }

    private fun onSeekForwardRequested() {
        mediaSession?.player?.seekForward()
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
