package com.wenresearch.mogaway.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.wenresearch.mogaway.http.Server;

@Component
@Scope("prototype")
public class Mogaway {

	@Autowired
	public Server server;

}
