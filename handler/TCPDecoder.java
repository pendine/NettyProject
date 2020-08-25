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
        String str = new String(ByteToHex.bytesToHex(headerBytes));  // hex String array�� �����ٶ�

		result.add(str);  // String���� ������.
		/*
		 * header -> ����
		 * body -> byteArray 
		 * tail -> ����
		 * 
		 * ��ü 
		 *   header 
		 *   body
		 *   tail 
		 * 
		 *   decoder -> next handler
		 *  			-> thread ������ switch case �� ����ؼ� opcode ���� ���μ���  class ���� ó���Ѵ�.
		 *  													-> DB �ְų� ���� ��������. 
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

