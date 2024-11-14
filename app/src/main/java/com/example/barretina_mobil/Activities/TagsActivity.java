package com.example.barretina_mobil.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.barretina_mobil.R;
import com.example.barretina_mobil.Utils.UtilsWS;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;

public class TagsActivity extends AppCompatActivity {

    UtilsWS ws;
    private ArrayList<String> tags;
    private ListView taglist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);
        taglist = findViewById(R.id.taglist);
        ws = UtilsWS.getSharedInstance();
        ws.setOnMessage(this::onMessage);
        JSONObject request = new JSONObject();
        try {
            request.put("type", "getTags");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        ws.safeSend(request.toString());
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
        JSONArray tags = null;
        try {
            tags = json.getJSONArray("tags");
            this.tags = new ArrayList<>();

            for (int i = 0; i < tags.length(); i++) {
                String tag = tags.getString(i);
                this.tags.add(tag);
                Button tagView = new Button(this);
                tagView.setText(tag);
                tagView.setOnClickListener(v -> {
                    Intent intent = new Intent(TagsActivity.this, ProductsActivity.class);
                    intent.putExtra("tag", tag);
                    startActivity(intent);
                });
                taglist.addView(tagView);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
}