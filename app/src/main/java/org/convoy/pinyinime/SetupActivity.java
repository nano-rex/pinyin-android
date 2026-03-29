package org.convoy.pinyinime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

public class SetupActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar);
        setContentView(R.layout.activity_setup);

        Button openSettings = findViewById(R.id.open_input_settings);
        Button openPicker = findViewById(R.id.open_input_picker);

        openSettings.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)));
        openPicker.setOnClickListener(this::showPicker);
    }

    private void showPicker(View ignored) {
        Object service = getSystemService(INPUT_METHOD_SERVICE);
        if (service instanceof InputMethodManager) {
            ((InputMethodManager) service).showInputMethodPicker();
        }
    }
}
