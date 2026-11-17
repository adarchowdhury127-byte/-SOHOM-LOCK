package com.sohom.lock;

import android.app.*;
import android.os.*;
import android.content.*;
import android.provider.Settings;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import java.util.*;

public class MainActivity extends Activity {

    LinearLayout root;
    EditText pinInput;
    LinearLayout appList;
    SharedPreferences prefs;
    PackageManager pm;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        prefs = getSharedPreferences("SOHOM_LOCK", MODE_PRIVATE);
        pm = getPackageManager();

        root = new LinearLayout(this);
        root.setOrientation(1);
        root.setPadding(24, 30, 24, 20);
        root.setBackgroundColor(Color.rgb(8,20,38));

        ScrollView scroll = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(1);
        scroll.addView(content);

        TextView title = new TextView(this);
        title.setText("SOHOM LOCK");
        title.setTextColor(Color.WHITE);
        title.setTextSize(28);
        title.setGravity(Gravity.CENTER);
        content.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Protect your personal apps");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0,8,0,25);
        content.addView(subtitle);

        pinInput = new EditText(this);
        pinInput.setHint("Set your PIN (4 digits or more)");
        pinInput.setHintTextColor(Color.GRAY);
        pinInput.setTextColor(Color.WHITE);
        pinInput.setInputType(2);
        pinInput.setSingleLine(true);
        pinInput.setText(prefs.getString("pin", ""));
        content.addView(pinInput);

        Button savePin = new Button(this);
        savePin.setText("SAVE PIN");
        content.addView(savePin);

        savePin.setOnClickListener(v -> {
            String pin = pinInput.getText().toString();
            if (pin.length() < 4) {
                Toast.makeText(this, "PIN must be at least 4 digits", Toast.LENGTH_SHORT).show();
            } else {
                prefs.edit().putString("pin", pin).apply();
                Toast.makeText(this, "PIN saved", Toast.LENGTH_SHORT).show();
            }
        });

        Button accessibility = new Button(this);
        accessibility.setText("ENABLE LOCK PROTECTION");
        content.addView(accessibility);

        accessibility.setOnClickListener(v ->
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        );

        TextView heading = new TextView(this);
        heading.setText("SELECT APPS TO LOCK");
        heading.setTextColor(Color.rgb(85,184,255));
        heading.setTextSize(18);
        heading.setPadding(0,25,0,12);
        content.addView(heading);

        appList = new LinearLayout(this);
        appList.setOrientation(1);
        content.addView(appList);

        root.addView(scroll);
        setContentView(root);

        loadApps();
    }

    void loadApps() {
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        Collections.sort(apps, (a,b) ->
            pm.getApplicationLabel(a).toString().compareToIgnoreCase(
                pm.getApplicationLabel(b).toString()
            )
        );

        for (ApplicationInfo app : apps) {
            if (app.packageName.equals(getPackageName())) continue;
            if (pm.getLaunchIntentForPackage(app.packageName) == null) continue;

            CheckBox cb = new CheckBox(this);
            cb.setText(pm.getApplicationLabel(app).toString());
            cb.setTextColor(Color.WHITE);
            cb.setButtonTintList(android.content.res.ColorStateList.valueOf(
                Color.rgb(85,184,255)
            ));

            Set<String> locked = prefs.getStringSet("locked_apps", new HashSet<>());
            cb.setChecked(locked.contains(app.packageName));

            cb.setOnCheckedChangeListener((button, checked) -> {
                Set<String> updated = new HashSet<>(
                    prefs.getStringSet("locked_apps", new HashSet<>())
                );
                if (checked) updated.add(app.packageName);
                else updated.remove(app.packageName);

                prefs.edit().putStringSet("locked_apps", updated).apply();
            });

            appList.addView(cb);
        }
    }
}
