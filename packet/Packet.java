package com.it_cous.packet;

import com.it_cous.toHex.ByteToHex;

/**필수 헤더사이즈 4
 * cs 는 현재 모든 바이트의 총합에 대한 1의 보수. ^-128
 * SOH		byte	1	Start Of Header	
 * LEN		byte	1	패킷 길이
 * DIR		byte	1	패킷 방향
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
	private byte cs;	// 저장용
	private byte[] body;
	private byte calculCheckSum;	//검증용
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
	 * 단말의 서버 최초 접속시 응답용 패킷설정
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
	 * 패킷의 헤더, 바디, 테일 구분하여 저장
	 * 테일은 checksum 부분임.
	 * */
	public void setHeader(byte[] inputBytes) {
//		받은 패킷의 상태에 대한 경우
//		1. 패킷안에서 헤더 시작위치를 찾을 수 없음
//		2. 패킷안에서 헤더 시작위치를 찾을 수 있지만 패킷의 끝나는 범위가 받은 패킷 밖에 있음.
//		3. 패킷안에서 헤더 시작위치를 찾을 수 있으며 패킷의 끝나는 범위가 받은 패킷 안에 있음
		
		int headerPosition = getHeaderPosition(inputBytes); // soh 헤더위치 확인
		
		if(headerPosition < 0 ) { //헤더가 없음. 작없
			System.out.println("no Header");
			return;
		}else if( headerPosition+inputBytes[headerPosition+1] > inputBytes.length || // 헤더 시작위치부터 패킷길이가 받은 패킷범위를 벗어날때
				headerPosition == inputBytes.length-1 ) //헤더 시작위치가 마지막부분일때(길이를 읽을수 없을까봐)
			{
			System.out.println("packet is out of range about recieved packet");
			return;		
		}
		
		byte[] arr = new byte[ inputBytes[headerPosition+1] ];
		for(int i=0; i < arr.length; i++) {
			arr[i] = inputBytes[i+headerPosition]; //헤더위치에서부터 패킷길이만큼 읽어서 저장.
		}
		
		header = new byte[headerSize];
		
		for(int i=0; i < headerSize; i++ ) {
			header[i] = arr[i];
		}
		
		totalBytes = header[1];
		
		int bodyLength = header[1] - headerSize - 1;
		body = new byte[ bodyLength ];
		
		for(int i=headerSize; i<arr.length-1; i++) {
			body[i-headerSize] = arr[i];  //패킷의 바디만 저장. 저장시 checksum 바이트 미포함
		}
		
		cs = arr[arr.length-1];  // 비교용 checksum 바이트 따로저장.
		
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
	 * 헤더가 존재하는지 확인용
	 * getHeaderPosition(byte[] bytes)로 대체 사용가능함으로 
	 * 안쓰일예정
	 * */
	public boolean isinHeader(byte[] bytes) {
		for(int i=0; i<bytes.length; i++) {
			if(bytes[i] == SOH ) return true;
		}
		return false;
	}
	
	/**
	 * 시작헤더가 존재한다면 패킷에서 시작헤더 위치 반환
	 * 없다면 -1 return 
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
	 * 패킷의 길이정보를 포함하고 있는 바이트의 위치를 반환
	 * @author admin
	 * */
	public int getPacketLength(byte[] bytes) {
		int headerPosition = getHeaderPosition(bytes);
		int packetLengthPosition = bytes[ headerPosition+1 ];
		
		return packetLengthPosition;
	}	

	
	/**
	 * String으로 사람이 확인할 수 있는 로그 출력용. 
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
	 * body의 byte배열 반환용.
	 * */
	public byte[] getBody() {
		return body;
	}
	
	
	/**
	 * 모든 패킷을 받았는지 확인용
	 * */
	public boolean isAllMessage() {
		if(totalBytes - 1 == ( body.length + headerSize)) return true;
		else return false;
	}
	
	/**
	 * 모든 패킷을 받았는지 확인용
	 * */
	
	public boolean getRecieved() {
		return recieved;
	}
	
	
	/**
	 * 현재 배열을 토대로 cs 바이트 생성
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
	 * 패킷 오류가 없는지 확인
	 * 확인 방법은 전달받은 패킷의 cs와 cs를 제외한 바이트를 checksum으로 계산하여
	 * 동일한지 아닌지 확인.
	 * 동일하다면 return true
	 * 아니라면 return false;
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
