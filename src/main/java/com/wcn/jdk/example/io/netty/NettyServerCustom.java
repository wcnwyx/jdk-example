package com.wcn.jdk.example.io.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * 不使用netty传统的ServerBootstrap来启动，
 * 采用传统的jdk nio模式来启动，更好立刻netty的不见
 */
public class NettyServerCustom {
    public static void main(String[] args) throws InterruptedException {
        //相当于jdk中的selector
        NioEventLoopGroup bossSelector = new NioEventLoopGroup(1);//用于接受处理accept
        NioEventLoopGroup workerSelector = new NioEventLoopGroup(4);//用于接受处理read
        System.out.println("bossSelector:"+bossSelector);
        System.out.println("workerSelector:"+workerSelector);


        NioServerSocketChannel nioServerSocketChannel = new NioServerSocketChannel();
        bossSelector.register(nioServerSocketChannel);//将ServerSocketChannel注册到selector上

        nioServerSocketChannel.pipeline().addLast(new AcceptHandler(workerSelector));//pipeline中注册一个处理器
        ChannelFuture channelFuture = nioServerSocketChannel.bind(new InetSocketAddress(8080));
        channelFuture.sync();//同步阻塞到bind成功
        System.out.println("server start success.");

        nioServerSocketChannel.closeFuture().sync();//阻塞到关闭
        bossSelector.shutdownGracefully().sync();//关闭NioEventLoopGroup，其也是一个线程池
        workerSelector.shutdownGracefully().sync();
        System.out.println("server shutdown.");
    }

    static class AcceptHandler extends ChannelInboundHandlerAdapter{
        public NioEventLoopGroup workerSelector;

        public AcceptHandler(NioEventLoopGroup workerSelector) {
            this.workerSelector = workerSelector;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            //这里打印出EventLoopGroup可以看出ReadHandler是在bossSelector中执行的
            System.out.println("acceptHandler use Selector is :"+ctx.channel().eventLoop().parent());
            //有新的客户端连接过来，可以accept了此时的msg是一个NioSocketChannel
            NioSocketChannel nioSocketChannel = (NioSocketChannel) msg;
            System.out.println("accept new client:"+nioSocketChannel.remoteAddress());
            //socketChannel的pipeline中注册一个handler处理其
            nioSocketChannel.pipeline().addLast(new ReadHandler());
            workerSelector.register(nioSocketChannel);//将accept的新连接注册到workerSelector中
        }
    }

    static class ReadHandler extends ChannelInboundHandlerAdapter{
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            //这里打印出EventLoopGroup可以看出ReadHandler是在workerSelector中执行的
            System.out.println("readHandler use Selector is :"+ctx.channel().eventLoop().parent());
            //接收到客户端报文，可以read了
            ByteBuf byteBuf = (ByteBuf) msg;
            CharSequence charSequence = byteBuf.readCharSequence(byteBuf.readableBytes(), StandardCharsets.UTF_8);
            System.out.println("receive msg:"+charSequence.toString());

            byteBuf.writeBytes("response".getBytes(StandardCharsets.UTF_8));
            byteBuf.writeBytes(charSequence.toString().getBytes(StandardCharsets.UTF_8));
            ctx.channel().writeAndFlush(byteBuf);
            System.out.println("send msg to client success.");
        }
    }
}
