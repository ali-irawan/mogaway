package com.wenresearch.mogaway.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

public class EncodeUtil {

	public static byte[] encrypt_decrypt(byte[] bytes, int password){
		int j=password;
		for(int i=0;i<bytes.length;i++){
			bytes[i] = (byte)(bytes[i] ^ j);
			j--;
			if(j==0) j = password;
		}
		return bytes;
	}
	public static String toBase64(byte[] bytes){
		return StringUtils.newStringUtf8(Base64.encodeBase64(bytes));
	}
	
	public static byte[] fromBase64(String base64String){
		return Base64.decodeBase64(base64String);
	}
	
	public static byte[] gzip(byte[] bytes) throws IOException{
		ByteArrayOutputStream gzout = new ByteArrayOutputStream(150);
		GZIPOutputStream gzin = new GZIPOutputStream(gzout);
		gzin.write(bytes);
		gzin.close();
		return gzout.toByteArray();
	}
	
	public static byte[] gunzip(byte[] bytes) throws IOException{
		GZIPInputStream gzin = new GZIPInputStream(new ByteArrayInputStream(bytes));
		ByteArrayOutputStream gzout = new ByteArrayOutputStream(150);
		for (int c = gzin.read(); c != -1; c = gzin.read()) {
		      gzout.write(c);
		}
		byte[] result = gzout.toByteArray();
		gzout.close();
		return result;
	}
}
