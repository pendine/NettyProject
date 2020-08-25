package com.it_cous.server;

import com.it_cous.packet.Packet;
import com.it_cous.toHex.ByteToHex;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

public class ServerChannelHandler 
//extends ChannelInboundHandler //채널 인바운드 핸들러가 아닌 채널핸들러만 사용했기때문에 안됬었음.
extends SimpleChannelInboundHandler<Object>
{  
//	Override 어노테이션이 아닐경우 종속된 메소드가 아닌 개별메소드로 취급하기 때문에
//	오류발생함. =-> 어노테이션 사용이 필수
	
//	sout println => 로그출력문으로 변경 할 것
	
	
	
	/**
	 * 패킷검증 후 패킷 사용 또는 폐기 처분
	 * */
	@Override
	public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {  
		System.out.println("-Channel Read0 method  Class : ChatServerChannelHandler");
		
		int DIRposition = 2;
		int CMDposition = 3;
		
		String tmp = (String) msg;
		byte[] in = ByteToHex.hexStringToByteArray(tmp);
		
		System.out.println("in length : "+in.length);
		System.out.println("byteBuf.readableBytes() : " + ByteToHex.bytesToHex(in));
		Packet packet = new Packet();
		packet.setHeader(in); 
		
		if( packet.getRecieved() ) {
			System.out.println("Good Packet : do work");			
			System.out.println("Packet body - [ " +  ByteToHex.bytesToHex(packet.getBody()) + " ] ");
			System.out.println("cs - [ " +  ByteToHex.byteToHex(packet.getCs()) + " ] ");
			System.out.println(packet.toString());
		}else {
			System.out.println("Bad Packet : do not work");
		}
		
		System.out.println("packet.toString() = "+packet.toString());

		System.out.println("DIRposition byte : "+ packet.getBytePos(DIRposition) + " HEX : " + ByteToHex.byteToHex(packet.getBytePos(DIRposition)) );
		System.out.println("CMDposition byte : "+ packet.getBytePos(CMDposition) + " HEX : " + ByteToHex.byteToHex(packet.getBytePos(CMDposition)) );
		
		System.out.print("DIR : ");
		if( packet.getBytePos(DIRposition) == ByteToHex.hexToByteArray("7a") ) {
			System.out.println("sensor -> server ");
		}else if( packet.getBytePos(DIRposition) == ByteToHex.hexToByteArray("5a") ) {
			System.out.println("server -> sensor ");
		}
		
		System.out.print("CMD : ");
		if( packet.getBytePos(CMDposition) == ByteToHex.hexToByteArray("ad") ) {
			System.out.println("transfer network error");
//			xml getbean 사용 new packetbody
			
		}else if( packet.getBytePos(CMDposition) == ByteToHex.hexToByteArray("a5") ) {
			System.out.println("sensor -> server : server get pis id ");
//			xml getbean 사용
			
		}else if( packet.getBytePos(CMDposition) == ByteToHex.hexToByteArray("ab") ) {
			System.out.println("server get pis sensor");
//			xml getbean 사용
			
		}
		
		
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(" - Channel Active " );
		byte[] returnArr = new Packet().firstSend();
		System.out.println("Packet.firstSend() Actived");
		ctx.write(returnArr);
		System.out.println("Packet.firstSend() Context send");
		
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("- Channel Inactive " );
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("- Channel Read Complete " );
	}

	// 예외가 발생시 동작할 코드를 정의
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("-Exception  Class : ChatServerChannelHandler");
		cause.printStackTrace(); // 쌓여있는 트레이스를 출력합니다.
		ctx.close(); // 컨텍스트를 종료시킵니다.
	}

}
