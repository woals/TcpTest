package com.yinyxn.tcptest;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class MainActivity extends Activity {
    /* 服务器地址 */
    private final String SERVER_HOST_IP = "192.168.43.75";

    /* 服务器端口 */
    private final int SERVER_HOST_PORT = 8080;

    private TextView textView_receive;
    private Button btnConnect;
    private Button btnSend;
    private EditText editSend;
    private Socket socket;
    private PrintStream output;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnSend = (Button) findViewById(R.id.btnSend);
        editSend = (EditText) findViewById(R.id.sendMsg);
        textView_receive = (TextView) findViewById(R.id.textView_receive);

        btnSend.setEnabled(false);
        editSend.setEnabled(false);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Task().execute();
                if (!App.HADCONNECT.isEmpty()){
                    Toast.makeText(MainActivity.this, App.HADCONNECT, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSend.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.SENDER = editSend.getText().toString();
            }
        });
    }

    private class Task extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                Socket socket = null;
                InputStream in = null;
                OutputStream out = null;
                socket = new Socket("192.168.43.75", 8080);
//                    toastText("已连接");
                Log.d("debug", "已连接");


                App.HADCONNECT = "已连接";


                in = socket.getInputStream();
                out = socket.getOutputStream();
                //发送

                out.write("saf".getBytes());
                out.flush();
                Log.d("debug", "已发送");

                //接收
                byte[] buf = new byte[1024];
                int size = in.read(buf);
                String data = new String(buf, 0, size,"gbk");
                Log.d("debug", data);
                App.RECEIVE = data;


            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return App.RECEIVE;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            textView_receive.append(aVoid);
        }
    }

    private class MyThread extends Thread {
        @Override
        public void run() {
            try {
                /* 连接服务器 */
                socket = new Socket(SERVER_HOST_IP, SERVER_HOST_PORT);

                /* 获取输出流 */
                output = new PrintStream(socket.getOutputStream(), true, "utf-8");

                UIMessage();
            } catch (UnknownHostException e) {
                handleException(e, "未知主机异常: " + e.toString());
            } catch (IOException e) {
                handleException(e, "IO异常: " + e.toString());
            }
        }
    }

//    public void closeSocket()
//    {
//        try
//        {
//            output.close();
//            socket.close();
//        }
//        catch (IOException e)
//        {
//            handleException(e, "close exception: ");
//        }
//    }

    private void UIMessage() {
        btnConnect.setEnabled(false);
        editSend.setEnabled(true);
        btnSend.setEnabled(true);
    }

    private void sendMessage(String msg) {
        output.print(msg);
    }

    public void toastText() {
        String message;
        Bundle bundle = new Bundle();
        message = bundle.getString(App.HADCONNECT);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void handleException(Exception e, String prefix) {
        e.printStackTrace();
//        toastText(prefix + e.toString());
    }
}
