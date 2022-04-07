package com.wcn.jdk.example.io.bio;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketTest {
    public static void main(String[] args) throws Exception{
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("127.0.0.1", 8080), 1000);
        OutputStream outs = socket.getOutputStream();
        InputStream ins = socket.getInputStream();
        outs.write("hello".getBytes(StandardCharsets.UTF_8));
        outs.flush();
        System.out.println("send success.");
        byte[] result = new byte[1024];
        int length = ins.read(result);
        System.out.println("receive msg:"+new String(result, 0 , length));
        socket.close();
    }


}
