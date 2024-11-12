package com.example.barretina_mobil.Activities;

import android.os.Bundle;
import java.net.URI;
import java.net.URISyntaxException;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;
import com.example.barretina_mobil.Activities.CommandActivity;
import com.example.barretina_mobil.Utils.Config;
import com.example.barretina_mobil.Utils.UtilsConfig;
import com.google.android.material.textfield.TextInputLayout;
import com.example.barretina_mobil.R;

public class MainActivity extends AppCompatActivity {

    TextInputLayout name;
    TextInputLayout urlServer;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //UtilsConfig.deleteConfig(this);
        if (UtilsConfig.configExists(this)) {
            Config config = UtilsConfig.getConfig(this);
            //go to CommandActivity
            Intent intent = new Intent(this, CommandActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        name = findViewById(R.id.name);
        urlServer = findViewById(R.id.urlServer);
        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveConfig());
    }

    private void saveConfig() {
        String name = this.name.getEditText().getText().toString();
        String inputUrl = this.urlServer.getEditText().getText().toString();

        URI serverUrl = null;
        if (!inputUrl.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*")) {
            Toast.makeText(this, "Invalid server URL", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            serverUrl = new URI(inputUrl);
        } catch (URISyntaxException e) {
            Toast.makeText(this, "Invalid server URL", Toast.LENGTH_SHORT).show();
            return;
        }

        UtilsConfig.saveConfig(this, serverUrl, name);
        Intent intent = new Intent(this, CommandActivity.class);
        startActivity(intent);
        finish();
    }
}
