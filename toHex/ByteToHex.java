package com.it_cous.toHex;

import java.nio.charset.StandardCharsets;

public class ByteToHex {
	
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	public static byte hexToByteArray(String s) {	
	    return (byte) ((Character.digit(s.charAt(0), 16) << 4)
                + Character.digit(s.charAt(1), 16));
	}
	
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public static String byteToHex(byte bytes) {
	    int v = bytes & 0xFF;
	    char b = HEX_ARRAY[v>>>4];
	    char f = HEX_ARRAY[v&0x0f];
	    char[] hexChars = {b,f};
	    
	    return new String(hexChars);
	}
	
	public static String stringToHex(String s) {
	    String result = "";

	    for (int i = 0; i < s.length(); i++) {
	      result += String.format("%02X ", (int) s.charAt(i));
	    }

	    return result;
	  }
	
	
}
