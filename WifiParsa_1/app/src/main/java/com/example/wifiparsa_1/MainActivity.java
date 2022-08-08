package com.example.wifiparsa_1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
    {


    //این permission رو به manifests اضافه کنید
    // <uses-permission android:name="android.permission.INTERNET" />
    //<uses-permission android:name = "android.permission.ACCESS_NETWORK_STATE" />

    //  کتابخانه های مورد نیاز رو به build.gradle اضافه کنید
    //implementation 'com.loopj.android:android-async-http:1.4.9'
    //implementation 'com.android.volley:volley:1.1.1'


    // ابجکت های مورد نیاز
    Thread Thread = null;
    private PrintWriter output;
    private InputStream input;
    Socket socket = null;
    Button send, connection;


    // اطلاعات سرور
    String server_ip = "192.168.5.1";
    int server_prot = 8888;


    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        config();
        if (android.os.Build.VERSION.SDK_INT > 9)
            {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            }
        }


    private void config()
        {

        connection = findViewById(R.id.button);
        connection.setOnClickListener(v ->
            {
            Thread = new Thread(new ThreadConnect());
            Thread.start();

            });

        send = findViewById(R.id.button2);
        send.setOnClickListener(v ->
            {
            //************************************************************************
            // نحوه فراخوانی و ارسال دیتا به سمت سرور
            sendData();
            //************************************************************************
            });


        }

//    @Override
//    public void onClick(View v) {
//    if (v.getId() == R.id.button) {
//
//    Thread1 = new Thread(new Thread1());
//    Thread1.start();
//
//    }
//    if (v.getId() == R.id.button2) {
//    //************************************************************************
//    // نحوه فراخوانی و ارسال دیتا به سمت سرور
//    sendData();
//    //************************************************************************
//    }
//    }

    //************************************************************************
    // این تابع برای ارسال دیتا به سمت سرور مورد استفاده قرار میگره
    public void sendData()
        {

        Map<String, String> data = new HashMap<>();

        // توسط دستور زیر میتونید هر دیتایی که نیاز دارید رو داخل مپ ذخیره و بعد به جیسون تبدیل کنید

        data.put("Name", "Mojtaba");
        data.put("Pass", "0000");

        JSONObject jsonData = new JSONObject(data);

        new Thread(new ThreadSend(jsonData.toString())).start();

        }


    //************************************************************************


// ===============================================================================
//                           send data
//===============================================================================
    /*
    this function connect to server with server_ip and server_port

     */


    class ThreadConnect implements Runnable
        {
        public void run()
            {


            try
                {

                if (!server_ip.equals(""))
                    {

                    socket = new Socket(server_ip, server_prot);
                    output = new PrintWriter(socket.getOutputStream());
                    input = socket.getInputStream();

                    } else
                    {
                    toast("خطایی رخ داده است");
                    }
                runOnUiThread(new Runnable()
                    {
                    @Override
                    public void run()
                        {
                        if (socket.isConnected())
                            {
                            toast("اتصال با سرور برقرار شد");
                            } else
                            {
                            toast("اتصال با سرور برقرار نشد");
                            }
                        }
                    });
                } catch (IOException e)
                {
                e.printStackTrace();
                }
            }
        }

    class ThreadRead implements Runnable
        {
        @Override
        public void run()
            {
            while (true)
                {
                if (socket.isConnected())
                    {
                    try
                        {
                        String message = "";
                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = input.read(buffer)) != -1)
                            {
                            Log.e("test_tag", "try = 2" + read);
                            String output = new String(buffer, 0, read);
                            if (!output.equals(""))
                                {
                                runOnUiThread(new Runnable()
                                    {
                                    @Override
                                    public void run()
                                        {
                                        toast(output);
                                        }
                                    });
                                } else
                                {
                                toast("not");
                                Thread = new Thread(new ThreadConnect());
                                Thread.start();
                                return;
                                }
                            }
                        ;
                        } catch (IOException e)
                        {
                        e.printStackTrace();
                        }
                    }

                }
            }
        }

    class ThreadSend implements Runnable
        {
        private String message;

        ThreadSend(String message)
            {
            this.message = message;
            Log.e("tagtest = ", message);
            }

        @Override
        public void run()
            {
            runOnUiThread(new Runnable()
                {
                @Override
                public void run()
                    {
                    if (socket != null)
                        {
                        output.write(message);
                        output.flush();
                        new Thread(new ThreadRead()).start();
                        toast(message);
                        } else
                        {
                        toast("ارتباط با سرور برقرار نیست");
                        }

                    }
                });

            }
        }


    public void toast(String text)
        {

        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

        }
    }