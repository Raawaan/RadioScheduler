package com.example.rawan.radio.audioPlayer


import android.widget.Toast
import com.devbrackets.android.playlistcore.components.playlisthandler.DefaultPlaylistHandler
import com.devbrackets.android.playlistcore.components.playlisthandler.PlaylistHandler
import com.devbrackets.android.playlistcore.service.BasePlaylistService
import com.example.rawan.radio.MyApplication
import com.example.rawan.radio.audioPlayer.model.MediaItem

/**
 * A simple service that extends [BasePlaylistService] in order to provide
 * the application specific information required.
 */
class MediaService : BasePlaylistService<MediaItem, PlaylistManager>() {


    override val playlistManager by lazy { (applicationContext as MyApplication).playlistManager }

    override fun onCreate() {
        super.onCreate()
        Toast.makeText(this,"Stream Started",Toast.LENGTH_LONG).show()
        // Adds the audio player implementation, otherwise there's nothing to play media with
        playlistManager.mediaPlayers.add(AudioApi(applicationContext))
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this,"Media service Stopped", Toast.LENGTH_LONG).show()
        // Releases and clears all the MediaPlayersMediaImageProvider
        playlistManager.mediaPlayers.forEach {
            it.release()
        }
        playlistManager.mediaPlayers.clear()
    }

    override fun newPlaylistHandler(): PlaylistHandler<MediaItem> {
        val imageProvider = MediaImageProvider(applicationContext) {
            playlistHandler.updateMediaControls()
        }

        return DefaultPlaylistHandler.Builder(
                applicationContext,
                javaClass,
                playlistManager,
                imageProvider
        ).build()
    }
}