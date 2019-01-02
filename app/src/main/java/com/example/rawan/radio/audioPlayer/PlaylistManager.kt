package com.example.rawan.radio.audioPlayer

import android.app.Application
import com.devbrackets.android.exomedia.listener.VideoControlsButtonListener

import com.devbrackets.android.playlistcore.manager.ListPlaylistManager
import com.example.rawan.radio.audioPlayer.model.MediaItem

/**
 * A PlaylistManager that extends the [ListPlaylistManager] for use with the
 * [MediaService] which extends [com.devbrackets.android.playlistcore.service.BasePlaylistService].
 */
class PlaylistManager(application: Application) :
        ListPlaylistManager<MediaItem>(application, MediaService::class.java) {
    /**
     * An implementation of the [VideoControlsButtonListener] that provides
     * integration with the playlist service.
     */
    private inner class ControlsListener : VideoControlsButtonListener {
        override fun onPlayPauseClicked(): Boolean {
            invokePausePlay()
            return true
        }

        override fun onPreviousClicked(): Boolean {
            invokePrevious()
            return false
        }

        override fun onNextClicked(): Boolean {
            invokeNext()
            return false
        }

        override fun onRewindClicked(): Boolean {
            return false
        }

        override fun onFastForwardClicked(): Boolean {
            return false
        }
    }
}