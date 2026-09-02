package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.model.Song

class MusicPlaybackService : Service() {

    private val binder = LocalBinder()
    private var isForegroundServiceRunning = false

    inner class LocalBinder : Binder() {
        fun getService(): MusicPlaybackService = this@MusicPlaybackService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> onPlayAction?.invoke()
            ACTION_PAUSE -> onPauseAction?.invoke()
            ACTION_NEXT -> onNextAction?.invoke()
            ACTION_PREV -> onPrevAction?.invoke()
            ACTION_STOP -> stopForegroundService()
        }
        return START_NOT_STICKY
    }

    fun updateNotification(song: Song?, isPlaying: Boolean) {
        if (song == null) {
            stopForegroundService()
            return
        }

        val notification = buildNotification(song, isPlaying)
        if (!isForegroundServiceRunning) {
            startForeground(NOTIFICATION_ID, notification)
            isForegroundServiceRunning = true
        } else {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    private fun buildNotification(song: Song, isPlaying: Boolean): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseIntent = Intent(this, MusicPlaybackService::class.java).apply {
            action = if (isPlaying) ACTION_PAUSE else ACTION_PLAY
        }
        val playPausePendingIntent = PendingIntent.getService(
            this,
            1,
            playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextIntent = Intent(this, MusicPlaybackService::class.java).apply {
            action = ACTION_NEXT
        }
        val nextPendingIntent = PendingIntent.getService(
            this,
            2,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val prevIntent = Intent(this, MusicPlaybackService::class.java).apply {
            action = ACTION_PREV
        }
        val prevPendingIntent = PendingIntent.getService(
            this,
            3,
            prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseIcon = if (isPlaying) {
            android.R.drawable.ic_media_pause
        } else {
            android.R.drawable.ic_media_play
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(song.title)
            .setContentText("${song.artist} • ${song.album}")
            .setSubText("Rhythm Play")
            .setSmallIcon(R.drawable.img_app_icon)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(isPlaying)
            .setOnlyAlertOnce(true)
            .addAction(android.R.drawable.ic_media_previous, "Previous", prevPendingIntent)
            .addAction(playPauseIcon, if (isPlaying) "Pause" else "Play", playPausePendingIntent)
            .addAction(android.R.drawable.ic_media_next, "Next", nextPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Rhythm Playback Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Music playback controls and media info"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    fun stopForegroundService() {
        if (isForegroundServiceRunning) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            isForegroundServiceRunning = false
        }
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        isForegroundServiceRunning = false
    }

    companion object {
        const val CHANNEL_ID = "rhythm_playback_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_PLAY = "com.example.action.PLAY"
        const val ACTION_PAUSE = "com.example.action.PAUSE"
        const val ACTION_NEXT = "com.example.action.NEXT"
        const val ACTION_PREV = "com.example.action.PREV"
        const val ACTION_STOP = "com.example.action.STOP"

        var onPlayAction: (() -> Unit)? = null
        var onPauseAction: (() -> Unit)? = null
        var onNextAction: (() -> Unit)? = null
        var onPrevAction: (() -> Unit)? = null
    }
}
