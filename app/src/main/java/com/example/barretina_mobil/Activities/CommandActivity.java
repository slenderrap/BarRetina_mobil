package com.example.barretina_mobil.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.barretina_mobil.Adapters.CommandProductAddater;
import com.example.barretina_mobil.Models.CommandProduct;
import com.example.barretina_mobil.Models.ProductInfo;

import java.util.ArrayList;
import android.util.Log;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barretina_mobil.R;
import com.example.barretina_mobil.Utils.UtilsWS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CommandActivity extends AppCompatActivity {

    private static final int MAX_TABLE_NUMBER = 20;

    private UtilsWS ws;
    private Button tagsButton;
    private Button tableButton;
    private Button configButton;
    private Button addButton;
    private Button newCommandButton;
    private Button sendButton;
    private Button tableSubButton;
    private Button tableAddButton;
    private TextView tableNumText;

    private TextView totalTextView;
    private static ArrayList<CommandProduct> products;
    private static int tableNumber = 1;
    private CommandProductAddater adapter;
    private ListView listView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);
        ws = UtilsWS.getSharedInstance();
        tagsButton = findViewById(R.id.Tags);
        tagsButton.setOnClickListener(v -> {
            Intent intent = new Intent(CommandActivity.this, TagsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        tableButton = findViewById(R.id.tables);
        tableButton.setOnClickListener(v -> {
            Intent intent = new Intent(CommandActivity.this, TableActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        configButton = findViewById(R.id.config);
        configButton.setOnClickListener(v -> {
            Intent intent = new Intent(CommandActivity.this, MainActivity.class);
            intent.putExtra("modify", true);
            startActivity(intent);
        });
        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(CommandActivity.this, TagsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        newCommandButton = findViewById(R.id.newCommandButton);
        newCommandButton.setOnClickListener(v -> {
            newComandDialog();
        });

        tableNumText = findViewById(R.id.tableNum);
        tableNumText.setText(String.valueOf(tableNumber));
        tableSubButton = findViewById(R.id.tableSub);
        tableSubButton.setOnClickListener(v -> {
            if (tableNumber > 1) {
                tableNumber--;
                tableNumText.setText(String.valueOf(tableNumber));
            }
        });
        tableAddButton = findViewById(R.id.tableAdd);
        tableAddButton.setOnClickListener(v -> {
            if (tableNumber < MAX_TABLE_NUMBER) {
                tableNumber++;
                tableNumText.setText(String.valueOf(tableNumber));
            }
        });
        
        sendButton = findViewById(R.id.sendButton);
        
        sendButton.setOnClickListener(v -> {
            ws.setOnMessage(this::OnSetCommandResponse);
            try {
                JSONObject json = new JSONObject();
                json.put("type", "setCommand");
                json.put("tableNumber", tableNumber);
                JSONArray productsJson = new JSONArray();
                for (CommandProduct product : products) {
                    productsJson.put(product.toJson());
                }
                json.put("products", productsJson);
                ws.safeSend(json.toString());
            } catch (Exception e) {
                Log.e("CommandActivity", "Error sending command", e);
            }
        });
        totalTextView = findViewById(R.id.totalTextView);
        listView = findViewById(R.id.commandList);
        if (products == null) {
            products = new ArrayList<CommandProduct>();
        }
        adapter = new CommandProductAddater(this,products);
        adapter.setOnProductAdded(() -> {CalculateTotal(); sendButton.setEnabled(true);});
        adapter.setOnProductRemoved(() -> {
            CalculateTotal(); 
            if (products.size() == 0) {
                sendButton.setEnabled(false);
                sendButton.setBackgroundColor(getResources().getColor(R.color.red));
            }
        });
        listView.setAdapter(adapter);
        
        ProductInfo product = (ProductInfo) getIntent().getSerializableExtra("product");
        if (product != null) {
            HandleOrderedItem(product);
        }
        else {
            Log.d("HandleOrderedItem", "product is null");
        }
        /* Future Specs
        Command command = (Command) getIntent().getSerializableExtra("command");
        if (command != null) {
            HandleCommand(command);
        }
        */
        CalculateTotal();
        if (products.size() == 0) {
            sendButton.setEnabled(false);
            sendButton.setBackgroundColor(getResources().getColor(R.color.red));
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
    }

    private void CalculateTotal() {
        double total = 0;
        for (CommandProduct product : products) {
            total += product.getQuantity() * product.getunitPrice();
        }
        totalTextView.setText("Total: " + String.format("%.2f", total) + " €");
    }

    public void OnSetCommandResponse(String response) {
        Log.d("OnSetCommandResponse", "response: " + response);
        try {
            JSONObject json = new JSONObject(response);
            if (json.getString("type").equals("ack")) {
                String type = json.getString("responseType");
                Log.d("OnSetCommandResponse", "responseType: " + type);
                if (type.equals("setCommand")) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Command sent successfully", Toast.LENGTH_SHORT).show();
                        sendButton.setText("Actualitzar");
                    });
                }
            } else if (json.getString("type").equals("error")) {
                runOnUiThread(() -> {
                    try {
                        Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void newComandDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("En quina taula vols posar la comanda?");
    
        // Create an EditText and set it to numeric input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER); // Restrict to numbers
        builder.setView(input);
    
        builder.setPositiveButton("OK", null); // Placeholder, set later
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    
        AlertDialog dialog = builder.create();
    
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                // Get the positive button and set a custom click listener
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userInput = input.getText().toString();
                        int tableNumber = 0;
                        try {
                            tableNumber = Integer.parseInt(userInput);
                        } catch (Exception e) {
                            Toast.makeText(CommandActivity.this, "Invalid table number", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // Validate input
                        if (tableNumber > MAX_TABLE_NUMBER || tableNumber <= 0) {
                            Toast.makeText(CommandActivity.this, "Invalid table number", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            // Dismiss dialog and proceed
                            CommandActivity.tableNumber = tableNumber;
                            tableNumText.setText(String.valueOf(tableNumber));
                            products.clear();
                            adapter.notifyDataSetChanged();
                            CalculateTotal();
                            sendButton.setEnabled(false);
                            sendButton.setBackgroundColor(getResources().getColor(R.color.red));
                            try {
                                JSONObject json = new JSONObject();
                                json.put("type", "newCommand");
                                json.put("tableNumber", tableNumber);
                                ws.safeSend(json.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
    
        dialog.show();
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }
}
