package com.wenresearch.mogawe.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.wenresearch.mogawe.model.InvokeData;

public class Util {

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
	public static InvokeData parseJsonBody(InputStream inputStream) {
		InvokeData invokeData = null;
		
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
			
			invokeData = new InvokeData(str);
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
		return invokeData;
	}
}
