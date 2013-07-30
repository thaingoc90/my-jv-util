package my.library.main;

import java.io.File;

import org.apache.log4j.MDC;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log4jExample {
	static Logger log = LoggerFactory.getLogger(Log4jExample.class);
	static {
		MDC.put("userName", "ngoc-thai");
	}

	public static void main(String[] args) throws Exception {
		String log4jconfig = System.getProperty("user.dir") + File.separator
				+ "src" + File.separator + "config" + File.separator + "log4j.properties";
		PropertyConfigurator.configure(log4jconfig);
		log.error("ds");
		log.warn("Abvc");
	}
}
