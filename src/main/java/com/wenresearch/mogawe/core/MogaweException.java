package com.wenresearch.mogawe.core;

@SuppressWarnings("serial")
public class MogaweException extends Exception {

	public MogaweException() {
		super("Error occured");
	}

	public MogaweException(String message) {
		super(message);
	}

}
