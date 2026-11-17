package com.sohom.lock;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.*;

public class LockActivity extends Activity {

    EditText pinInput;
    String targetPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        targetPackage = getIntent().getStringExtra("target_package");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(30,30,30,30);
        layout.setBackgroundColor(Color.rgb(8,20,38));

        TextView title = new TextView(this);
        title.setText("SOHOM LOCK");
        title.setTextColor(Color.WHITE);
        title.setTextSize(28);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        TextView message = new TextView(this);
        message.setText("Enter your PIN to unlock");
        message.setTextColor(Color.LTGRAY);
        message.setTextSize(16);
        message.setPadding(0,20,0,20);
        layout.addView(message);

        pinInput = new EditText(this);
        pinInput.setHint("Enter PIN");
        pinInput.setHintTextColor(Color.GRAY);
        pinInput.setTextColor(Color.WHITE);
        pinInput.setInputType(2);
        pinInput.setGravity(Gravity.CENTER);
        layout.addView(pinInput);

        Button unlock = new Button(this);
        unlock.setText("UNLOCK");
        layout.addView(unlock);

        unlock.setOnClickListener(v -> checkPin());

        setContentView(layout);
    }

    void checkPin() {
        String saved = getSharedPreferences(
            "SOHOM_LOCK", MODE_PRIVATE
        ).getString("pin", "");

        String entered = pinInput.getText().toString();

        if (saved.isEmpty()) {
            Toast.makeText(this, "Please set a PIN first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (saved.equals(entered)) {
            getSharedPreferences("SOHOM_LOCK", MODE_PRIVATE)
                .edit()
                .putString("unlocked_target", targetPackage)
                .apply();

            finish();
        } else {
            pinInput.setText("");
            Toast.makeText(this, "Wrong PIN!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        // Keep the lock screen visible.
    }
}
