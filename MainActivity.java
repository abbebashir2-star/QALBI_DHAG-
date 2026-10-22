package com.qalbi.pubg;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private int green = Color.rgb(52, 235, 139);

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        showHome();
    }

    private TextView text(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value); t.setTextSize(size); t.setTextColor(color);
        t.setGravity(Gravity.CENTER); t.setPadding(12, 10, 12, 10);
        return t;
    }

    private Button button(String label) {
        Button b = new Button(this);
        b.setText(label); b.setTextColor(Color.BLACK);
        b.setAllCaps(false);
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(green));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 8, 0, 8); b.setLayoutParams(p);
        return b;
    }

    private void showHome() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(24, 28, 24, 24);
        root.setBackgroundColor(Color.rgb(9, 14, 12));

        root.addView(text("QALBI", 36, green));
        root.addView(text("PUBG GAMING CENTER", 15, Color.WHITE));
        root.addView(text("Gratis • Ingen konto • Inga köp", 13, Color.LTGRAY));

        Button play = button("BOOST & PLAY  →");
        play.setOnClickListener(v -> launchPubg());
        root.addView(play);

        Button crosshair = button("Aktivera / stäng av Crosshair");
        crosshair.setOnClickListener(v -> toggleCrosshair());
        root.addView(crosshair);

        Button display = button("Skärminställningar");
        display.setOnClickListener(v -> {
            try { startActivity(new Intent(Settings.ACTION_DISPLAY_SETTINGS)); }
            catch (Exception e) { toast("Skärminställningar kunde inte öppnas."); }
        });
        root.addView(display);

        Button dnd = button("Stör ej-inställningar");
        dnd.setOnClickListener(v -> {
            NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            if (nm != null && !nm.isNotificationPolicyAccessGranted()) {
                try { startActivity(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)); }
                catch (Exception e) { toast("Öppna Stör ej manuellt i Inställningar."); }
            } else {
                try { startActivity(new Intent(Settings.ACTION_SOUND_SETTINGS)); }
                catch (Exception e) { toast("Ljudinställningar kunde inte öppnas."); }
            }
        });
        root.addView(dnd);

        root.addView(text("Qalbi använder Androids tillgängliga funktioner. Den kan inte garantera högre FPS eller ändra CPU/GPU-begränsningar.", 12, Color.LTGRAY));
        setContentView(root);
    }

    private void launchPubg() {
        String[] packages = {
            "com.tencent.ig",              // Global
            "com.pubg.imobile",            // India
            "com.tencent.iglite"           // Lite, where available
        };
        for (String pkg : packages) {
            Intent launch = getPackageManager().getLaunchIntentForPackage(pkg);
            if (launch != null) {
                try { startActivity(launch); return; }
                catch (Exception ignored) {}
            }
        }
        toast("PUBG Mobile hittades inte. Installera spelet från den officiella appbutiken.");
    }

    private void toggleCrosshair() {
        if (!Settings.canDrawOverlays(this)) {
            new AlertDialog.Builder(this)
                .setTitle("Tillåt crosshair")
                .setMessage("För att visa siktet ovanpå spelet måste du tillåta Qalbi PUBG att visas över andra appar.")
                .setNegativeButton("Avbryt", (d,w) -> {})
                .setPositiveButton("Öppna inställningar", (d,w) -> {
                    Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                    startActivity(i);
                }).show();
            return;
        }
        Intent i = new Intent(this, CrosshairService.class);
        i.setAction(CrosshairService.ACTION_TOGGLE);
        startService(i);
        toast("Crosshair-kommando skickat.");
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
