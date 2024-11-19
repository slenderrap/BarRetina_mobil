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
import com.example.barretina_mobil.Adapters.ProductInfoAddapter;
import com.example.barretina_mobil.Models.ProductInfo;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductsActivity extends AppCompatActivity {
    private UtilsData utilsData;
    private ListView productsList;
    private String tag;
    private Button backButton;
    private List<ProductInfo> products;
    private ProductInfoAddapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        
        productsList = findViewById(R.id.productsList);
        tag = getIntent().getStringExtra("tag");
        
        // Initialize products list and adapter
        products = new ArrayList<>();
        adapter = new ProductInfoAddapter(this, products);
        productsList.setAdapter(adapter);
        
        setTitle("Products - " + tag);
    
        requestProducts();
        
        backButton = findViewById(R.id.commandList);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProductsActivity.this, TagsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    private void requestProducts() {
        utilsData = UtilsData.getInstance();
        utilsData.getProductsByTag(tag, new UtilsData.DataCallback<List<ProductInfo>>() {
            @Override
            public void onSuccess(List<ProductInfo> result) {
                runOnUiThread(() -> {
                    products.clear();
                    products.addAll(result);
                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ProductsActivity.this, "ProductsActivity Error: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, TagsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}