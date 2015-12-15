package com.example.asavarikarandikar.chatall;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dsouza on 07-12-2015.
 */
public class FriendDisplayAdapter extends ArrayAdapter<String> {
    String username;
    Context context;
    public FriendDisplayAdapter(Context context,ArrayList<String> friendname,String username ){
        super(context,0,friendname);
        this.username = username;
        this.context=context;

    }

    public View getView(int position,View convertView,ViewGroup parent){
        final String friendname = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.friend_list, parent, false);
        }
        Button name = (Button) convertView.findViewById(R.id.button);
        name.setText(friendname);
//        TextView name = (TextView) convertView.findViewById(R.id.button);
//        name.setText(friendname);
//        name.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent myIntent = new Intent(context,SendMessage.class);
//                myIntent.putExtra("friend",friendname);
//                myIntent.putExtra("username",username);
//            }
//        });
        return convertView;
    }
}
