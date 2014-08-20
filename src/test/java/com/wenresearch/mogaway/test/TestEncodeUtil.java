package com.wenresearch.mogaway.test;

import java.io.IOException;

import org.apache.commons.codec.binary.StringUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.wenresearch.mogaway.util.EncodeUtil;

public class TestEncodeUtil extends TestCase {
	
	public final static String TEST_BASE64 = "H4sIAAAAAAAAA6tWykvMTVWyUsrJT8/MU9JRKijKTwZyU/J9YAKJRYm5xUpW0Uo+jqFBrn5AocLy1KKSSqXYWgA2lmzGPgAAAA==";
	
	public void testEncodeDecode(){
		
		String encoded = EncodeUtil.toBase64("123456".getBytes());
		String decoded = StringUtils.newStringUtf8(EncodeUtil.fromBase64(encoded));
		
		Assert.assertEquals("123456", decoded);
	}
	
	public void testGzip() throws IOException{
		
		byte[] temp = new byte[600];
		for(int i=0;i<temp.length;i++){
			temp[i] = (byte) i;
		}
		
		//System.out.println("Before zip: " + temp.length);
		byte[] zipped = EncodeUtil.gzip(temp);
		//System.out.println("After zip: " + zipped.length);
		
		byte[] unzipped = EncodeUtil.gunzip(zipped);
		//System.out.println("After unzip: " + unzipped.length);
		
		Assert.assertEquals(temp.length, unzipped.length);
		Assert.assertEquals(new String(temp), new String(unzipped));
	}
	
	public void testEncode() throws IOException{
		String temp = "{\"name\":\"login\",\"proc\":\"doLogin\",\"params\":[\"LAUREN\",\"qwerty\"]}";
		String send = EncodeUtil.toBase64(EncodeUtil.gzip(temp.getBytes()));
		System.out.println(send);
		
		System.out.println(StringUtils.newStringUtf8(EncodeUtil.gunzip(EncodeUtil.fromBase64(send))));
	}
}
