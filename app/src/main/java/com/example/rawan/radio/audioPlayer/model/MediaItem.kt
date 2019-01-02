package com.example.rawan.radio.audioPlayer.model

import com.devbrackets.android.playlistcore.annotation.SupportedMediaType
import com.devbrackets.android.playlistcore.api.PlaylistItem
import com.devbrackets.android.playlistcore.manager.BasePlaylistManager

/**
 * A custom [PlaylistItem]
 * to hold the information pertaining to the audio and video items
 */
class MediaItem(private val sample: ListOfStreams) : PlaylistItem {

    override val id: Long
        get() = 0

    override val downloaded: Boolean
        get() = false

    @SupportedMediaType
    override val mediaType: Int
        get() = BasePlaylistManager.AUDIO

    override val mediaUrl: String?
        get() = sample.stream

    override val downloadedMediaUri: String?
        get() = null

    override val thumbnailUrl: String?
        get() = sample.radioImage

    override val artworkUrl: String?
        get() = sample.radioImage

    override val title: String?
        get() = sample.radioName

    override val album: String?
        get() = sample.radioName

    override val artist: String?
        get() = sample.radioName
}