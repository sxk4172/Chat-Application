package com.example.asavarikarandikar.chatall;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.util.Log;
import android.widget.TextView;


/**
 * Created by sanikakulkarni on 12/5/15.
 */
public class DownloadTask extends AsyncTask {
    Socket socket;
    String username, friend;

    DownloadTask(ReceiveMessageHelper help, String username, String friend) throws IOException {
        this.mHelper = help;
        this.username = username;
        this.friend = friend;
        Log.v("async take creates", "task created");

    }

    public static interface ReceiveMessageHelper {
        public void updateMessageHistory(String updateMessage);

    }

    private ReceiveMessageHelper mHelper;
    String messages;

    @Override
    protected Object doInBackground(Object[] params) {
        try {

           while (true) {
                Log.d("trying create a socket","Async task");
                socket = new Socket("glados.cs.rit.edu", 5896);
                Log.d("Created a new socket","Async task");
                Json jsonFile = new Json();
                jsonFile.put("query", "requestmessage");
                jsonFile.put("username", username);
                jsonFile.put("sendername", friend);
                String json = jsonFile.getJsonString();

                OutputStream out = socket.getOutputStream();
                InputStream in = socket.getInputStream();

                int count = 0;
                out.write(json.getBytes());
                System.out.println("write ke baad");


                byte b[] = new byte[65000];
                Log.d("beforein.available", String.valueOf(count));
               // if (in.available() > 0) {
                    Log.d("In in.available","true");
                    count = in.read(b);
                //}
                Log.v("Iam here", new String(b, "UTF-8"));
                if (count > 0) {
                    ByteStreamData data = new ByteStreamData();
                    data.setData(b, count);
                    Json responseJson = new Json(data.getString());
                    System.out.println(responseJson);
                    if (responseJson.getValue("query").equalsIgnoreCase("messageresponse")) {
                        Log.v("Message Sent", "OKmessageresponse");
                        ArrayList<String> messagesList = responseJson.getMulVal("message");
                        messages = "";
                        for (int i = 0; i < messagesList.size(); i++) {
                            if (i == 0) {
                                messages = messagesList.get(i);
                            } else {
                                messages = messages + "\n" + messagesList.get(i);
                            }

                            System.out.println("recieved - " + messages);
                        }

                        mHelper.updateMessageHistory(messages);

                    }
                }
                Log.d("before sleep","asynctask");
               // while (in.available() == 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
               // }
                Log.d("before inCancelled", "asynctask");
                if (isCancelled()) {
                    Log.d("Asynctask is cancelled","so break the loop");
                    //socket.close();
                    break;
                }
                socket.close();
            }
        } catch (NumberFormatException | IOException e) {
            Log.d("Error", e.getMessage());
        }


        return null;
    }

    protected void onPostExecute(Void result) {
        //The task is complete, tell the status bar about it

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
