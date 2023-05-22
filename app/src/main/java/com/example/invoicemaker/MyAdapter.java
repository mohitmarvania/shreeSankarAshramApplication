package com.example.invoicemaker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.Locale;

import io.reactivex.annotations.NonNull;
import io.realm.Realm;
import io.realm.RealmResults;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    RealmResults<Data> dataList;

    public MyAdapter(Context context, RealmResults<Data> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Data data = dataList.get(position);
        holder.nameOutput.setText(data.getName().toUpperCase(Locale.ROOT));
        holder.donationOutput.setText("Rs." + data.getDonation());

        String formatedTime = DateFormat.getDateTimeInstance().format(data.createdTime);
        holder.creationOutput.setText(formatedTime);

        // UPDATING DATA FOR A USER.
        holder.itemView.setOnClickListener((v)->{

            Intent intent = new Intent(context, AddUserActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


            intent.putExtra("name", data.name);
            intent.putExtra("smarnarthe", data.smartheName);
            intent.putExtra("address", data.address);
            intent.putExtra("mobileNo", data.mobileNo);
            intent.putExtra("bhet", data.donation);


            String dataId = Integer.toString(holder.itemView.getId());
            intent.putExtra("dataId", dataId);

            context.startActivity(intent);
        });

        // DELETING A USER
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                PopupMenu menu = new PopupMenu(context, view);
                menu.getMenu().add("DELETE");
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getTitle().equals("DELETE")) {
                            //Delete the node.
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            data.deleteFromRealm();
                            realm.commitTransaction();
                            Toast.makeText(context, "Data Deleted !!", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });

                menu.show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nameOutput, donationOutput, creationOutput;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nameOutput = itemView.findViewById(R.id.nameOutput);
            donationOutput = itemView.findViewById(R.id.bhetOutput);
            creationOutput = itemView.findViewById(R.id.creationOutput);


        }
    }

}
