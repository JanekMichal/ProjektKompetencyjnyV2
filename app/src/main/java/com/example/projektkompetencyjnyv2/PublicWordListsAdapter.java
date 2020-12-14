package com.example.projektkompetencyjnyv2;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PublicWordListsAdapter extends RecyclerView.Adapter<PublicWordListsAdapter.ViewHolder> {

    private static final String TAG = "WordListsAdapter";
    private ArrayList<String> listNames;
    private ArrayList<Integer> difficultyLevels;
    private ArrayList<Integer> wordQuantities;
    private ArrayList<String> owners;
    private Context mContext;
    private CurrentUser currentUser;
    private int userId;

    public PublicWordListsAdapter(Context mContext, ArrayList<String> listNames, ArrayList<Integer> difficultyLevels, ArrayList<Integer> wordQuantities, ArrayList<String> owners) {
        this.listNames = listNames;
        this.difficultyLevels = difficultyLevels;
        this.mContext = mContext;
        this.wordQuantities = wordQuantities;
        this.owners = owners;

        currentUser = new CurrentUser(mContext.getApplicationContext());
        userId = currentUser.getId();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.public_word_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Log.d(TAG, "onBindViewHolder: called");

        holder.listNameBtn.setText(listNames.get(position));
        holder.difficultyRB.setRating(difficultyLevels.get(position));
        holder.ownerTxt.setText("Utworzone przez: " + owners.get(position));

        String wordsQuantity = "Liczba słów w liście: "+wordQuantities.get(position);
        holder.wordsQuantity.setText(wordsQuantity);

        holder.listLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(mContext, "przeniesienie do listy słów", Toast.LENGTH_SHORT).show();
                /*
                currentUser.setCurrentListName(listNames.get(position));
                currentUser.setCurrentListOwner(owners.get(position));

                Intent intent = new Intent(mContext, Words.class);
                mContext.startActivity(intent);
                 */
            }
        });
    }

    @Override
    public int getItemCount() {
        return listNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private static final String TAG = "ViewHolder";

        TextView listNameBtn;
        RatingBar difficultyRB;
        TextView wordsQuantity;
        RelativeLayout listLayout;
        TextView ownerTxt;
        Button addListBtn;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            listNameBtn = itemView.findViewById(R.id.listNameBtn);
            difficultyRB = itemView.findViewById(R.id.difficultyRB);
            wordsQuantity = itemView.findViewById(R.id.wordsQuantityTxt);
            listLayout = itemView.findViewById(R.id.listLayout);
            ownerTxt = itemView.findViewById(R.id.ownerTxt);
            addListBtn = itemView.findViewById(R.id.addListBtn);

            addListBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "dodanie list do swojego katalogu", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onClick: naciśnięto przycisk do nauki słów");
                }
            });
        }
    }
}