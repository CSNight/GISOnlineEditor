package com.supermap.iserverex.address_query;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by HeTingwei on 2017/12/9.
 * 多线程客户端
 */
public class QUERY_POI_Client {
    private Socket socket;
    private static final int MESSAGE_SIZE = 2048;//每次允许接受数据的最大长度

    public static void main(String[] args) {
        new QUERY_POI_Client();
    }

    private QUERY_POI_Client() {
        try {
            socket = new Socket("127.0.0.1", 45678);
            if (socket.isConnected()) {
                System.out.println("连接成功");
                new Thread() {//开启一个接受数据的线程
                    @Override
                    public void run() {
                        super.run();
                        InputStream in;
                        try {
                            in = socket.getInputStream();
                            byte[] b;
                            while (true) {
                                b = new byte[MESSAGE_SIZE];
                                in.read(b);
                                System.out.println("接收到服务端消息：" + new String(b, "gbk"));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }.start();
            }
            OutputStream out = null;
            while (true) {
                Scanner scanner = new Scanner(System.in);
                String str = scanner.nextLine();
                out = socket.getOutputStream();
                out.write(str.getBytes("gbk"));
                out.flush();
                if (str.equals("end")) {
                    System.exit(0);//关闭客户端
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
