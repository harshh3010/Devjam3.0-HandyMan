package com.devjam.handyman.Ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devjam.handyman.BookingActivity;
import com.devjam.handyman.Model.Service;
import com.devjam.handyman.R;

import java.util.ArrayList;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolderClass> {

    private ArrayList<Service> myArr;
    private Context context;

    public ServiceAdapter(ArrayList<Service> myArr,String city) {
        this.myArr = myArr;
    }

    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the item view to be displayed in recyclerview
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_service_details,parent,false);
        context = parent.getContext();
        return new ViewHolderClass(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderClass holder, int position) {

        // Displaying the necessary data in their respective fields
        holder.name_txt.setText(myArr.get(position).getName());
        if(Double.parseDouble(myArr.get(position).getCost()) != 0){
            holder.price_txt.setText("Rs " + myArr.get(position).getCost());
        }else{
            holder.price_txt.setText("Price not fixed");
        }
    }

    // Method to get the size of services array list
    @Override
    public int getItemCount() {
        return myArr.size();
    }


    public class ViewHolderClass extends RecyclerView.ViewHolder {
        public TextView name_txt,price_txt;
        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            // Initializing the required fields
            name_txt = itemView.findViewById(R.id.item_service_name_text);
            price_txt = itemView.findViewById(R.id.item_service_price_text);

            // On click listener for item view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Passing the service object to Booking Activity
                    Intent intent = new Intent(context, BookingActivity.class);
                    intent.putExtra("service",myArr.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }
    }

}
