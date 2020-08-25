package com.it_cous.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

public class IdleHandler extends IdleStateHandler{

	public IdleHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
		super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
		// TODO Auto-generated constructor stub
	}
	
	public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception{
		
		
	}
	
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception{
		
		super.channelActive(ctx);
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception{
		
		super.channelInactive(ctx);
	}
	
	public void ExceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
		
		ctx.channel().close();
	}
	
}
