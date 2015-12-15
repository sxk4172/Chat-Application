package com.example.asavarikarandikar.chatall;

import android.content.Intent;
import android.os.AsyncTask;
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

import android.widget.TextView;

/**
 * Created by asavarikarandikar on 12/3/15.
 */
public class SendMessage extends AppCompatActivity implements DownloadTask.ReceiveMessageHelper {
    String friend;
    String username;
    public AsyncTask task;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_box);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String data = getIntent().getExtras().getString("friend");
        String user = getIntent().getExtras().getString("username");
        Log.d("Get data", data);
        Log.d("Get data", user);
        username = user;
        friend = data;
        TextView myAwesomeTextView = (TextView) findViewById(R.id.textView);
        myAwesomeTextView.setText(data);
//        try {
//            task = new DownloadTask(this, username, friend).execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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

    public void sendMessage(View view) {

        String message;
        EditText userNameView = (EditText) findViewById(R.id.sendMessage);
        message = userNameView.getText().toString();

        if (message.equals("")) {
            return;
        }

        Json jsonFile = new Json();
        jsonFile.put("query", "sendmessage");
        jsonFile.put("message", message);
        jsonFile.put("receivername", friend);
        jsonFile.put("username", username);

        String json = jsonFile.getJsonString();

        try {
            Log.v("SendMessage", message);
            Socket socket = new Socket("glados.cs.rit.edu", 5896);

            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            long time = System.currentTimeMillis();
            out.write(json.getBytes());
            byte b[] = new byte[65000];
            int count = in.read(b);
            long time1 = System.currentTimeMillis();
            long timeIs = time1 - time;
            Log.d("Time", String.valueOf(timeIs));
            Log.v("Iam here", new String(b, "UTF-8"));
            if (count > 0) {
                ByteStreamData data = new ByteStreamData();
                data.setData(b, count);
                Json responseJson = new Json(data.getString());
                if (responseJson.getValue("query").equalsIgnoreCase("ok")) {
                    Log.v("Message Sent", "OK");
                    TextView getMessage = (TextView) findViewById(R.id.messageHistory);
                    String temp = String.valueOf(getMessage.getText());
                    temp = temp + "\n" + username + ":" + "\n" + message + "\n";
                    getMessage.setText(temp);
                    EditText getMessage1 = (EditText) findViewById(R.id.sendMessage);
                    getMessage1.setText("");
                }

            }

        } catch (NumberFormatException | IOException e) {
            Log.d("Error", e.getMessage());
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(" onpause sendmessage", "true");
        task.cancel(true);
    }

    public void onResume() {
        super.onResume();
        Log.d("onResume sendmessage", "true");
        try {
            task = new DownloadTask(this, username, friend).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        Log.d("keypressed", "onbackpressed sendmessage");

        task.cancel(true);

    }

    public void onDestroy() {
        super.onDestroy();
        task.cancel(true);
    }

    public void onStop() {
        super.onStop();
        task.cancel(true);
    }


    @Override
    public void updateMessageHistory(final String updateMessage) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView getMessage = (TextView) findViewById(R.id.messageHistory);
                String temp = String.valueOf(getMessage.getText());
                temp = temp + "\n" + friend + ":" + "\n" + updateMessage + "\n";
                getMessage.setText(temp);
            }
        });
        task.cancel(true);
        try {
            task = new DownloadTask(this, username, friend).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}