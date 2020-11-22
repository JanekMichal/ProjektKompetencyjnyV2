package com.example.projektkompetencyjnyv2;

import android.content.Context;
import android.content.Intent;
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
import java.sql.Statement;
import java.util.ArrayList;

public class WordListsAdapter extends RecyclerView.Adapter<WordListsAdapter.ViewHolder>{

    private static final String TAG = "WordListsAdapter";
    private ArrayList<String> listNames;
    private ArrayList<Integer> difficultyLevels;
    private ArrayList<Integer> wordQuantities;
    private ArrayList<Integer> learnedQuantities;
    private ArrayList<String> owners;
    private Context mContext;
    //tymczasowe id użytkownika
    //TODO dodać przesyłanie id użytkownika
    private int userId=1;

    public WordListsAdapter(Context mContext,ArrayList<String> listNames, ArrayList<Integer> difficultyLevels,ArrayList<Integer> wordQuantities,ArrayList<Integer> learnedQuantities,ArrayList<String> owners) {
        this.listNames = listNames;
        this.difficultyLevels = difficultyLevels;
        this.mContext = mContext;
        this.wordQuantities=wordQuantities;
        this.learnedQuantities=learnedQuantities;
        this.owners=owners;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.word_list_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");

        holder.listNameBtn.setText(listNames.get(position));
        holder.difficultyRB.setRating(difficultyLevels.get(position));
        holder.progressBar.setProgress(learnedQuantities.get(position));
        holder.progressBar.setMax(wordQuantities.get(position));
        String progressTxt=learnedQuantities.get(position)+"/"+wordQuantities.get(position);
        holder.progressTxt.setText(progressTxt);
        Log.d(TAG, owners.get(position));
        holder.ownerTxt.setText("Utworzone przez: "+owners.get(position));

        holder.listLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on: "+listNames.get(position));
                Toast.makeText(mContext, listNames.get(position), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(mContext, Words.class);
                intent.putExtra("listName",listNames.get(position));
                intent.putExtra("owner",owners.get(position));
                mContext.startActivity(intent);
            }
        });

        holder.listNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "cliecked button", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(mContext, Words.class);
                intent.putExtra(MainActivity.EXTRA_TEXT,listNames.get(position));
                intent.putExtra(MainActivity.EXTRA_TEXT2,owners.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener,PopupMenu.OnMenuItemClickListener{

        private static final String TAG = "ViewHolder";

        Button listNameBtn;
        RatingBar difficultyRB;
        ProgressBar progressBar;
        TextView progressTxt;
        RelativeLayout listLayout;
        TextView ownerTxt;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            listNameBtn=itemView.findViewById(R.id.listNameBtn);
            difficultyRB=itemView.findViewById(R.id.difficultyRB);
            progressBar=itemView.findViewById(R.id.progressBar);
            progressTxt=itemView.findViewById(R.id.progressTxt);
            listLayout=itemView.findViewById(R.id.listLayout);
            ownerTxt=itemView.findViewById(R.id.ownerTxt);

            listNameBtn.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            showPopupMenu(v);
            return false;
        }

        private void showPopupMenu(View view){
            PopupMenu popupMenu=new PopupMenu(view.getContext(),view);
            popupMenu.inflate(R.menu.list_functions_popup_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()){

                case R.id.deleteList:
                    Log.d(TAG, "onMenuItemClick: wybrano usuwanie"+getAdapterPosition());
                    removeListItem(getAdapterPosition());
                    return true;

                default:
                    return false;
            }
        }
    }

    public void removeListItem(int position){

        int listOwnerId,listId;
        ConnectionClass connectionClass;
        Connection con;
        ResultSet rs;
        PreparedStatement stmt;

        connectionClass=new ConnectionClass();
        con=connectionClass.CONN();

        try {
            //pobranie id listy oraz id właściciela listy
            stmt = con.prepareStatement("" +
                    "select wl.id_word_list as id_list, wl.owner_Id as owner_id\n" +
                    "from Word_list as wl inner join [User] as u \n" +
                    "\ton wl.owner_id=u.id_user\n" +
                    "where wl.name=? and u.login=?");

            stmt.setString(1,listNames.get(position));
            stmt.setString(2,owners.get(position));
            rs=stmt.executeQuery();

            if(rs.next()){
                listOwnerId=rs.getInt("owner_id");
                listId=rs.getInt("id_list");
                rs.close();
                stmt.close();

                if(listOwnerId==userId){
                    //usuwanie listy z bazy
                    Log.d(TAG, "removeListItem: usuwanie listy właściciela");

                    //TODO sprawdzić jak działa usunięcie listy na tablice z postępem oraz z słowami
                    stmt = con.prepareStatement("" +
                            "delete\n" +
                            "from Word_list\n" +
                            "where id_word_list=?");

                    stmt.setInt(1,listId);
                    stmt.executeUpdate();
                }
                else{
                    //usuwanie z list użytkownika
                    Log.d(TAG, "removeListItem: usuwanie powiązania użytkownika z listą");

                    //TODO sprawdzić czy usuwa również progress
                    stmt = con.prepareStatement("" +
                            "delete\n" +
                            "from [User_WordList]\n" +
                            "where id_wordList=? AND id_user=?");

                    stmt.setInt(1,listId);
                    stmt.setInt(2,userId);
                    stmt.executeUpdate();
                }

                //usunięcie z listy w aplikacji
                listNames.remove(position);
                difficultyLevels.remove(position);
                wordQuantities.remove(position);
                learnedQuantities.remove(position);
                owners.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, listNames.size());

                rs.close();
                stmt.close();
                con.close();
            }
            else
                Toast.makeText(mContext, "Wystąpił błąd przy usuwaniu listy", Toast.LENGTH_SHORT).show();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
}



