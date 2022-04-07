package com.wcn.jdk.example.io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
                    InputStream is = finalSocket.getInputStream();
                    OutputStream outs = finalSocket.getOutputStream();
                    int ret = -1;
                    while((ret=is.read())!=-1){//read会产生阻塞
                        System.out.println("receive msg:"+(char)ret);
                        outs.write(ret);
                    }
                    System.out.println("byte----"+finalSocket.getInetAddress()+" port:"+finalSocket.getPort());
                    outs.write("bye".getBytes(StandardCharsets.UTF_8));
                    finalSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
    }
}
