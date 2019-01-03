package com.example.rawan.radio.audioPlayer



import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.bumptech.glide.Glide
import com.example.rawan.radio.R

import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition

import com.devbrackets.android.playlistcore.components.image.ImageProvider
import com.example.rawan.radio.audioPlayer.model.MediaItem


class MediaImageProvider(context: Context, private val onImageUpdated: () -> Unit) : ImageProvider<MediaItem> {

    private val glide: RequestManager by lazy { Glide.with(context.applicationContext) }

    private val notificationImageTarget = NotificationImageTarget()
    private val remoteViewImageTarget = RemoteViewImageTarget()

    private val defaultNotificationImage: Bitmap by lazy { BitmapFactory.decodeResource(context.resources,
           R.drawable.ic_radio_black_24dp ) }

    private var notificationImage: Bitmap? = null
    override var remoteViewArtwork: Bitmap? = null
        private set

    override val notificationIconRes: Int
        get() =  R.drawable.ic_radio_black_24dp

    override val remoteViewIconRes: Int
        get() =  R.drawable.ic_radio_black_24dp

    override val largeNotificationImage: Bitmap?
        get() = if (notificationImage != null) notificationImage else defaultNotificationImage

    override fun updateImages(playlistItem: MediaItem) {
        glide.asBitmap().load(playlistItem.thumbnailUrl).into(notificationImageTarget)
        glide.asBitmap().load(playlistItem.artworkUrl).into(remoteViewImageTarget)
    }

    /**
     * A class used to listen to the loading of the large notification images and perform
     * the correct functionality to update the notification once it is loaded.
     *
     * **NOTE:** This is a Glide Image loader class
     */
    private inner class NotificationImageTarget : SimpleTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            notificationImage = resource
            onImageUpdated()
        }
    }

    /**
     * A class used to listen to the loading of the large lock screen images and perform
     * the correct functionality to update the artwork once it is loaded.
     *
     * **NOTE:** This is a Glide Image loader class
     */
    private inner class RemoteViewImageTarget : SimpleTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            remoteViewArtwork = resource
            onImageUpdated()
        }
    }
}
