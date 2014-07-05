package com.wenresearch.mogaway.core;

@SuppressWarnings("serial")
public class MogawayException extends Exception {

	public MogawayException() {
		super("Error occured");
	}

	public MogawayException(String message) {
		super(message);
	}

}
