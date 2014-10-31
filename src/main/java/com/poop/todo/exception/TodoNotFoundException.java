package com.poop.todo.exception;

public class TodoNotFoundException extends Exception{
	private static final long serialVersionUID = 1L;

	public TodoNotFoundException(String message) {
		super(message);
	}

}
