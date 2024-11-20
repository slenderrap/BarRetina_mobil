package com.example.barretina_mobil.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.barretina_mobil.Adapters.TableAdapter;
import com.example.barretina_mobil.Models.Table;
import com.example.barretina_mobil.R;
import com.example.barretina_mobil.Utils.UtilsData;

import java.util.ArrayList;
import java.util.List;

public class TableActivity extends AppCompatActivity {
    private UtilsData utilsData;
    private ListView tableList;
    private List<Table> tables;
    private TableAdapter adapter;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tables);

        // Initialize views
        tableList = findViewById(R.id.tableList);
        backButton = findViewById(R.id.backButton);

        // Initialize data
        tables = new ArrayList<>();
        adapter = new TableAdapter(this, tables);
        tableList.setAdapter(adapter);
        utilsData = UtilsData.getInstance();

        // Set up click listeners
        backButton.setOnClickListener(v -> {
            utilsData.stopTableUpdateCallback();
            Intent intent = new Intent(TableActivity.this, CommandActivity.class);
            startActivity(intent);
            finish();
        });

        // Load tables
        loadTables();
    }

    private void loadTables() {
        utilsData.setTableUpdateCallback(1000, new UtilsData.DataCallback<List<Table>>() {
            @Override
            public void onSuccess(List<Table> result) {
                Log.d("TableActivity", "onSuccess: " + result.toString());
                runOnUiThread(() -> {
                    tables.clear();
                    tables.addAll(result);
                    tableList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(TableActivity.this, "TableActivity Error: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, CommandActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
} 