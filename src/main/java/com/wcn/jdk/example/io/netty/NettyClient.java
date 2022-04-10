package com.wcn.jdk.example.io.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture connectFuture = bootstrap
                .group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                CharSequence charSequence = byteBuf.readCharSequence(byteBuf.readableBytes(), StandardCharsets.UTF_8);
                                System.out.println("receive msg:"+charSequence.toString());
                                ctx.channel().close();//接受到就关闭socket了
                            }
                        });
                    }
                })
                .option(ChannelOption.TCP_NODELAY, true)
                .connect(new InetSocketAddress("127.0.0.1", 8080));
        connectFuture.sync();//阻塞到connect成功
        System.out.println("connect success.");

        ByteBuf byteBuf = Unpooled.copiedBuffer("123".getBytes(StandardCharsets.UTF_8));
        connectFuture.channel().writeAndFlush(byteBuf);
        System.out.println("send msg success.");

        ChannelFuture closeFuture = connectFuture.channel().closeFuture();
        closeFuture.sync();//阻塞到close
        System.out.println("NioSocketChannel shutdown.");
        nioEventLoopGroup.shutdownGracefully();//关闭NioEventLoopGroup，其是一个线程池
        System.out.println("NioEventLoopGroup shutdown.");
        System.out.println("client shutdown.");
    }
}
