package com.webproject.utils;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
	public static String getMD5(String string){
		if(string==null){
			return null;
		}
		try {
	        MessageDigest md = MessageDigest.getInstance("MD5");
	        byte[] array = md.digest(string.getBytes());
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < array.length; ++i) {
	          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
	       }
	       return sb.toString();
	    } catch (NoSuchAlgorithmException e) {
	    	
	    }
	   	return null;
	}	
	
	public static void main(String args[]){
		System.out.println(getMD5("secret"));
	}
}
