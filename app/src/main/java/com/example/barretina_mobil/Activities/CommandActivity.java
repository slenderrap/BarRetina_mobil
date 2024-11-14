package com.example.barretina_mobil.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.barretina_mobil.R;

public class CommandActivity extends AppCompatActivity {

    Button tagsButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);
        tagsButton = findViewById(R.id.Tags);
        tagsButton.setOnClickListener(v -> {
            Intent intent = new Intent(CommandActivity.this, TagsActivity.class);
            startActivity(intent);
        });
    }
}
