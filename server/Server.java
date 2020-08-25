package com.it_cous.server;

import com.it_cous.writeLog.LogWriter;

import com.it_cous.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


public class Server {
    private final int port;

    public Server(int port) {
        super();
        this.port = port;
    }
    
    public static void main(String[] args) throws Exception {
        new Server(8999).run();
    }
    
    public void run() throws Exception {    	
        // SslContext를 사용하면 
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        try {
        	LogWriter.writeTime("--Server Init--");
            ServerBootstrap bootstrap = new ServerBootstrap();
            
            bootstrap.group(bossGroup, workerGroup)
            		 .channel(NioServerSocketChannel.class)
            		 .childHandler(new ServerInitializer());
            
            System.out.println("option, childoption set");
            
        	LogWriter.writeTime("--Server Start--");
        	LogWriter.writeTime("--Server bind port--");
            bootstrap.bind(port).sync().channel().closeFuture().sync();
//            ChannelFuture channelFuture = bootstrap.bind(port).sync();
//            			  channelFuture.channel().closeFuture().sync();

        }catch(Exception e) {
        	LogWriter.writeTime("--	Exception	--");
        	e.printStackTrace();
        	LogWriter.writeTime("--	Server shutdown	--");
        }finally {
        	bossGroup.shutdownGracefully();
        	workerGroup.shutdownGracefully();
        	LogWriter.writeTime("--	EventLoopGroup shutdown	--");
            LogWriter.writeTime("--	Server shutdown	--");
        }
    }
    
}