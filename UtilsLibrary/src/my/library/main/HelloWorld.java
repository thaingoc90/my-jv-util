package my.library.main;

import org.springframework.stereotype.Service;

@Service
public class HelloWorld {

	private String msg;
	
	public HelloWorld() {
		msg = "Hello world";
	}
	
	public void printMsg() {
		System.out.println(msg);
	}
}
