package com.example.barretina_mobil.Activities.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.barretina_mobil.Models.Product;
import com.example.barretina_mobil.R;
import com.example.barretina_mobil.Activities.CommandActivity;

import java.util.List;

public class ProductAddapter extends ArrayAdapter<Product> {
    private Context context;
    private List<Product> products;

    public ProductAddapter(Context context, List<Product> products) {
        super(context, R.layout.product_list_item, products);
        this.context = context;
        this.products = products;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.product_list_item, parent, false);

        TextView nameTextView = rowView.findViewById(R.id.productNameTextView);
        TextView priceTextView = rowView.findViewById(R.id.productPriceTextView);
        Button orderButton = rowView.findViewById(R.id.orderButton);

        Product product = products.get(position);

        nameTextView.setText(product.getName());
        priceTextView.setText(String.format("%.2f€", product.getPrice()));

        orderButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommandActivity.class);
            intent.putExtra("productName", product.getName());
            context.startActivity(intent);
        });

        return rowView;
    }
}
