package io.netty.example.learn.netty.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created by pengfei.dong
 * Date 2018/7/23
 * Time 下午8:02
 */

public class LearNetty {

    public static void main(String[] args) {
        EventLoopGroup eventExecutors = new NioEventLoopGroup(1);

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(eventExecutors)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new DemoServerHandler());
                        }
                    });

            // Start the server.
            ChannelFuture f = serverBootstrap.bind(8080).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
}
