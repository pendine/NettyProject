package com.it_cous.packet;

import com.it_cous.toHex.ByteToHex;
/**
 * 패킷의 cmd와 cs사이의 모든 내용을 여기서 처리할것이며
 * 이 클래스를 사용할 곳에서 cmd를 구분해야함.
 * 여기는 단순히 동작을 처리할 부분.
 * */
public class PacketBody extends Packet{

	/**네트워크 에러 패킷을 전송한다.
3	DIR					byte	1	패킷 방향				0x7A(Rx from Coordinator)
4	CMD					byte	1	CMD(OP CODE)		0xad
	네트워크 에러 발생한 게이트웨이 수만큼 반복		반복	N			
5	GATEWAY				byte	2	게이트웨이 번호			0x00 0x00 … 0xXX 0xXX
	네트워크 에러 발생한 게이트웨이 수만큼 반복		반복	N			
7	CS					byte	1	CheckSum			1~패킷끝 XOR 값 입니다.
	 * */
	
	/**PIS의 시설물ID를 서버로 전송한다.
3	DIR					byte	1	패킷 방향				0x7A(Rx from Coordinator)
4	CMD					byte	1	CMD(OP CODE)		0xa5
5	Facility ID			number	1	시설물 ID				0x00     << int형으로써 4바이트인지 아니면 1바이트인지만 사용목적에따라 다르게 적은것인지 모르겠음
	게이트웨이 수 만큼 반복						
6	Gateway n			byte	1	게이트웨이 n번 아이디		0x00 0x00
7	Gateway n_Channel	byte	4	게이트웨이 n번 채널		"0x01 0xBF 0x22 0x2E
														(ch 447.8750)"
	게이트웨이 수 만큼 반복						
8	CS					byte	1	CheckSum		1~패킷끝 XOR 값 입니다.
	 * */
	
	/**PIS 주차센서정보를 서버로 전송한다.
3	DIR					byte	1	패킷 방향				0x7A(Rx from Coordinator)
4	CMD					byte	1	CMD(OP CODE)		0xab
5	GATEWAY				byte	2	게이트웨이 번호			0x00 0x00 … 0xXX 0xXX
	Hub Packet 시작						
6	SOH					byte	1	Start Of Header		0x2B
7	LEN					byte	1	패킷 길이		
8	CMD					byte	1	CMD(OP CODE)		"I" (0x69)
9	TAG_ID				byte	4	센서ID		
10	STATUS				byte	1	주차 상태				"E" (0x45) : 비어있음
														"P" (0x50) : 주차되어 있음
11	Battery				byte	1	배터리 전압				(battery Value * 0.05) + 1.7 V
12	CS					byte	1	CheckSum			항목 6-11 XOR 값
	Hub Packet 끝
13	CS					byte	1	CheckSum			1~패킷끝 XOR 값 입니다.
	 * */
/*	패킷 방향	0x7A = 서버에서 수신
	CMD = 0xad : 네트워크 에러 패킷을 서버로 전송
		  0xa5 : PIS의 시설물ID를 서버로 전송
		  0xab : PIS 주차센서정보를 서버로 전송
*/

	private byte[] packetBody;
	
	public PacketBody() {}
	
	public PacketBody(byte[] packetBody) {
		this.packetBody = packetBody;
	}
	
	
//	----- hub 패킷에서 사용할 부분 -----
	private static final int HubHeaderSize = 3;
	private byte[] Hubheader = new byte[3];
//	-----------------------------

	byte Facility_ID;  // 프로토콜에서 number이라고 표현되어있음
	byte[] GATEWAY 		= new byte[2];
	byte GatewayN;
	byte[] GatewayChannelN = new byte[4];
	byte LEN;
	byte CMD;
	byte[] TAG_ID = new byte[4];
	byte STATUS;
	byte Battery;
	byte CS;

	//모든 출력문 로그 출력으로 변경할것.
	public void networkErrorProcess() {
		System.out.println("process : Network error ");
		for(int i=0; i < packetBody.length; i = i+2 ) {
			GATEWAY[0] = packetBody[i*2];
			GATEWAY[1] = packetBody[i*2+1];
			//에러난 게이트웨이 정보를 확인하고 필요한 대로 처리할것.
			
			
		}
		
	}
	
	public void pisID() {
		System.out.println("process : Get for PIS ID ");
		if((packetBody.length - 1 )%5 != 0){//
			System.out.println("byte warning");
		}
		
		Facility_ID = packetBody[0];
		
		for(int i = 1; i<packetBody.length; i= i+5) {
			GatewayN = packetBody[i];
			GatewayChannelN[0] = packetBody[i+1];
			GatewayChannelN[1] = packetBody[i+2];
			GatewayChannelN[2] = packetBody[i+3];
			GatewayChannelN[3] = packetBody[i+4];
			
			//Gateway 네트워크 ID, 채널 파악부분 
		}
		
	}
	
	public void pisSensorStatus() {
		System.out.println("process : Get for PIS Sensor status ");
		
		GATEWAY[0] = packetBody[0];
		GATEWAY[1] = packetBody[1];

		Packet sensor = new Packet();
		sensor.getHeaderPosition(packetBody);
		sensor.getPacketLength(packetBody);
		
		if( sensor.getBytePos(packetBody[4]) != ByteToHex.hexToByteArray("69") ) {//지정된 cmd가 아닌경우
			return;
		}
		TAG_ID[0] = packetBody[5] ;
		TAG_ID[1] = packetBody[6] ; 
		TAG_ID[2] = packetBody[7] ;
		TAG_ID[3] = packetBody[8] ;
		
		STATUS = packetBody[9] ;
		if(	STATUS == ByteToHex.hexToByteArray("45") ||
			STATUS == ByteToHex.hexToByteArray("50") ) {
			
		}else {
			System.out.println("STATUS byte error");
			return;
		}
		Battery = packetBody[10] ;
		CS = packetBody[11] ;
		if( sensor.isOriginCS(packetBody, CS) ) {
			
		}else {
			System.out.println("checksum byte error");
			return;
		}
	}
	
}