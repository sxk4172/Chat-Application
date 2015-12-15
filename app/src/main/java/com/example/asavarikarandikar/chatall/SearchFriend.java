package com.example.asavarikarandikar.chatall;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by asavarikarandikar on 12/3/15.
 */
public class SearchFriend extends AppCompatActivity {
    String username;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_friend);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String user = getIntent().getExtras().getString("username");
        username = user;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void search(View view) {
        String friend;
        EditText friendView = (EditText)findViewById(R.id.search_friend);
        friend = friendView.getText().toString();

        Json jsonFile = new Json();
        jsonFile.put("query","searchfriend");
        jsonFile.put("username", friend);
        String json = jsonFile.getJsonString();

        try {
            Log.v("SearchFriend", friend);
            Socket socket = new Socket("glados.cs.rit.edu", 5896);

            OutputStream out= socket.getOutputStream();
            InputStream in = socket.getInputStream();
            out.write(json.getBytes());
            byte b[] = new byte[65000];
            int count = in.read(b);
            Log.v("Friend is here",new String(b, "UTF-8"));
            if(count>0){
                ByteStreamData data = new ByteStreamData();
                data.setData(b,count);
                Json responseJson = new Json(data.getString());
                if(responseJson.getValue("query").equalsIgnoreCase("ok")){
                    Log.v("So Friendship", "OK");
                    Intent myIntent = new Intent(SearchFriend.this,SendMessage.class);
                    myIntent.putExtra("friend",friend);
                    myIntent.putExtra("username",username);
                    startActivity(myIntent);
                }

            }

        } catch(NumberFormatException | IOException e) {
            Log.d("Error",e.getMessage());
        }

    }

}
