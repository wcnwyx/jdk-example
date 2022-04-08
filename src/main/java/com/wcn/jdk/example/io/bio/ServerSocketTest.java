package com.wcn.jdk.example.io.bio;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerSocketTest {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(8080));
        Socket socket = null;
        while((socket = serverSocket.accept())!=null){//accept()会产生阻塞等待
            Socket finalSocket = socket;
            System.out.println("socket income:"+finalSocket.getInetAddress()+" port:"+finalSocket.getPort());
            Thread thread= new Thread(()->{
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(finalSocket.getInputStream()));
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(finalSocket.getOutputStream()));
                    String msg = null;
                    while((msg=reader.readLine())!=null){//read会产生阻塞
                        System.out.println("receive msg:"+msg);
                        writer.write("response_"+msg+"\r\n");
                        writer.flush();
                    }
                    System.out.println("byte----"+finalSocket.getInetAddress()+" port:"+finalSocket.getPort());
                    writer.write("bye\r\n");
                    finalSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
    }
}
