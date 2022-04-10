package com.wcn.jdk.example.io.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * 不采用官方的Bootstrap来启动，
 * 采用传统jdk的nio模式来启动
 */
public class NettyClientCustom {
    public static void main(String[] args) throws InterruptedException {
        //相当鱼jdk的selector
        NioEventLoopGroup selectorThread = new NioEventLoopGroup();

        NioSocketChannel nioSocketChannel = new NioSocketChannel();
        selectorThread.register(nioSocketChannel);//将SocketChannel注册到selector
        nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                ByteBuf byteBuf = (ByteBuf) msg;
                CharSequence charSequence = byteBuf.readCharSequence(byteBuf.readableBytes(), StandardCharsets.UTF_8);
                System.out.println("receive msg:"+charSequence.toString());
                ctx.channel().close();//接受到就关闭socket了
            }
        });//pipeline中添加一个响应返回报文的handler

        ChannelFuture channelFuture = nioSocketChannel.connect(new InetSocketAddress("127.0.0.1", 8080));
        channelFuture.sync();//阻塞到连接成功

        ByteBuf byteBuf = Unpooled.copiedBuffer("123".getBytes(StandardCharsets.UTF_8));
        ChannelFuture sendFuture = nioSocketChannel.writeAndFlush(byteBuf);
        sendFuture.sync();//阻塞到发送完成
        System.out.println("send msg success.");
//        sendFuture.addListener();//也可以注册异步监听器

        nioSocketChannel.closeFuture().sync();//阻塞到关闭
        selectorThread.shutdownGracefully().sync();//NioEventLoopGroup 这个线程池也要关闭
        System.out.println("client shutdown.");
    }
}
