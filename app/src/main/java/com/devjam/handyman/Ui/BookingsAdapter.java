package com.devjam.handyman.Ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devjam.handyman.Model.Booking;
import com.devjam.handyman.R;
import com.devjam.handyman.Util.UserApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.ViewHolderClass> {

    private ArrayList<Booking> myArr;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserApi userApi = UserApi.getInstance();
    private Context context;
    private ProgressDialog pd;

    public BookingsAdapter(ArrayList<Booking> myArr) {
        this.myArr = myArr;
    }

    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_bookings,parent,false);
        context = parent.getContext();
        return  new ViewHolderClass(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderClass holder, final int position) {
        holder.name_txt.setText(myArr.get(position).getServiceId());
        holder.date_txt.setText(myArr.get(position).getDate());
        holder.time_txt.setText(myArr.get(position).getTime());
        holder.price_txt.setText(myArr.get(position).getPrice());

        holder.cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(context,R.style.AppCompatAlertDialogStyle);
                pd.setMessage("Please wait...");
                pd.show();

                db.collection("Users")
                        .document(userApi.getEmail())
                        .collection("Bookings")
                        .document(myArr.get(position).getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                myArr.remove(position);
                                notifyDataSetChanged();
                                notifyItemRangeChanged(position,myArr.size());
                                pd.dismiss();
                                Toast.makeText(context,"Booking cancelled!",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(context,"An error occurred!",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return myArr.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder{

        public TextView name_txt,date_txt,price_txt,time_txt;
        public Button cancel_btn;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            name_txt = itemView.findViewById(R.id.item_booking_name_text);
            date_txt = itemView.findViewById(R.id.item_booking_date_text);
            time_txt = itemView.findViewById(R.id.item_booking_time_text);
            price_txt = itemView.findViewById(R.id.item_booking_price_text);
            cancel_btn = itemView.findViewById(R.id.item_booking_cancel_button);
        }
    }
}
