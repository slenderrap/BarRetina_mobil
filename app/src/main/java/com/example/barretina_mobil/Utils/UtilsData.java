package com.example.barretina_mobil.Utils;

import android.util.Log;

import com.example.barretina_mobil.Models.ProductInfo;
import com.example.barretina_mobil.Models.CommandProduct;
import com.example.barretina_mobil.Models.Table;
import com.example.barretina_mobil.Models.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UtilsData {
    private static UtilsData instance;
    private UtilsWS ws;
    private Thread tableUpdateCallback;
    // Cache storage
    private ArrayList<String> cachedTags;
    private HashMap<String, List<ProductInfo>> cachedProductsByTag;
    private List<Product> cachedAllProducts;
    
    // Callbacks
    public interface DataCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    private UtilsData() {
        ws = UtilsWS.getSharedInstance();
        cachedTags = null;
        cachedProductsByTag = new HashMap<>();
        cachedAllProducts = null;
    }

    public static UtilsData getInstance() {
        if (instance == null) {
            instance = new UtilsData();
        }
        return instance;
    }

    public void getTags(DataCallback<ArrayList<String>> callback) {
        if (cachedTags != null) {
            Log.d("GetTags", "cachedTags != null");
            callback.onSuccess(cachedTags);
            return;
        }
        Log.d("GetTags", "setOnMessage");
        ws.setOnMessage(message -> {
            Log.d("GetTags", "setOnMessage message: " + message);
            try {
                JSONObject json = new JSONObject(message);
                if (json.getString("type").equals("ack") && 
                    json.getString("responseType").equals("getTags")) {
                    
                    JSONArray tagsJson = json.getJSONArray("tags");
                    ArrayList<String> tags = new ArrayList<>();
                    
                    for (int i = 0; i < tagsJson.length(); i++) {
                        tags.add(tagsJson.getString(i));
                    }
                    
                    cachedTags = tags;
                    callback.onSuccess(tags);
                } else if (json.getString("type").equals("error")) {
                    callback.onError(json.getString("message"));
                }
            } catch (JSONException e) {
                Log.d("GetTags", "setOnMessage JSONException: " + e.getMessage());
                callback.onError(e.getMessage());
            }
        });
        Log.d("GetTags", "sendRequest");
        try {
            JSONObject request = new JSONObject();
            request.put("type", "getTags");
            ws.safeSend(request.toString());
        } catch (JSONException e) {
            Log.d("GetTags", "sendRequest JSONException: " + e.getMessage());
            callback.onError(e.getMessage());
        }
    }
    
    public void getProductsByTag(String tag, DataCallback<List<ProductInfo>> callback) {
        if (cachedProductsByTag.containsKey(tag)) {
            callback.onSuccess(cachedProductsByTag.get(tag));
            return;
        }

        // If we have all products cached, filter them by tag
        if (cachedAllProducts != null) {
            List<ProductInfo> products = new ArrayList<>();
            for (Product product : cachedAllProducts) {
                if (product.getTags().contains(tag)) {
                    products.add(new ProductInfo(product.getId(), product.getName(), product.getPrice()));
                }
            }
            cachedProductsByTag.put(tag, products);
            callback.onSuccess(products);
            return;
        }

        // If we don't have cached products, get all products first
        getAllProducts(new DataCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> allProducts) {
                List<ProductInfo> products = new ArrayList<>();
                for (Product product : allProducts) {
                    if (product.getTags().contains(tag)) {
                        products.add(new ProductInfo(product.getId(), product.getName(), product.getPrice()));
                    }
                }
                cachedProductsByTag.put(tag, products);
                callback.onSuccess(products);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    
    public void getAllProducts(DataCallback<List<Product>> callback) {
        if (cachedAllProducts != null) {
            callback.onSuccess(cachedAllProducts);
            return;
        }

        ws.setOnMessage(message -> {
            try {
                JSONObject json = new JSONObject(message);
                if (json.getString("type").equals("ack") && 
                    json.getString("responseType").equals("getProducts")) {
                    
                    List<Product> products = new ArrayList<>();
                    JSONArray productsArray = json.getJSONArray("products");

                    for (int i = 0; i < productsArray.length(); i++) {
                        JSONObject productJson = productsArray.getJSONObject(i);
                        products.add(new Product(productJson));
                    }

                    cachedAllProducts = products;
                    callback.onSuccess(products);
                } else if (json.getString("type").equals("error")) {
                    callback.onError(json.getString("message"));
                }
            } catch (JSONException e) {
                callback.onError(e.getMessage());
            }
        });

        try {
            JSONObject request = new JSONObject();
            request.put("type", "getProducts");
            ws.safeSend(request.toString());
        } catch (JSONException e) {
            callback.onError(e.getMessage());
        }
    }
    
    public void setTableUpdateCallback(long delay, DataCallback<List<Table>> callback) {
        Log.d("UtilsData", "setTableUpdateCallback: " + delay);
        ws.setOnMessage(message -> {
            Log.d("UtilsData", "getTables: " + message);
            try {
                JSONObject json = new JSONObject(message);
                Log.d("UtilsData", "getTables: " + json.toString());
                if (json.getString("type").equals("ack") && 
                    json.getString("responseType").equals("getTables")) {
                    
                    List<Table> tables = new ArrayList<>();
                    JSONArray tablesArray = json.getJSONArray("tables");

                    for (int i = 0; i < tablesArray.length(); i++) {
                        JSONObject tableJson = tablesArray.getJSONObject(i);
                        int tableNumber = tableJson.getInt("tablenumber");
                        String waiter;
                        try {
                            waiter = tableJson.getString("waiter");
                        } catch (JSONException e) {
                            // waiter was null
                            waiter = "";
                        }
                        boolean occupied = tableJson.getBoolean("occupied");
                        boolean paid = false;
                        try {
                            String state = tableJson.getString("state");
                            if (state.equals("pagat"))
                                paid = true;
                        } catch (JSONException e) {
                            // state was null
                            paid = false;
                        }
                        tables.add(new Table(
                            tableNumber,
                            waiter,
                            occupied,
                            paid
                        ));
                    }
                    callback.onSuccess(tables);
                } else if (json.getString("type").equals("error")) {
                    callback.onError(json.getString("message"));
                }
            } catch (JSONException e) {
                callback.onError(e.getMessage());
            }
        });

        tableUpdateCallback = new Thread(() -> {
            while (true) {
                Log.d("UtilsData", "tableUpdateCallback: running");
                try {
                    JSONObject request = new JSONObject();
                    request.put("type", "getTables");
                    ws.safeSend(request.toString());
                }
                catch (JSONException e) {
                    callback.onError(e.getMessage());
                }
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        });
        tableUpdateCallback.start();
    }

    public void stopTableUpdateCallback() {
        if (tableUpdateCallback != null) {
            tableUpdateCallback.interrupt();
            tableUpdateCallback = null;
        }
    }

    public void clearCache() {
        cachedTags = null;
        cachedProductsByTag.clear();
        cachedAllProducts = null;
        stopTableUpdateCallback();
        ws = UtilsWS.getSharedInstance();
    }
}
