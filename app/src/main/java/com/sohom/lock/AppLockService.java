package com.sohom.lock;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.*;

public class AppLockService extends AccessibilityService {

    String lastPackage = "";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null || event.getPackageName() == null) return;

        String current = event.getPackageName().toString();

        if (current.equals(lastPackage)) return;

        lastPackage = current;

        SharedPreferences prefs =
            getSharedPreferences("SOHOM_LOCK", MODE_PRIVATE);

        String unlocked = prefs.getString("unlocked_target", "");

        if (!unlocked.isEmpty() && !current.equals(unlocked)) {
            prefs.edit().remove("unlocked_target").apply();
        }

        if (current.equals(getPackageName())) return;

        Set<String> locked = prefs.getStringSet(
            "locked_apps", new HashSet<>()
        );

        if (locked.contains(current) && !current.equals(unlocked)) {
            Intent intent = new Intent(this, LockActivity.class);
            intent.putExtra("target_package", current);
            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP
            );
            startActivity(intent);
        }
    }

    @Override
    public void onInterrupt() {
    }
}
