package my.library.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 * spring lib, config file must be in folder src
 * 
 * @author ngoctdn
 * 
 */
@Component
public class JavaSEBean {
	public static final String CONFIG_BEAN = "config/*.xml";

	@Autowired
	private HelloWorld obj;

	private static ApplicationContext ac;

	public static void main(String[] args) throws Exception {
		ac = new ClassPathXmlApplicationContext(CONFIG_BEAN);
		JavaSEBean mainObj = (JavaSEBean) ac.getBean(JavaSEBean.class);
		mainObj.obj.printMsg();
	}
}
