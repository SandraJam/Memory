package com.example.sandra.apprendogles;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by sandra on 12/04/15.
 */
public class MyGLSurfaceView extends GLSurfaceView {
    private MyGLRenderer mRenderer;
    private DisplayMetrics metrics;

    public MyGLSurfaceView(Context context, DisplayMetrics met) {
        super(context);
        metrics = met;
        setEGLContextClientVersion(2);
        mRenderer = new MyGLRenderer(metrics);
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:
                mRenderer.changeToBlack(y, x);
                break;
        }
        requestRender();
        return true;
    }
}
