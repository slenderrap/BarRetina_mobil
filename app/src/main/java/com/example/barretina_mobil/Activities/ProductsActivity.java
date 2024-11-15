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
import com.example.barretina_mobil.Activities.Adapters.ProductAddapter;
import com.example.barretina_mobil.Models.Product;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductsActivity extends AppCompatActivity {
    private UtilsWS ws;
    private ListView productsList;
    private String tag;
    private Button backButton;
    private List<Product> products;
    private ProductAddapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        
        productsList = findViewById(R.id.productsList);
        tag = getIntent().getStringExtra("tag");
        
        // Initialize products list and adapter
        products = new ArrayList<>();
        adapter = new ProductAddapter(this, products);
        productsList.setAdapter(adapter);
        
        setTitle("Products - " + tag);
        
        ws = UtilsWS.getSharedInstance();
        ws.setOnMessage(this::onMessage);
        
        requestProducts();
        
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProductsActivity.this, TagsActivity.class);
            startActivity(intent);
        });
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
        Log.d("ProductsActivity", "displayProducts: " + json.toString());
        try {
            JSONArray productsArray = json.getJSONArray("products");
            products.clear();

            for (int i = 0; i < productsArray.length(); i++) {
                JSONObject productJson = productsArray.getJSONObject(i);
                
                String name = productJson.getString("name");
                double price = productJson.getDouble("price");
                
                // Filter products by tag
                boolean hasTag = false;
                JSONArray tagsArray = productJson.getJSONArray("tags");
                for (int j = 0; j < tagsArray.length(); j++) {
                    if (tagsArray.getString(j).equals(tag)) {
                        hasTag = true;
                        break;
                    }
                }
                
                if (hasTag) {
                    Log.d("ProductsActivity", "Adding product: " + name + " - " + price);
                    products.add(new Product(name, price));
                }
            }
            
            runOnUiThread(() -> adapter.notifyDataSetChanged());
            
        } catch (JSONException e) {
            Log.e("ProductsActivity", "Error displaying products: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, TagsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}