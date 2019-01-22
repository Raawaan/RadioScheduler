package com.example.rawan.radio.audioPlayer


import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.devbrackets.android.exomedia.util.TimeFormatUtil
import com.devbrackets.android.playlistcore.components.playlisthandler.DefaultPlaylistHandler
import com.devbrackets.android.playlistcore.components.playlisthandler.PlaylistHandler
import com.devbrackets.android.playlistcore.data.MediaProgress
import com.devbrackets.android.playlistcore.data.PlaybackState
import com.devbrackets.android.playlistcore.listener.PlaylistListener
import com.devbrackets.android.playlistcore.listener.ProgressListener
import com.devbrackets.android.playlistcore.service.BasePlaylistService
import com.example.rawan.radio.*
import com.example.rawan.radio.audioPlayer.model.AudioPlayerModel
import com.example.rawan.radio.audioPlayer.model.ListOfStreams
import com.example.rawan.radio.audioPlayer.model.MediaItem
import com.example.rawan.radio.audioPlayer.presenter.AudioPlayerPresenter
import com.example.rawan.radio.audioPlayer.view.AudioPlayerUI
import com.example.rawan.radio.radioDatabase.RadioDatabase
import kotlinx.android.synthetic.main.audio_player_activity.view.*

/**
 * A simple service that extends [BasePlaylistService] in order to provide
 * the application specific information required.
 */
class MediaService : BasePlaylistService<MediaItem, PlaylistManager>(), PlaylistListener<MediaItem>, ProgressListener, AudioPlayerUI {

    lateinit var view: View
    lateinit var audioPlayerPresenter: AudioPlayerPresenter
    private var shouldSetDuration: Boolean = false
    private var userInteracting: Boolean = false
    lateinit var amanager: AudioManager
    private lateinit var playlistManagerStart: PlaylistManager
    private val glide: RequestManager by lazy { Glide.with(this) }
    override val playlistManager by lazy { (applicationContext as MyApplication).playlistManager }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        val soundOn= MySharedPreference.sharedPreference(this).getBoolean("sound",true)
        if(soundOn){
            amanager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            amanager.setStreamVolume(AudioManager.STREAM_MUSIC,amanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0)
        }
//        startForeground(1, NotificationUtilities.notification(this))

        view = LayoutInflater.from(this).inflate(R.layout.audio_player_activity,null)
        audioPlayerPresenter= AudioPlayerPresenter(this, AudioPlayerModel(
                RadioDatabase.getInstance(this)
        ))

        Toast.makeText(this,"Stream Started",Toast.LENGTH_LONG).show()
        // Adds the audio player implementation, otherwise there's nothing to play media with
        playlistManager.mediaPlayers.add(AudioApi(applicationContext))
        playlistManagerStart = (applicationContext as MyApplication).playlistManager
        playlistManagerStart.registerPlaylistListener(this)
        playlistManagerStart.registerProgressListener(this)

        //Makes sure to retrieve the current playback information
        init(StartService.radioId())
        updateCurrentPlaybackInformation()
    }


    override fun onDestroy() {
        super.onDestroy()
        // Releases and clears all the MediaPlayersMediaImageProvider
        playlistManager.mediaPlayers.forEach {
            it.release()
        }
        playlistManager.mediaPlayers.clear()
        playlistManagerStart.unRegisterPlaylistListener(this)
        playlistManagerStart.unRegisterProgressListener(this)
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
        playlistManagerStart.currentItemChange?.let {
            onPlaylistItemChanged(it.currentItem, it.hasNext, it.hasPrevious)
        }

        if (playlistManagerStart.currentPlaybackState != PlaybackState.STOPPED) {
            onPlaybackStateChanged(playlistManagerStart.currentPlaybackState)
        }

        playlistManagerStart.currentProgress?.let {
            onProgressUpdated(it)
        }
    }

    /**
     * Performs the initialization of the views and any other
     * general setup
     */
    @RequiresApi(Build.VERSION_CODES.O)
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
        playlistManagerStart = (applicationContext as MyApplication).playlistManager

        val mediaItems = listOfStreams.map {
            MediaItem(it)

        }

        playlistManagerStart.setParameters(mediaItems, 0)
        playlistManagerStart.id = StartService.PLAYLIST_ID.toLong()

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
        view.previousButton.setOnClickListener { playlistManagerStart.invokePrevious() }
        view.playPauseButton.setOnClickListener { playlistManagerStart.invokePausePlay() }
        view.nextButton.setOnClickListener { playlistManagerStart.invokeNext() }
    }

    /**
     * Starts the audio playback if necessary.
     *
     * @param forceStart True if the audio should be started from the beginning even if it is currently playing
     */
    private fun startPlayback(forceStart: Boolean) {
        //If we are changing audio files, or we haven't played before then start the playback
        if (forceStart || playlistManagerStart.currentPosition != 0) {
            playlistManagerStart.currentPosition = 0
            playlistManagerStart.play(0, false)
        }
    }
}