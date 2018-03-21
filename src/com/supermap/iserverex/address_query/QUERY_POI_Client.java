package com.supermap.iserverex.address_query;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

/**
 * Created by HeTingwei on 2017/12/9.
 * 多线程客户端
 */
public class QUERY_POI_Client {
    private Socket socket;
    public String guid = "";

    private ReceiveThread rec;
    private static final int MESSAGE_SIZE = 4096;//每次允许接受数据的最大长度

    public QUERY_POI_Client() {
        try {
            socket = new Socket("127.0.0.1", 45678);
            if (socket.isConnected()) {
                System.out.println("连接成功");
                rec = new ReceiveThread(socket);
                rec.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String SendMessage(String address) {
        new SendThread(socket, address).start();
        while (true) {
            if (rec.status) {
                rec.status = false;
                break;
            }
        }
        try {
            return rec.getClass().getDeclaredMethod("getRes").invoke(rec).toString();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return "";
        }
    }
    public String Stop(){
        rec.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";
    }
    //发送消息的线程
    class SendThread extends Thread {
        Socket socket;
        String str;

        SendThread(Socket socket, String str) {
            this.socket = socket;
            this.str = str;
        }

        @Override
        public void run() {
            super.run();
            try {
                socket.getOutputStream().write(str.getBytes("gbk"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //接受消息的线程（同时也有记录对应客户端socket的作用）
    class ReceiveThread extends Thread {
        Socket socket;//客户端对应的套接字
        public String getRes() {
            return res;
        }
        String res;
        volatile boolean status = false;
        ReceiveThread(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            super.run();
            //接收客户端发送的消息
            InputStream inputStream = null;
            try {
                inputStream = socket.getInputStream();
                while (true) {
                    try {
                        byte[] b;
                        while (true) {
                            b = new byte[MESSAGE_SIZE];
                            inputStream.read(b);
                            res = new String(b, "gbk");
                            status = true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {//关闭资源
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
