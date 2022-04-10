package com.wcn.jdk.example.io.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.nio.charset.StandardCharsets;

public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup worker = new NioEventLoopGroup(4);

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf)msg;
                                int byteNum = byteBuf.readableBytes();
                                byte[] bytes = new byte[byteNum];
                                byteBuf.readBytes(bytes);
                                System.out.println("receive msg:"+new String(bytes));

                                byteBuf.writeBytes("response:".getBytes(StandardCharsets.UTF_8));
                                byteBuf.writeBytes(bytes);
                                ctx.channel().writeAndFlush(byteBuf);
                            }
                        });
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture ch = serverBootstrap.bind(8080).sync();//阻塞到服务启动完成
        ch.channel().closeFuture().sync();//阻塞到channel关闭
    }
}
