package com.it_cous.server;

import com.it_cous.handler.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
 
public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    
    protected void initChannel(SocketChannel arg0) throws Exception {
        ChannelPipeline pipeline = arg0.pipeline();
        System.out.println("channel init method");
        
        pipeline.addLast(new TCPDecoder());				//디코더와 핸들러의 위치를 바꾸면 똑같은 경고문구라도 다름. <-이유 채널이 닫힌 후 채널을 디코딩하려하기때문
        pipeline.addLast(new TCPEncoder());	
        pipeline.addLast(new ServerChannelHandler());//인바운드와 아웃바운드를 정확이 이해한건지 다시 확인이 필요함
		 
    }
 
}
