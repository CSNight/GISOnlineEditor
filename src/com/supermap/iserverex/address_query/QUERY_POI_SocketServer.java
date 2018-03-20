package com.supermap.iserverex.address_query;

import com.supermap.data.DatasetVector;
import com.supermap.data.Workspace;
import com.supermap.iserverex.utils.GUID;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class QUERY_POI_SocketServer {
    private Map<String, ReceiveThread> receiveList = new HashMap<>();//存放已连接客户端类
    private final static int MESSAGE_SIZE = 2048;//每次允许接受数据的最大长度
    private int count = 0;
    private Workspace ws;
    private DatasetVector dv;
    private ServerSocket serverSocket;

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    //服务端处理逻辑
    public QUERY_POI_SocketServer(Workspace ws, DatasetVector dv) {
        this.ws = ws;
        this.dv = dv;
        serverSocket = null;
        try {
            serverSocket = new ServerSocket(45678);//用来监听的套接字，指定端口号
            while (true) {
                Socket socket = serverSocket.accept();//监听客户端连接，阻塞线程
                String id = GUID.getUUID();
                System.out.println("连接上客户端：" + id);
                //在其他线程处理接收来自客户端的消息
                ReceiveThread receiveThread = new ReceiveThread(socket, id);
                receiveThread.start();
                receiveList.put(id, receiveThread);
                new SendThread(receiveList.get(id).socket, id).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //接受消息的线程（同时也有记录对应客户端socket的作用）
    class ReceiveThread extends Thread {
        String id;
        Socket socket;//客户端对应的套接字
        boolean continueReceive = true;//标识是否还维持连接需要接收

        ReceiveThread(Socket socket, String id) {
            this.socket = socket;
            this.id = id;
            try {
                //给连接上的客户端发送，分配的客户端编号的通知
                socket.getOutputStream().write(("你的客户端编号是" + id).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            super.run();
            //接收客户端发送的消息
            InputStream inputStream = null;
            try {
                inputStream = socket.getInputStream();
                byte[] b;
                while (continueReceive) {
                    b = new byte[MESSAGE_SIZE];
                    inputStream.read(b);
                    b = splitByte(b);//去掉数组无用部分
                    //发送end的客户端断开连接
                    if (new String(b).equals("end")) {
                        continueReceive = false;
                        receiveList.remove(this.id);
                        //通知其他客户端
                        StringBuilder message = new StringBuilder("客户端" + id + "连接断开\n" +
                                "现在在线的有，客户端：");
                        for (String thread_id : receiveList.keySet()) {
                            message.append(" ").append(receiveList.get(thread_id).id);
                        }
                        System.out.println(message);
                    } else {
                        try {
                            new SendThread(socket, QUERY_POI_SupermapAPI.getDS(new String(b), ws, dv)).start();
                            count++;
                            System.out.print("\r" + count);
                        } catch (Exception e) {
                            new SendThread(socket, "输入错误，请重新输入").start();
                            System.out.println("客户端输入错误");
                        }

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
                socket.getOutputStream().write(str.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //去除byte数组多余部分
    private byte[] splitByte(byte b[]) {
        int i = 0;
        for (; i < b.length; i++) {
            if (b[i] == 0) {
                break;
            }
        }
        byte[] b2 = new byte[i];
        System.arraycopy(b, 0, b2, 0, i);
        return b2;
    }
}
