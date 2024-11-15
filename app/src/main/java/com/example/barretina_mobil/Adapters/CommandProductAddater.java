package com.example.barretina_mobil.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.barretina_mobil.Models.CommandProduct;
import com.example.barretina_mobil.R;

import java.util.List;

public class CommandProductAddater extends ArrayAdapter<CommandProduct> {
    private Context context;
    private List<CommandProduct> products;

    public CommandProductAddater(Context context, List<CommandProduct> products) {
        super(context, 0, products);
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.command_product_item, parent, false);
        }

        CommandProduct currentProduct = products.get(position);

        TextView nameTextView = listItem.findViewById(R.id.productNameTextView);
        TextView quantityTextView = listItem.findViewById(R.id.quantityTextView);
        TextView totalPriceTextView = listItem.findViewById(R.id.totalPriceTextView);
        Button decreaseButton = listItem.findViewById(R.id.decreaseButton);
        Button increaseButton = listItem.findViewById(R.id.increaseButton);

        nameTextView.setText(currentProduct.getName());
        quantityTextView.setText(String.valueOf(currentProduct.getQuantity()));
        double totalPrice = currentProduct.getunitPrice() * currentProduct.getQuantity();
        totalPriceTextView.setText(String.format("%.2f€", totalPrice));

        decreaseButton.setOnClickListener(v -> {
            if (currentProduct.getQuantity() > 1) {
                currentProduct.setQuantity(currentProduct.getQuantity() - 1);
                notifyDataSetChanged();
            }
            else{
                products.remove(currentProduct);
                notifyDataSetChanged();
            }
        });

        increaseButton.setOnClickListener(v -> {
            currentProduct.setQuantity(currentProduct.getQuantity() + 1);
            notifyDataSetChanged();
        });

        return listItem;
    }
}
