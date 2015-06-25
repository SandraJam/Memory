package com.example.sandra.apprendogles;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;

/**
 * Created by sandra on 12/04/15.
 */
public class OpenGLES20Activity extends Activity{
    private GLSurfaceView mGLview;
    DisplayMetrics metrics = new DisplayMetrics();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mGLview = new MyGLSurfaceView(this, metrics);
        setContentView(mGLview);
    }


}
