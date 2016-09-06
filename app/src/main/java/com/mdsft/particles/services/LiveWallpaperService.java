package com.mdsft.particles.services;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;


import com.mdsft.particles.ParticlesRenderer;

public class LiveWallpaperService extends WallpaperService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Engine onCreateEngine() {
        return new WallpaperEngine();
    }

    class WallpaperEngine extends Engine{
        private WallpaperGLSurfaceView wallpaperGLSurfaceView;
        private ParticlesRenderer particlesRenderer;
        private boolean rendererSet;
        private float previousX, previousY;

        WallpaperEngine(){
            wallpaperGLSurfaceView = new WallpaperGLSurfaceView(LiveWallpaperService.this);
            particlesRenderer = new ParticlesRenderer(LiveWallpaperService.this);
            setTouchEventsEnabled(true);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (rendererSet)
                if (visible)
                    wallpaperGLSurfaceView.onResume();
                else
                    wallpaperGLSurfaceView.onPause();
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            if (event != null) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    previousX = event.getX();
                    previousY = event.getY();
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    final float deltaX = event.getX() - previousX;
                    final float deltaY = event.getY() - previousY;

                    previousX = event.getX();
                    previousY = event.getY();

                    wallpaperGLSurfaceView.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            particlesRenderer.handleTouchDrag(deltaX, deltaY);
                        }
                    });

                }
            }
            super.onTouchEvent(event);
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
            final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

            if (supportsEs2) {
                // Request an OpenGL ES 2.0 compatible context.
                wallpaperGLSurfaceView.setEGLContextClientVersion(2);
                // Assign the renderer.
                wallpaperGLSurfaceView.setRenderer(particlesRenderer);
                rendererSet = true;
            }
            else {
                Log.e("Wallpaper", "onCreate:  Does not support Es2");
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            wallpaperGLSurfaceView.onDestroy();
        }

        class WallpaperGLSurfaceView extends GLSurfaceView {
            private static final String TAG = "WallpaperGLSurfaceView";

            WallpaperGLSurfaceView(Context context) {
                super(context);
            }

            @Override
            public SurfaceHolder getHolder() {
                return getSurfaceHolder();
            }

            public void onDestroy() {
                super.onDetachedFromWindow();
            }
        }
    }


}
