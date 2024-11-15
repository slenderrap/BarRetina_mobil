package com.example.barretina_mobil.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.barretina_mobil.Adapters.CommandProductAddater;
import com.example.barretina_mobil.Models.CommandProduct;
import com.example.barretina_mobil.Models.ProductInfo;

import java.util.ArrayList;
import android.util.Log;

import android.widget.ListView;
import android.widget.TextView;

import com.example.barretina_mobil.R;

public class CommandActivity extends AppCompatActivity {

    private Button tagsButton;
    private Button addButton;
    private TextView totalTextView;
    private static ArrayList<CommandProduct> products;
    private CommandProductAddater adapter;
    private ListView listView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);
        tagsButton = findViewById(R.id.Tags);
        tagsButton.setOnClickListener(v -> {
            Intent intent = new Intent(CommandActivity.this, TagsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(CommandActivity.this, TagsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        totalTextView = findViewById(R.id.totalTextView);
        listView = findViewById(R.id.commandList);
        if (products == null) {
            products = new ArrayList<CommandProduct>();
            CalculateTotal();
        }
        adapter = new CommandProductAddater(this,products);
        listView.setAdapter(adapter);

        ProductInfo product = (ProductInfo) getIntent().getSerializableExtra("product");
        if (product != null) {
            HandleOrderedItem(product);
        }
        else {
            Log.d("HandleOrderedItem", "product is null");
        }

    }

    private void HandleOrderedItem(ProductInfo product) {
        Log.d("HandleOrderedItem", "product: " + product.getName());
        if (products.stream().filter(item -> item.getName().equals(product.getName())).findFirst().isPresent()) {
            Log.d("HandleOrderedItem", "product found, increasing quantity");
            CommandProduct commandProduct = products.stream().filter(item -> item.getName().equals(product.getName())).findFirst().get();
            commandProduct.setQuantity(commandProduct.getQuantity() + 1);
        } else {
            Log.d("HandleOrderedItem", "product not found, adding new product");
            products.add(new CommandProduct(product, 1));
        }
        adapter.notifyDataSetChanged();
        CalculateTotal();
    }

    private void CalculateTotal() {
        double total = 0;
        for (CommandProduct product : products) {
            total += product.getQuantity() * product.getunitPrice();
        }
        totalTextView.setText("Total: " + String.format("%.2f", total) + " €");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }
}
