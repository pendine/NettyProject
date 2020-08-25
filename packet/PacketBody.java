package com.it_cous.packet;

import com.it_cous.toHex.ByteToHex;
/**
 * ��Ŷ�� cmd�� cs������ ��� ������ ���⼭ ó���Ұ��̸�
 * �� Ŭ������ ����� ������ cmd�� �����ؾ���.
 * ����� �ܼ��� ������ ó���� �κ�.
 * */
public class PacketBody extends Packet{

	/**��Ʈ��ũ ���� ��Ŷ�� �����Ѵ�.
3	DIR					byte	1	��Ŷ ����				0x7A(Rx from Coordinator)
4	CMD					byte	1	CMD(OP CODE)		0xad
	��Ʈ��ũ ���� �߻��� ����Ʈ���� ����ŭ �ݺ�		�ݺ�	N			
5	GATEWAY				byte	2	����Ʈ���� ��ȣ			0x00 0x00 �� 0xXX 0xXX
	��Ʈ��ũ ���� �߻��� ����Ʈ���� ����ŭ �ݺ�		�ݺ�	N			
7	CS					byte	1	CheckSum			1~��Ŷ�� XOR �� �Դϴ�.
	 * */
	
	/**PIS�� �ü���ID�� ������ �����Ѵ�.
3	DIR					byte	1	��Ŷ ����				0x7A(Rx from Coordinator)
4	CMD					byte	1	CMD(OP CODE)		0xa5
5	Facility ID			number	1	�ü��� ID				0x00     << int�����ν� 4����Ʈ���� �ƴϸ� 1����Ʈ������ ������������ �ٸ��� ���������� �𸣰���
	����Ʈ���� �� ��ŭ �ݺ�						
6	Gateway n			byte	1	����Ʈ���� n�� ���̵�		0x00 0x00
7	Gateway n_Channel	byte	4	����Ʈ���� n�� ä��		"0x01 0xBF 0x22 0x2E
														(ch 447.8750)"
	����Ʈ���� �� ��ŭ �ݺ�						
8	CS					byte	1	CheckSum		1~��Ŷ�� XOR �� �Դϴ�.
	 * */
	
	/**PIS �������������� ������ �����Ѵ�.
3	DIR					byte	1	��Ŷ ����				0x7A(Rx from Coordinator)
4	CMD					byte	1	CMD(OP CODE)		0xab
5	GATEWAY				byte	2	����Ʈ���� ��ȣ			0x00 0x00 �� 0xXX 0xXX
	Hub Packet ����						
6	SOH					byte	1	Start Of Header		0x2B
7	LEN					byte	1	��Ŷ ����		
8	CMD					byte	1	CMD(OP CODE)		"I" (0x69)
9	TAG_ID				byte	4	����ID		
10	STATUS				byte	1	���� ����				"E" (0x45) : �������
														"P" (0x50) : �����Ǿ� ����
11	Battery				byte	1	���͸� ����				(battery Value * 0.05) + 1.7 V
12	CS					byte	1	CheckSum			�׸� 6-11 XOR ��
	Hub Packet ��
13	CS					byte	1	CheckSum			1~��Ŷ�� XOR �� �Դϴ�.
	 * */
/*	��Ŷ ����	0x7A = �������� ����
	CMD = 0xad : ��Ʈ��ũ ���� ��Ŷ�� ������ ����
		  0xa5 : PIS�� �ü���ID�� ������ ����
		  0xab : PIS �������������� ������ ����
*/

	private byte[] packetBody;
	
	public PacketBody() {}
	
	public PacketBody(byte[] packetBody) {
		this.packetBody = packetBody;
	}
	
	
//	----- hub ��Ŷ���� ����� �κ� -----
	private static final int HubHeaderSize = 3;
	private byte[] Hubheader = new byte[3];
//	-----------------------------

	byte Facility_ID;  // �������ݿ��� number�̶�� ǥ���Ǿ�����
	byte[] GATEWAY 		= new byte[2];
	byte GatewayN;
	byte[] GatewayChannelN = new byte[4];
	byte LEN;
	byte CMD;
	byte[] TAG_ID = new byte[4];
	byte STATUS;
	byte Battery;
	byte CS;

	//��� ��¹� �α� ������� �����Ұ�.
	public void networkErrorProcess() {
		System.out.println("process : Network error ");
		for(int i=0; i < packetBody.length; i = i+2 ) {
			GATEWAY[0] = packetBody[i*2];
			GATEWAY[1] = packetBody[i*2+1];
			//������ ����Ʈ���� ������ Ȯ���ϰ� �ʿ��� ��� ó���Ұ�.
			
			
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
			
			//Gateway ��Ʈ��ũ ID, ä�� �ľǺκ� 
		}
		
	}
	
	public void pisSensorStatus() {
		System.out.println("process : Get for PIS Sensor status ");
		
		GATEWAY[0] = packetBody[0];
		GATEWAY[1] = packetBody[1];

		Packet sensor = new Packet();
		sensor.getHeaderPosition(packetBody);
		sensor.getPacketLength(packetBody);
		
		if( sensor.getBytePos(packetBody[4]) != ByteToHex.hexToByteArray("69") ) {//������ cmd�� �ƴѰ��
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