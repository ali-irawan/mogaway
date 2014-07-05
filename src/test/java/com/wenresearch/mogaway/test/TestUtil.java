package com.wenresearch.mogaway.test;

import java.io.ByteArrayInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.wenresearch.mogaway.util.Util;

public class TestUtil extends TestCase {
	private final static Logger log = LoggerFactory.getLogger(TestUtil.class);

	public void testRead() {
		log.info("Running test: Util.read()");
		String s = "message";

		ByteArrayInputStream in = new ByteArrayInputStream(s.getBytes());
		Assert.assertEquals("message", Util.read(in));
	}
}
