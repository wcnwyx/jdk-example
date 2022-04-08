package com.wcn.jdk.example.io.bio;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketTest {
    public static void main(String[] args) throws Exception{
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("127.0.0.1", 8080), 1000);

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        writer.write("hello\r\n");
        writer.flush();
        System.out.println("send success.");

        String msg = reader.readLine();
        System.out.println("receive msg:"+msg);
        socket.close();
    }


}
