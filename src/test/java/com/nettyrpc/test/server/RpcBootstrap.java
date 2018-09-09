package com.nettyrpc.test.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RpcBootstrap {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("server-spring.xml").start();;
	}
}