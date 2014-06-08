package com.recovr.app;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Util {
	public static String Username = "";
	public static String registerURL = "http://192.168.1.117:3000/profile";
	public static String loginURL = "http://192.168.1.117:3000/profile";
	public static String coordinateURL = "http://192.168.1.117:3000/coordinate";
	
	public static String readInputStreamAsString(InputStream in) throws IOException {
    	BufferedInputStream bis = new BufferedInputStream(in);
    	ByteArrayOutputStream buf = new ByteArrayOutputStream();
    	int result = bis.read();
    	while(result != -1) {
    		byte b = (byte)result;
    	    buf.write(b);
    	    result = bis.read();
    	}        
    	return buf.toString();
    }
}
