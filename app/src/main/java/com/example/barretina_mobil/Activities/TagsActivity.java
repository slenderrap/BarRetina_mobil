package com.example.barretina_mobil.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.barretina_mobil.R;
import com.example.barretina_mobil.Utils.UtilsWS;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import android.widget.ArrayAdapter;

public class TagsActivity extends AppCompatActivity {

    private UtilsWS ws;
    private ArrayList<String> tags;
    private ListView taglist;
    private Button backButton;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);
        taglist = findViewById(R.id.taglist);
        tags = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tags);
        taglist.setAdapter(adapter);
        ws = UtilsWS.getSharedInstance();
        ws.setOnMessage(this::onMessage);
        JSONObject request = new JSONObject();
        try {
            request.put("type", "getTags");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        ws.safeSend(request.toString());
        backButton = findViewById(R.id.commandList);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(TagsActivity.this, CommandActivity.class);
            startActivity(intent);
        });

        taglist.setOnItemClickListener((parent, view, position, id) -> {
            String tag = tags.get(position);
            Intent intent = new Intent(TagsActivity.this, ProductsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("tag", tag);
            startActivity(intent);
        });
    }

    private void onMessage(String message) {
        JSONObject json = null;
        try {
            json = new JSONObject(message);
            String type = json.getString("type");
            switch (type) {
                case "ack":
                    String responseType = json.getString("responseType");
                    if (responseType.equals("getTags")) {
                        final JSONObject finanjson = json;
                        runOnUiThread(() -> onGetTagsMessage(finanjson));
                    }
                    break;
                case "error":
                    Toast.makeText(this, "Error: " + json.getString("message"), Toast.LENGTH_SHORT).show();
                    break;
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private void onGetTagsMessage(JSONObject json) {
        JSONArray tagsJson;
        Log.d("onGetTagsMessage", "json: " + json.toString());
    
        try {
            tagsJson = json.getJSONArray("tags");
            this.tags.clear(); // Clear existing data instead of creating a new list
    
            for (int i = 0; i < tagsJson.length(); i++) {
                String tag = tagsJson.getString(i);
                Log.d("onGetTagsMessage", "added tag: " + tag);
                this.tags.add(tag); // Add each tag to the existing list
            }
    
            // Notify the adapter once after the loop
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Log.d("onGetTagsMessage", "error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, CommandActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
