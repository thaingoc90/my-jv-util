package ind.web.nhp.base;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class BaseController {

	private static Language defaultLang = new Language();

	@Autowired
	protected LanguageLoader languageManager;

	@ModelAttribute("lang")
	public Language getLanguage(Locale locale) {
		Language lang = languageManager.getLanguage(getLocale(locale));
		return lang != null ? lang : defaultLang;
	}

	@ModelAttribute("staticResourceRoot")
	public String modelStaticResourceRoot(HttpServletRequest request) {
		return request.getContextPath() + "/" + Constants.RESOURCE_PATH_MAPPING + "/";
	}

	@ModelAttribute("locale")
	public String getLocale(Locale locale) {
		return locale.getLanguage();
	}

}
