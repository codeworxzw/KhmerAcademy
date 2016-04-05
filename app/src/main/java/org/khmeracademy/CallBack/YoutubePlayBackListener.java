package org.khmeracademy.CallBack;

import com.google.android.youtube.player.YouTubePlayer;

/**
 * Created by PC1 on 2/9/2016.
 */
public interface YoutubePlayBackListener {
    void onPlayNextVideo();
    void onPlayerInitialize(YouTubePlayer youTubePlayer);
    void onFullScreen();
}
