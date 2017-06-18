package com.mycompany.springbatch.demo.message;

public interface JdbcMessage {

	
	public void send(Object t);
	
	public Object receive();
}
