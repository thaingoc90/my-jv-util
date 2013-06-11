package my.library.utils;

import org.springframework.context.ApplicationContext;

/**
 * spring-context-3.0.5.RELEASE.jar
 * 
 * @author ngoctdn
 *
 */
public class SpringUtils {
	
	private static ApplicationContext appContext;

	public static ApplicationContext getAppContext() {
		return appContext;
	}

	public static void setAppContext(ApplicationContext appContext) {
		SpringUtils.appContext = appContext;
	}
	
	/**
	 * Gets a bean by name.
	 * 
	 * @param <T>
	 * @param name
	 * @param clazz
	 * @return
	 */
	public static <T> T getBean(String name, Class<T> clazz) {
		return appContext.getBean(name, clazz);
	}

	public static <T> T getBean(Class<T> clazz) {
		return appContext.getBean(clazz);
	}

}
