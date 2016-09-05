package com.mdsft.particles.services;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.mdsft.particles.ParticlesRenderer;

/**
 * Created by spidergears on 05/09/16.
 */
public class WallpaperService extends android.service.wallpaper.WallpaperService {

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
        private final Handler wallpaperThreadHandler = new Handler();
        private WallpaperGLSurfaceView wallpaperGLSurfaceView;
        private ParticlesRenderer particlesRenderer;
        private boolean rendererSet;

        WallpaperEngine(){
            wallpaperGLSurfaceView = new WallpaperGLSurfaceView(WallpaperService.this);
            particlesRenderer = new ParticlesRenderer(WallpaperService.this);
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

        protected void setRenderer(GLSurfaceView.Renderer renderer) {
            wallpaperGLSurfaceView.setRenderer(renderer);
            rendererSet = true;
        }

        protected void setEGLContextClientVersion(int version) {
            wallpaperGLSurfaceView.setEGLContextClientVersion(version);
        }

        protected void setPreserveEGLContextOnPause(boolean preserve) {
            wallpaperGLSurfaceView.setPreserveEGLContextOnPause(preserve);
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
