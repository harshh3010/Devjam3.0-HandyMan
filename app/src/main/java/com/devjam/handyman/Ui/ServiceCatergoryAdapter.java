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

import com.devjam.handyman.CategoryActivity;
import com.devjam.handyman.R;

import java.util.ArrayList;

public class ServiceCatergoryAdapter extends RecyclerView.Adapter<ServiceCatergoryAdapter.ViewHolderClass>  implements Filterable {

    private ArrayList<String> myArr;
    private ArrayList<String> myArrFull;
    private Context context;
    private String City;

    public ServiceCatergoryAdapter(ArrayList<String> myArr,String city) {
        this.myArr = myArr;
        myArrFull = new ArrayList<>(myArr);
        City = city;
    }

    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the item view to be displayed in recyclerview
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_service_category,parent,false);
        context = parent.getContext();
        return new ViewHolderClass(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderClass holder, int position) {
        // Displaying the necessary data in their respective fields
        holder.name_txt.setText(myArr.get(position));
    }

    // Method to get the size of categories array list
    @Override
    public int getItemCount() {
        return myArr.size() ;
    }

    // Method to filter the contents of the recyclerview on basis of a query text
    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<String> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length()==0){
                filteredList.addAll(myArrFull);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(String str : myArrFull){
                    if(str.toLowerCase().contains(filterPattern)){
                        filteredList.add(str);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            myArr.clear();
            myArr.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolderClass extends RecyclerView.ViewHolder{

        public TextView name_txt;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            // Initializing the required fields
            name_txt = itemView.findViewById(R.id.item_service_category_name_text);

            // On click listener for item view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // passing the City name and Category to CategoryActivity
                    if(!name_txt.getText().toString().isEmpty()){
                        Intent intent = new Intent(context, CategoryActivity.class);
                        intent.putExtra("category",name_txt.getText().toString());
                        intent.putExtra("city",City);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }

}
