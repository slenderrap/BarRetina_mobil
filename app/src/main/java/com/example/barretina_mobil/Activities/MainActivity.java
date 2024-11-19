package com.example.barretina_mobil.Activities;

import android.os.Bundle;
import android.util.Log;
import java.net.URI;
import java.net.URISyntaxException;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;

import com.example.barretina_mobil.Utils.Config;
import com.example.barretina_mobil.Utils.UtilsConfig;
import com.example.barretina_mobil.Utils.UtilsWS;
import com.google.android.material.textfield.TextInputLayout;
import com.example.barretina_mobil.R;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout name;
    private TextInputLayout urlServer;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Delete config, only for test
        //UtilsConfig.deleteConfig(this);
        if (UtilsConfig.configExists(this)) {
            Config config = UtilsConfig.getConfig(this);
            Log.d("MainActivity", "Server URL: " + config.getServerUrl().toString());
            UtilsWS.init(config.getServerUrl().toString());
            //go to CommandActivity
            Intent intent = new Intent(this, CommandActivity.class);
            startActivity(intent);
            return;
        }
        setContentView(R.layout.activity_main);
        name = findViewById(R.id.productPriceTextView);
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
        Config config = UtilsConfig.getConfig(this);
        UtilsWS.init(config.getServerUrl().toString());
        Intent intent = new Intent(this, CommandActivity.class);
        startActivity(intent);
    }
}
