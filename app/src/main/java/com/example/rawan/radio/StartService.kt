package com.example.rawan.radio

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.devbrackets.android.exomedia.util.TimeFormatUtil
import com.devbrackets.android.playlistcore.data.MediaProgress
import com.devbrackets.android.playlistcore.data.PlaybackState
import com.devbrackets.android.playlistcore.listener.PlaylistListener
import com.devbrackets.android.playlistcore.listener.ProgressListener
import com.example.rawan.radio.audioPlayer.MediaService
import com.example.rawan.radio.audioPlayer.PlaylistManager
import com.example.rawan.radio.audioPlayer.model.AudioPlayerModel
import com.example.rawan.radio.audioPlayer.model.ListOfStreams
import com.example.rawan.radio.audioPlayer.model.MediaItem
import com.example.rawan.radio.audioPlayer.presenter.AudioPlayerPresenter
import com.example.rawan.radio.audioPlayer.view.AudioPlayerUI
import com.example.rawan.radio.radioDatabase.RadioDatabase
import kotlinx.android.synthetic.main.audio_player_activity.view.*


class StartService: JobService(), PlaylistListener<MediaItem>, ProgressListener, AudioPlayerUI {

        lateinit var view: View
        lateinit var audioPlayerPresenter: AudioPlayerPresenter
        companion object {
            const val EXTRA_INDEX = "EXTRA_INDEX"
            const val PLAYLIST_ID = 4 //Arbitrary, for the example
        }

    override fun onCreate() {
        super.onCreate()

        val intent = Intent(this, MediaService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(intent)
        }
        else{
            startService(intent);
        }
    }
        private var shouldSetDuration: Boolean = false
        private var userInteracting: Boolean = false

        private lateinit var playlistManager: PlaylistManager
//    private val selectedPosition by lazy { intent.extras?.getInt(EXTRA_INDEX, 0) ?: 0 }

        private val glide: RequestManager by lazy { Glide.with(this) }
    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this,"start service Stopped",Toast.LENGTH_LONG).show()

    }
        override fun onStopJob(p0: JobParameters?): Boolean {

            playlistManager.unRegisterPlaylistListener(this)
            playlistManager.unRegisterProgressListener(this)
            return false
        }
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onStartJob(p0: JobParameters?): Boolean {
            Toast.makeText(this,"Stream Started",Toast.LENGTH_LONG).show()
            view = LayoutInflater.from(this).inflate(R.layout.audio_player_activity,null)
            audioPlayerPresenter= AudioPlayerPresenter(this, AudioPlayerModel(
                    RadioDatabase.getInstance(this)
            ))

            p0?.extras?.get("radioId")?.toString()?.toInt()?.let { init(it) }
            playlistManager = (applicationContext as MyApplication).playlistManager
            playlistManager.registerPlaylistListener(this)
            playlistManager.registerProgressListener(this)

            //Makes sure to retrieve the current playback information
            updateCurrentPlaybackInformation()
            return false
        }

        override fun onPlaylistItemChanged(currentItem: MediaItem?, hasNext: Boolean, hasPrevious: Boolean): Boolean {
            shouldSetDuration = true

            //Updates the button states
            view.nextButton.isEnabled = hasNext
            view.previousButton.isEnabled = hasPrevious

            //Loads the new image
        currentItem?.let {
            glide.load(it.artworkUrl).into(view.artworkView!!)
        }

            return true
        }

        override fun onPlaybackStateChanged(playbackState: PlaybackState): Boolean {
            when (playbackState) {
                PlaybackState.STOPPED -> stopSelf()
                PlaybackState.RETRIEVING, PlaybackState.PREPARING, PlaybackState.SEEKING -> restartLoading()
                PlaybackState.PLAYING -> doneLoading(true)
                PlaybackState.PAUSED -> doneLoading(false)
                else -> {}
            }

            return true
        }

        override fun onProgressUpdated(mediaProgress: MediaProgress): Boolean {
            if (shouldSetDuration && mediaProgress.duration > 0) {
                shouldSetDuration = false
                setDuration(mediaProgress.duration)
            }

            if (!userInteracting) {
                view.seekBar.secondaryProgress = (mediaProgress.duration * mediaProgress.bufferPercentFloat).toInt()
                view.seekBar.progress = mediaProgress.position.toInt()
                view.currentPositionView.text = TimeFormatUtil.formatMs(mediaProgress.position)
            }

            return true
        }

        /**
         * Makes sure to update the UI to the current playback item.
         */
        private fun updateCurrentPlaybackInformation() {
            playlistManager.currentItemChange?.let {
                onPlaylistItemChanged(it.currentItem, it.hasNext, it.hasPrevious)
            }

            if (playlistManager.currentPlaybackState != PlaybackState.STOPPED) {
                onPlaybackStateChanged(playlistManager.currentPlaybackState)
            }

            playlistManager.currentProgress?.let {
                onProgressUpdated(it)
            }
        }

        /**
         * Performs the initialization of the views and any other
         * general setup
         */
        private fun init(radioId:Int) {
            setupListeners()

            audioPlayerPresenter.selectStream(radioId)

        }


        /**
         * Called when we receive a notification that the current item is
         * done loading.  This will then update the view visibilities and
         * states accordingly.
         *
         * @param isPlaying True if the audio item is currently playing
         */
        private fun doneLoading(isPlaying: Boolean) {
            loadCompleted()
            updatePlayPauseImage(isPlaying)
        }

        /**
         * Updates the Play/Pause image to represent the correct playback state
         *
         * @param isPlaying True if the audio item is currently playing
         */
        private fun updatePlayPauseImage(isPlaying: Boolean) {
            val resId = if (isPlaying) R.drawable.playlistcore_ic_pause_black else R.drawable.playlistcore_ic_play_arrow_black
            view.playPauseButton.setImageResource(resId)
        }

        /**
         * Used to inform the controls to finalize their setup.  This
         * means replacing the loading animation with the PlayPause button
         */

        private fun loadCompleted() {
            view.playPauseButton.visibility = View.VISIBLE
            view.previousButton.visibility = View.VISIBLE
            view.nextButton.visibility = View.VISIBLE

            view.loadingBar.visibility = View.INVISIBLE
        }

        /**
         * Used to inform the controls to return to the loading stage.
         * This is the opposite of [.loadCompleted]
         */
        private fun restartLoading() {
            view.playPauseButton.visibility = View.INVISIBLE
            view.previousButton.visibility = View.INVISIBLE
            view.nextButton.visibility = View.INVISIBLE

            view.loadingBar.visibility = View.VISIBLE
        }

        /**
         * Sets the [.seekBar]s max and updates the duration text
         *
         * @param duration The duration of the media item in milliseconds
         */
        private fun setDuration(duration: Long) {
            view.seekBar.max = duration.toInt()
            view.durationView.text = TimeFormatUtil.formatMs(duration)
        }

        /**
         * Retrieves the playlist instance and performs any generation
         * of content if it hasn't already been performed.
         *
         * @return True if the content was generated
         */
        private fun setupPlaylistManager(listOfStreams:List<ListOfStreams>): Boolean {
            playlistManager = (applicationContext as MyApplication).playlistManager

            val mediaItems = listOfStreams.map {
                MediaItem(it)

            }

            playlistManager.setParameters(mediaItems, 0)
            playlistManager.id = PLAYLIST_ID.toLong()

            return true
        }

        override fun listOfStreams(listOfStreams: List<ListOfStreams>) {
            startPlayback(setupPlaylistManager(listOfStreams))
        }

        override fun toast(message: String) {
            Toast.makeText(this,"Error While retrieving Streams", Toast.LENGTH_LONG).show()
        }




        /**
         * Links the SeekBarChanged to the [.seekBar] and
         * onClickListeners to the media buttons that call the appropriate
         * invoke methods in the [.playlistManager]
         */
        private fun setupListeners() {
            view.previousButton.setOnClickListener { playlistManager.invokePrevious() }
            view.playPauseButton.setOnClickListener { playlistManager.invokePausePlay() }
            view.nextButton.setOnClickListener { playlistManager.invokeNext() }
        }

        /**
         * Starts the audio playback if necessary.
         *
         * @param forceStart True if the audio should be started from the beginning even if it is currently playing
         */
        private fun startPlayback(forceStart: Boolean) {
            //If we are changing audio files, or we haven't played before then start the playback
            if (forceStart || playlistManager.currentPosition != 0) {
                playlistManager.currentPosition = 0
                playlistManager.play(0, false)
            }
        }
    }