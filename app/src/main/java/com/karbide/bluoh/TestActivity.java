package com.karbide.bluoh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class TestActivity extends AppCompatActivity {

    private static final String TEST_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        JCVideoPlayerStandard jcVideoPlayerStandard = (JCVideoPlayerStandard) findViewById(R.id.player);
        jcVideoPlayerStandard.setUp("http://dl.enjoypur.vc/upload_file/367/368/7484/PagalWorld%20-%20Bollywood%20HD%20Video%20Songs%202016/Aap%20Se%20Mausiiquii%20(2016)%20HD%20Video%20Songs/Menu%20Kehn%20De%20-%20Himesh%20Reshammiya%20%28HD%20720p%29.mp4"
                , JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "Test");
//        jcVideoPlayerStandard.thumbImageView.setThumbInCustomProject("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640");

    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}