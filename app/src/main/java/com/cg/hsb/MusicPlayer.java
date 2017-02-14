
package com.cg.hsb;


import android.media.MediaPlayer;

public class MusicPlayer {

    private String mFile;
    MediaPlayer mPlayer;

    public MusicPlayer(String file) {
        mFile = new String(file);
        mPlayer = new MediaPlayer();

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                mPlayer.release();
            }
        });

        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer arg0) {

            }
        });

        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer player, int arg1, int arg2) {
                mPlayer.release();
                return false;
            }
        });

        new Thread()
        {
            @Override
            public void run()
            {
                Play();
            }
        }.start();
    }

    private void Play() {
        if (mPlayer.isPlaying()) {
            mPlayer.reset();
        }

        try {
            mPlayer.setDataSource(mFile);
            mPlayer.prepare();
            mPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}