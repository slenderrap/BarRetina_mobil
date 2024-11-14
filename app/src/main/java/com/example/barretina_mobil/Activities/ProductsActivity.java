package com.example.barretina_mobil.Activities;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.barretina_mobil.R;
import com.example.barretina_mobil.Utils.UtilsWS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductsActivity extends AppCompatActivity {
    private UtilsWS ws;
    private LinearLayout productsList;
    private String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        
        productsList = findViewById(R.id.productsList);
        tag = getIntent().getStringExtra("tag");
        
        // Set activity title to show current tag
        setTitle("Products - " + tag);
        
        // Initialize WebSocket
        ws = UtilsWS.getSharedInstance();
        ws.setOnMessage(this::onMessage);
        
        // Request products for this tag
        requestProducts();
    }

    private void requestProducts() {
        JSONObject request = new JSONObject();
        try {
            request.put("type", "getProducts");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        ws.safeSend(request.toString());
    }

        private void onMessage(String message) {
        try {
            JSONObject json = new JSONObject(message);
            String type = json.getString("type");
            
            if (type.equals("ack")) {
                String responseType = json.getString("responseType");
                if (responseType.equals("getProducts")) {
                    runOnUiThread(() -> displayProducts(json));
                }
            } else if (type.equals("error")) {
                Toast.makeText(this, "Error: " + json.getString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void displayProducts(JSONObject json) {
        try {
            JSONArray products = json.getJSONArray("products");
            productsList.removeAllViews();

            for (int i = 0; i < products.length(); i++) {
                JSONObject product = products.getJSONObject(i);
                
                String name = product.getString("name");
                double price = product.getDouble("price");
                // Filter products by tag
                boolean hasTag = false;
                JSONArray tagsArray = product.getJSONArray("tags");
                for (int j = 0; j < tagsArray.length(); j++) {
                    if (tagsArray.getString(j).equals(tag)) {
                        hasTag = true;
                        break;
                    }
                }
                if (!hasTag) {
                    continue;
                }
                // Create product view
                TextView productView = new TextView(this);
                productView.setText(String.format("%s - %.2f€", name, price));
                productView.setTextSize(18);
                productView.setPadding(20, 10, 20, 10);

                productsList.addView(productView);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}