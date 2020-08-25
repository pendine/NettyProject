package com.it_cous.handler;

import java.awt.datatransfer.SystemFlavorMap;
import java.net.SocketAddress;

import com.it_cous.packet.Packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;

public class TCPEncoder extends MessageToByteEncoder<Object> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		// TODO Auto-generated method stub
		
		byte[] test = {36,24,36};
		out.writeBytes(test);
		ctx.write(test);
		ctx.write(out);
		
		
		Packet sendToPISCoo = new Packet();
		System.out.println("now Encoder");
		byte[] firstInit = sendToPISCoo.firstSend();
//		msg = (Object)firstInit;
//		System.out.println("msg init");
//		for(byte b : firstInit){
//				System.out.print( b + " ");
//		}
//		System.out.println("msg write");
		out.readBytes(firstInit);
		out.writeBytes(firstInit);
		
		System.out.println("Server -> sensor");
		System.out.println("Massage send");
		ctx.write(out);
		ctx.flush();
	}
	
	
	
}
