package com.example.barretina_mobil.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.barretina_mobil.Models.Table;
import com.example.barretina_mobil.R;

import java.util.List;

public class TableAdapter extends ArrayAdapter<Table> {
    private final Context context;
    private final List<Table> tables;

    public TableAdapter(Context context, List<Table> tables) {
        super(context, R.layout.table_item, tables);
        this.context = context;
        this.tables = tables;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.table_item, parent, false);
            
            holder = new ViewHolder();
            holder.numberTextView = convertView.findViewById(R.id.tableNumber);
            holder.waiterTextView = convertView.findViewById(R.id.waiterName);
            holder.statusTextView = convertView.findViewById(R.id.tableStatus);
            holder.editCommandButton = convertView.findViewById(R.id.editCommandButton);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Table table = tables.get(position);
        
        holder.numberTextView.setText(String.format("Table %d", table.getNumber()));
        holder.waiterTextView.setText(table.getWaiterName());
        
        // Set status text and color based on table state
        String status;
        int colorRes;
        
        if (table.isFree()) {
            status = "Free";
            colorRes = R.color.green;
            holder.editCommandButton.setText("New");
        } else if (table.isPaid()) {
            status = "Paid";
            colorRes = R.color.blue;
        } else {
            status = "Occupied";
            colorRes = R.color.red;
        }
        
        holder.statusTextView.setText(status);
        holder.statusTextView.setTextColor(ContextCompat.getColor(context, colorRes));

        holder.editCommandButton.setOnClickListener(v -> {
            // TODO: Future Specs 
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView numberTextView;
        TextView waiterTextView;
        TextView statusTextView;
        Button editCommandButton;
    }
} 