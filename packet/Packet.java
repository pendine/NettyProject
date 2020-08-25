package com.it_cous.packet;

import com.it_cous.toHex.ByteToHex;

/**�ʼ� ��������� 4
 * cs �� ���� ��� ����Ʈ�� ���տ� ���� 1�� ����. ^-128
 * SOH		byte	1	Start Of Header	
 * LEN		byte	1	��Ŷ ����
 * DIR		byte	1	��Ŷ ����
 * CMD		byte	1	CMD(OP CODE)	
 * body~~
 * CS		byte	1	CheckSum	 
 * */
public class Packet {
	
	private static final int headerSize = 4;
	private static final byte SOH = ByteToHex.hexToByteArray("2B");
	private byte[] header;
	private byte LEN;
	private byte DIR;
	private byte CMD;
	private byte cs;	// �����
	private byte[] body;
	private byte calculCheckSum;	//������
	private int totalBytes;
	private byte[] allBytes;
	private byte[] bodybytes;
	private boolean recieved = false;
	
	public Packet() {
		
	}
	
	public Packet(byte[] bytes) {
		setHeader(bytes);
	}
	
	/**
	 * �ܸ��� ���� ���� ���ӽ� ����� ��Ŷ����
	 * */
	public byte[] firstSend() {
		byte soh = ByteToHex.hexToByteArray("2B");
		byte total_packetLength = ByteToHex.hexToByteArray("06");
		byte DIR = ByteToHex.hexToByteArray("5A");
		byte CMD = ByteToHex.hexToByteArray("A5");
		byte body = ByteToHex.hexToByteArray("00");
		byte checkSum = getCheckSum( new byte[] {soh , total_packetLength  , DIR  , CMD  , body} );
		
		byte[] returnbytes = {soh,total_packetLength,DIR,CMD,body,checkSum};
		
		Packet aaa = new Packet();
		aaa.setHeader(returnbytes);
		System.out.println(aaa.toString());
		System.out.println( ByteToHex.bytesToHex(returnbytes) );
		return returnbytes;
	}
	
	
	/**
	 * ��Ŷ�� ���, �ٵ�, ���� �����Ͽ� ����
	 * ������ checksum �κ���.
	 * */
	public void setHeader(byte[] inputBytes) {
//		���� ��Ŷ�� ���¿� ���� ���
//		1. ��Ŷ�ȿ��� ��� ������ġ�� ã�� �� ����
//		2. ��Ŷ�ȿ��� ��� ������ġ�� ã�� �� ������ ��Ŷ�� ������ ������ ���� ��Ŷ �ۿ� ����.
//		3. ��Ŷ�ȿ��� ��� ������ġ�� ã�� �� ������ ��Ŷ�� ������ ������ ���� ��Ŷ �ȿ� ����
		
		int headerPosition = getHeaderPosition(inputBytes); // soh �����ġ Ȯ��
		
		if(headerPosition < 0 ) { //����� ����. �۾�
			System.out.println("no Header");
			return;
		}else if( headerPosition+inputBytes[headerPosition+1] > inputBytes.length || // ��� ������ġ���� ��Ŷ���̰� ���� ��Ŷ������ �����
				headerPosition == inputBytes.length-1 ) //��� ������ġ�� �������κ��϶�(���̸� ������ �������)
			{
			System.out.println("packet is out of range about recieved packet");
			return;		
		}
		
		byte[] arr = new byte[ inputBytes[headerPosition+1] ];
		for(int i=0; i < arr.length; i++) {
			arr[i] = inputBytes[i+headerPosition]; //�����ġ�������� ��Ŷ���̸�ŭ �о ����.
		}
		
		header = new byte[headerSize];
		
		for(int i=0; i < headerSize; i++ ) {
			header[i] = arr[i];
		}
		
		totalBytes = header[1];
		
		int bodyLength = header[1] - headerSize - 1;
		body = new byte[ bodyLength ];
		
		for(int i=headerSize; i<arr.length-1; i++) {
			body[i-headerSize] = arr[i];  //��Ŷ�� �ٵ� ����. ����� checksum ����Ʈ ������
		}
		
		cs = arr[arr.length-1];  // �񱳿� checksum ����Ʈ ��������.
		
		if( isAllMessage() && isOriginCS(arr,cs)  ) {
			System.out.println("Packet Stat : Accepted all packet ");
			allBytes = new byte[arr.length];
			for(int i=0; i<arr.length; i++)allBytes[i] = arr[i];
			this.toString();
			recieved = true;
		}else if( isAllMessage() && !isOriginCS(arr,cs) ) {
			System.out.println("Packet Stat : difference check sum ");
			byte[] tmp = new byte[arr.length -1 ];
			for(int i=0; i<tmp.length; i++) {tmp[i] = arr[i]; }
			System.out.println("get CS = "+ByteToHex.byteToHex(cs)+" cal CS = "+ ByteToHex.byteToHex(getCheckSum(tmp)) );
			this.toString();
			return;
		}else {
			System.out.println("Packet Stat : Didn't Accept all packet : retry request");
			return;
		}
		
	}
	
	/**
	 * ����� �����ϴ��� Ȯ�ο�
	 * getHeaderPosition(byte[] bytes)�� ��ü ��밡�������� 
	 * �Ⱦ��Ͽ���
	 * */
	public boolean isinHeader(byte[] bytes) {
		for(int i=0; i<bytes.length; i++) {
			if(bytes[i] == SOH ) return true;
		}
		return false;
	}
	
	/**
	 * ��������� �����Ѵٸ� ��Ŷ���� ������� ��ġ ��ȯ
	 * ���ٸ� -1 return 
	 * @param byte[] bytes
	 * @return
	 */
	public int getHeaderPosition(byte[] bytes) {
		for(int i=0; i<bytes.length; i++) {
			if(bytes[i] == SOH ) return i;
		}
		return -1;
	}

	/**
	 * ��Ŷ�� ���������� �����ϰ� �ִ� ����Ʈ�� ��ġ�� ��ȯ
	 * @author admin
	 * */
	public int getPacketLength(byte[] bytes) {
		int headerPosition = getHeaderPosition(bytes);
		int packetLengthPosition = bytes[ headerPosition+1 ];
		
		return packetLengthPosition;
	}	

	
	/**
	 * String���� ����� Ȯ���� �� �ִ� �α� ��¿�. 
	 * */
	public String toString() {
		String a = "Header info - SOH ["	+	ByteToHex.byteToHex(header[0])	+	"] "
				+" LEN int["	+	totalBytes	+	"] "
				+" LEN hex["	+	ByteToHex.byteToHex(header[1])	+	"] "
				+" DIR ["	+	ByteToHex.byteToHex(header[2])	+	"] "
				+" CMD ["	+	ByteToHex.byteToHex(header[3])	+	"] "
				+" BODY ["	+	ByteToHex.bytesToHex(body)	+	"] "
				+" CS ["	+	ByteToHex.byteToHex(cs)	+	"] ";;
		return a;
	}
	
	
	/**
	 * body�� byte�迭 ��ȯ��.
	 * */
	public byte[] getBody() {
		return body;
	}
	
	
	/**
	 * ��� ��Ŷ�� �޾Ҵ��� Ȯ�ο�
	 * */
	public boolean isAllMessage() {
		if(totalBytes - 1 == ( body.length + headerSize)) return true;
		else return false;
	}
	
	/**
	 * ��� ��Ŷ�� �޾Ҵ��� Ȯ�ο�
	 * */
	
	public boolean getRecieved() {
		return recieved;
	}
	
	
	/**
	 * ���� �迭�� ���� cs ����Ʈ ����
	 * */
	
	public byte getCheckSum(byte[] array) {
//		byte[] header = new byte[headerSize];
//		for(int i=0; i<headerSize; i++) {
//			header[i] = array[i];
//		}
//				
//		byte[] body = new byte[array.length - headerSize];
//		for(int i=headerSize; i < array.length; i++) {
//			body[i-headerSize] = array[i];
//		}
//		
//		byte returnCs = checkFrame(header, body);
		
		byte returnCs = checkFrame(array);
		
		return returnCs;
	}
	

	private static byte checkFrame(byte[] inputArr) {
	      byte checkSum = 0;
	      for(int i=0; i<inputArr.length; i++) checkSum ^= inputArr[i];
	      return checkSum;
	}
	
	private static byte checkFrame(byte[] header, byte[] data) {
	      byte checkSum = 0;
	      for (byte b : header) 
	      { 
	    	  checkSum ^= b; 
	      }      
	      
	      if(data != null){
	         for (byte b : data)
	         { 
	        	 checkSum ^= b; 
	         }
	      }
	      return checkSum;
	}

//	2b247aa5000000cd5cd44300013363d44300029a69d44300030070d44300046676d443 11
	
	/**
	 * ��Ŷ ������ ������ Ȯ��
	 * Ȯ�� ����� ���޹��� ��Ŷ�� cs�� cs�� ������ ����Ʈ�� checksum���� ����Ͽ�
	 * �������� �ƴ��� Ȯ��.
	 * �����ϴٸ� return true
	 * �ƴ϶�� return false;
	 * */
	
	public boolean isOriginCS(byte[] array, byte cs) {
		byte[] target = new byte[array.length-1];
		for(int i = 0; i<target.length; i++) {
			target[i] = array[i];
		}
		calculCheckSum = getCheckSum(target);
		
		if(calculCheckSum == cs) return true;
		else return false;
	}

	public byte getCs() {
		return cs;
	}
	
	public byte getBytePos(int pos) {
		return allBytes[pos];
	}
	
}
