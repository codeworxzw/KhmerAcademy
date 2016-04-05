package org.khmeracademy.Fragment;


import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import org.khmeracademy.CallBack.YoutubePlayBackListener;
import org.khmeracademy.Model.PlayerConfig;

public class YouTubeFragment extends YouTubePlayerSupportFragment implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_DIALOG_REQUEST = 1;

    private YoutubePlayBackListener playback;

    private static final String KEY_VIDEO_ID = "KEY_VIDEO_ID";

    private String mVideoId;

    /**
     * Returns a new instance of this Fragment
     *
     * @param videoId The ID of the video to play
     */
    public static YouTubeFragment newInstance(final String videoId) {
        final YouTubeFragment youTubeFragment = new YouTubeFragment();
        final Bundle bundle = new Bundle();
        bundle.putString(KEY_VIDEO_ID, videoId);
        youTubeFragment.setArguments(bundle);
        return youTubeFragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        final Bundle arguments = getArguments();
        playback = (YoutubePlayBackListener) getActivity();

        if (bundle != null && bundle.containsKey(KEY_VIDEO_ID)) {
            mVideoId = bundle.getString(KEY_VIDEO_ID);
        } else if (arguments != null && arguments.containsKey(KEY_VIDEO_ID)) {
            mVideoId = arguments.getString(KEY_VIDEO_ID);
        }

        initialize(PlayerConfig.YOUTUBE_API_KEY, this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, boolean restored) {

        if (mVideoId != null) {
            youTubePlayer.loadVideo(mVideoId);
            youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                @Override
                public void onLoading() {
                    playback.onPlayerInitialize(youTubePlayer);
                }

                @Override
                public void onLoaded(String s) {

                }

                @Override
                public void onAdStarted() {

                }

                @Override
                public void onVideoStarted() {

                }

                @Override
                public void onVideoEnded() {
                    playback.onPlayNextVideo();
                }

                @Override
                public void onError(YouTubePlayer.ErrorReason errorReason) {

                }
            });

            youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                @Override
                public void onFullscreen(boolean b) {
                    playback.onFullScreen();
                }
            });
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(getActivity(), RECOVERY_DIALOG_REQUEST).show();
        } else {
            //Handle the failure
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString(KEY_VIDEO_ID, mVideoId);
    }
}