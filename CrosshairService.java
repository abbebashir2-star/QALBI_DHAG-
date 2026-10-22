package com.qalbi.pubg;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

public class CrosshairService extends Service {
    public static final String ACTION_TOGGLE = "com.qalbi.pubg.TOGGLE";
    private WindowManager windowManager;
    private TextView crosshair;

    @Override public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_TOGGLE.equals(intent.getAction())) {
            if (crosshair != null) removeCrosshair();
            else addCrosshair();
        }
        return START_NOT_STICKY;
    }

    private void addCrosshair() {
        if (!Settings.canDrawOverlays(this)) { stopSelf(); return; }
        crosshair = new TextView(this);
        crosshair.setText("＋");
        crosshair.setTextColor(Color.rgb(50, 255, 125));
        crosshair.setTextSize(38);
        crosshair.setShadowLayer(3, 0, 0, Color.BLACK);
        crosshair.setGravity(Gravity.CENTER);
        int type = android.os.Build.VERSION.SDK_INT >= 26
            ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            : WindowManager.LayoutParams.TYPE_PHONE;
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.CENTER;
        try { windowManager.addView(crosshair, params); }
        catch (Exception e) { crosshair = null; stopSelf(); }
    }

    private void removeCrosshair() {
        try { if (crosshair != null) windowManager.removeView(crosshair); }
        catch (Exception ignored) {}
        crosshair = null;
        stopSelf();
    }

    @Override public void onDestroy() {
        if (crosshair != null) {
            try { windowManager.removeView(crosshair); } catch (Exception ignored) {}
            crosshair = null;
        }
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}
