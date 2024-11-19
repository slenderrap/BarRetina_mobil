package com.example.barretina_mobil.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.barretina_mobil.R;
import com.example.barretina_mobil.Utils.UtilsData;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import android.widget.ArrayAdapter;

public class TagsActivity extends AppCompatActivity {

    private UtilsData utilsData;
    private ArrayList<String> tags;
    private ListView taglist;
    private Button backButton;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        backButton = findViewById(R.id.commandList);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(TagsActivity.this, CommandActivity.class);
            startActivity(intent);
        });

        taglist = findViewById(R.id.taglist);
        tags = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tags);
        taglist.setAdapter(adapter);
        taglist.setOnItemClickListener((parent, view, position, id) -> {
            String tag = tags.get(position);
            Intent intent = new Intent(TagsActivity.this, ProductsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("tag", tag);
            startActivity(intent);
        });

        GetTags();
    }

    private void GetTags() {
        utilsData = UtilsData.getInstance();
        utilsData.getTags(new UtilsData.DataCallback<ArrayList<String>>() {
            @Override
            public void onSuccess(ArrayList<String> result) {
                runOnUiThread(() -> {
                    tags.clear();
                    tags.addAll(result);
                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(TagsActivity.this, "TagsActivity Error: " + error, Toast.LENGTH_SHORT).show();
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
