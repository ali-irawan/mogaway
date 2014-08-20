package com.wenresearch.mogaway.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import com.wenresearch.mogaway.model.InvokeCall;

/**
 * Utility class
 * 
 * @author Ali Irawan
 * @version 1.0
 * 
 */
public class Util {

	/**
	 * Read from input stream and return as string
	 * @param inputStream input stream bytes
	 * @return string content
	 */
	public static String read(InputStream inputStream) {
		String str = "";
		InputStream is = null;
		try {
			is = inputStream;
			byte[] bytes = new byte[256];
			int n = -1;

			ByteArrayOutputStream sw = new ByteArrayOutputStream(256);
			while (-1 != (n = is.read(bytes, 0, 256))) {
				sw.write(bytes, 0, n);
			}
			str = new String(sw.toByteArray());
			sw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return str;
	}
	
	/**
	 * Parse JSON call
	 * @param inputStream input stream JSON
	 * @return return invoke call
	 */
	public static InvokeCall parseJsonCall(InputStream inputStream, boolean useEncryption, byte password ) {
		InvokeCall invokeCall = null;
		
		byte[] result = null;
		String str = "";
		InputStream is = null;
		try {
			is = inputStream;
			byte[] bytes = new byte[256];
			int n = -1;

			ByteArrayOutputStream sw = new ByteArrayOutputStream(256);
			while (-1 != (n = is.read(bytes, 0, 256))) {
				sw.write(bytes, 0, n);
			}
			result = sw.toByteArray();
			sw.close();
			
			if(useEncryption){
				str = StringUtils.newStringUtf8(result);
				System.out.println("Gzip: " + str);
				
				byte[] decoded = EncodeUtil.fromBase64(str);
				byte[] plain = EncodeUtil.encrypt_decrypt(decoded, 123456);
				byte[] unzip = EncodeUtil.gunzip(plain);
				str = StringUtils.newStringUtf8(unzip);
			} else {
				str = StringUtils.newStringUtf8(result);
			}
			System.out.println("JSON: " + str);
			invokeCall = new InvokeCall(str);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return invokeCall;
	}
}
