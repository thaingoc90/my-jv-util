package ind.web.nhp.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringUtils implements ApplicationContextAware {

	private static ApplicationContext ctx;

	@Override
	public void setApplicationContext(ApplicationContext appContext) throws BeansException {
		SpringUtils.ctx = appContext;

	}

	public static ApplicationContext getApplicationContext() {
		return ctx;
	}

	public static Object getBean(String name) {
		return ctx.getBean(name);
	}

	public static <T> T getBean(String name, Class<T> clazz) {
		return ctx.getBean(name, clazz);
	}

	public static <T> T getBean(Class<T> clazz) {
		return ctx.getBean(clazz);
	}
}