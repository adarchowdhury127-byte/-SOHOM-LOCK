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

        if (current.equals(getPackageName())) {
            lastPackage = current;
            return;
        }

        if (!current.equals(lastPackage)) {
            lastPackage = current;

            SharedPreferences prefs =
                getSharedPreferences("SOHOM_LOCK", MODE_PRIVATE);

            Set<String> locked = prefs.getStringSet(
                "locked_apps", new HashSet<>()
            );

            if (locked.contains(current)) {
                String unlocked = prefs.getString("unlocked_target", "");

                if (!current.equals(unlocked)) {
                    Intent intent = new Intent(this, LockActivity.class);
                    intent.putExtra("target_package", current);
                    intent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                    );
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    public void onInterrupt() {
    }
}
