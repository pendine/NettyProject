package com.it_cous.handler;

import java.nio.charset.Charset;
import java.util.List;

import com.it_cous.packet.Packet;
import com.it_cous.toHex.ByteToHex;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;


public class TCPDecoder extends ByteToMessageDecoder 
{

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> result) throws Exception {
		
		byte[] headerBytes = new byte[byteBuf.readableBytes()];
		byteBuf.readBytes(headerBytes); 
        String str = new String(ByteToHex.bytesToHex(headerBytes));  // hex String array로 돌려줄때

		result.add(str);  // String으로 돌려줌.
		/*
		 * header -> 공통
		 * body -> byteArray 
		 * tail -> 공통
		 * 
		 * 객체 
		 *   header 
		 *   body
		 *   tail 
		 * 
		 *   decoder -> next handler
		 *  			-> thread 돌릴때 switch case 문 사용해서 opcode 별로 프로세스  class 만들어서 처리한다.
		 *  													-> DB 넣거나 응답 보내던가. 
		 *  
		*/
	}

	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("-Exception  Class : TCPDecoder");
		cause.printStackTrace(); 
	}
	
}

