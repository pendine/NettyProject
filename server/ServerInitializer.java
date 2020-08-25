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
        
        pipeline.addLast(new TCPDecoder());				//���ڴ��� �ڵ鷯�� ��ġ�� �ٲٸ� �Ȱ��� ������� �ٸ�. <-���� ä���� ���� �� ä���� ���ڵ��Ϸ��ϱ⶧��
        pipeline.addLast(new TCPEncoder());	
        pipeline.addLast(new ServerChannelHandler());//�ιٿ��� �ƿ��ٿ�带 ��Ȯ�� �����Ѱ��� �ٽ� Ȯ���� �ʿ���
		 
    }
 
}
