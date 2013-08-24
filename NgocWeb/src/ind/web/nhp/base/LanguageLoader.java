package ind.web.nhp.base;

import ind.web.nhp.utils.PropsUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;

public class LanguageLoader implements ServletContextAware {

    private Logger LOGGER = LoggerFactory.getLogger(LanguageLoader.class);
    private ServletContext servletContext;
    private String configLocation;
    public static final String LOCALE_LIST = "locale.list";
    public static final String LOCALE_LOCATION = "locale.location";
    public static final String DEFAULT_LOCALE_LOCATION = "language/core";
    private Map<String, Language> languages;

    public void init() {
	Properties props = loadConfig();
	String localeLocation = props.getProperty(LOCALE_LOCATION, DEFAULT_LOCALE_LOCATION);
	String temp = props.getProperty(LOCALE_LIST);
	String[] tempListLocales = temp.split(",");
	List<String> listLocale = new LinkedList<String>();
	for (String locale : tempListLocales) {
	    locale = locale != null ? locale.trim() : null;
	    if (!StringUtils.isEmpty(locale)) {
		listLocale.add(locale);
	    }
	}
	if (listLocale.size() == 0) {
	    String msg = "No language pack defined!";
	    LOGGER.warn(msg);
	    return;
	}
	languages = new HashMap<String, Language>();
	for (String locale : listLocale) {
	    Map<String, String> messages = loadLanguageMessages(localeLocation + '/' + locale);
	    Language lang = new Language(messages);
	    languages.put(locale, lang);
	}

    }

    private Map<String, String> loadLanguageMessages(String location) {
	Map<String, String> allMessages = new ConcurrentHashMap<String, String>();
	List<String> resources = enumResources(location);
	for (String resource : resources) {
	    InputStream is = null;
	    try {
		is = servletContext.getResourceAsStream(resource);
		if (is != null) {
		    Properties props = PropsUtils.loadProperties(is, false);
		    for (String key : props.stringPropertyNames()) {
			allMessages.put(key, props.getProperty(key));
		    }
		}
	    } catch (Exception e) {
		LOGGER.warn(e.getMessage(), e);
	    } finally {
		IOUtils.closeQuietly(is);
	    }
	}
	return allMessages;
    }

    private Properties loadConfig() {
	InputStream is = null;
	try {
	    is = servletContext.getResourceAsStream(configLocation);
	    Properties props = PropsUtils.loadProperties(is, false);
	    return props;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	} finally {
	    IOUtils.closeQuietly(is);
	}
    }

    public List<String> enumResources(String location) {
	Set<?> temp = servletContext.getResourcePaths(location);
	List<String> result = new LinkedList<String>();
	for (Object obj : temp) {
	    result.add(obj.toString());
	}
	return result;
    }

    public String getConfigLocation() {
	return configLocation;
    }

    public void setConfigLocation(String configLocation) {
	this.configLocation = configLocation;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
	this.servletContext = servletContext;
    }

    public Language getLanguage(String locale) {
	return (languages == null || languages.size() == 0) ? null : languages.get(locale);
    }
}
