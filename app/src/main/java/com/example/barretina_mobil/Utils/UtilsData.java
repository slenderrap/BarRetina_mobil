package com.example.barretina_mobil.Utils;

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
    private final UtilsWS ws;
    
    // Cache storage
    private ArrayList<String> cachedTags;
    private HashMap<String, List<ProductInfo>> cachedProductsByTag;
    private List<Product> cachedAllProducts;
    private List<Table> cachedTables;
    
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
        cachedTables = null;
    }

    public static UtilsData getInstance() {
        if (instance == null) {
            instance = new UtilsData();
        }
        return instance;
    }

    public void getTags(DataCallback<ArrayList<String>> callback) {
        if (cachedTags != null) {
            callback.onSuccess(cachedTags);
            return;
        }

        ws.setOnMessage(message -> {
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
                callback.onError(e.getMessage());
            }
        });

        try {
            JSONObject request = new JSONObject();
            request.put("type", "getTags");
            ws.safeSend(request.toString());
        } catch (JSONException e) {
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
    
    public void getTables(DataCallback<List<Table>> callback) {
        if (cachedTables != null) {
            callback.onSuccess(cachedTables);
            return;
        }

        ws.setOnMessage(message -> {
            try {
                JSONObject json = new JSONObject(message);
                if (json.getString("type").equals("ack") && 
                    json.getString("responseType").equals("getTables")) {
                    
                    List<Table> tables = new ArrayList<>();
                    JSONArray tablesArray = json.getJSONArray("tables");

                    for (int i = 0; i < tablesArray.length(); i++) {
                        JSONObject tableJson = tablesArray.getJSONObject(i);
                        tables.add(new Table(
                            tableJson.getInt("tableNumber"),
                            tableJson.getString("waiter"),
                            tableJson.getBoolean("occupied"),
                            tableJson.getBoolean("paid")
                        ));
                    }

                    cachedTables = tables;
                    callback.onSuccess(tables);
                } else if (json.getString("type").equals("error")) {
                    callback.onError(json.getString("message"));
                }
            } catch (JSONException e) {
                callback.onError(e.getMessage());
            }
        });

        try {
            JSONObject request = new JSONObject();
            request.put("type", "getTables");
            ws.safeSend(request.toString());
        } catch (JSONException e) {
            callback.onError(e.getMessage());
        }
    }

    

    public void clearCache() {
        cachedTags = null;
        cachedProductsByTag.clear();
        cachedAllProducts = null;
        cachedTables = null;
    }
}
