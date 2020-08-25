package com.it_cous.server;

import com.it_cous.packet.Packet;
import com.it_cous.toHex.ByteToHex;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

public class ServerChannelHandler 
//extends ChannelInboundHandler //ä�� �ιٿ�� �ڵ鷯�� �ƴ� ä���ڵ鷯�� ����߱⶧���� �ȉ����.
extends SimpleChannelInboundHandler<Object>
{  
//	Override ������̼��� �ƴҰ�� ���ӵ� �޼ҵ尡 �ƴ� �����޼ҵ�� ����ϱ� ������
//	�����߻���. =-> ������̼� ����� �ʼ�
	
//	sout println => �α���¹����� ���� �� ��
	
	
	
	/**
	 * ��Ŷ���� �� ��Ŷ ��� �Ǵ� ��� ó��
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
//			xml getbean ��� new packetbody
			
		}else if( packet.getBytePos(CMDposition) == ByteToHex.hexToByteArray("a5") ) {
			System.out.println("sensor -> server : server get pis id ");
//			xml getbean ���
			
		}else if( packet.getBytePos(CMDposition) == ByteToHex.hexToByteArray("ab") ) {
			System.out.println("server get pis sensor");
//			xml getbean ���
			
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

	// ���ܰ� �߻��� ������ �ڵ带 ����
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("-Exception  Class : ChatServerChannelHandler");
		cause.printStackTrace(); // �׿��ִ� Ʈ���̽��� ����մϴ�.
		ctx.close(); // ���ؽ�Ʈ�� �����ŵ�ϴ�.
	}

}
