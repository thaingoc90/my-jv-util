package ind.web.nhp.base;

import ind.web.nhp.model.ErrorModel;
import ind.web.nhp.utils.UrlUtils;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class BaseController {

	private static Language defaultLang = new Language();
	protected Language lang;

	@Autowired
	protected LanguageLoader languageManager;

	@ModelAttribute("lang")
	public Language getLanguage(Locale locale) {
		Language lang = languageManager.getLanguage(getLocale(locale));
		this.lang = lang != null ? lang : defaultLang;
		return this.lang;
	}

	@ModelAttribute("staticResourceRoot")
	public String modelStaticResourceRoot(HttpServletRequest request) {
		return request.getContextPath() + "/" + Constants.RESOURCE_PATH_MAPPING + "/";
	}

	@ModelAttribute("locale")
	public String getLocale(Locale locale) {
		return locale.getLanguage();
	}

	@ModelAttribute("baseUrl")
	public String modelBaseUrl(HttpServletRequest request) {
		return UrlUtils.getBaseUrl(request);
	}

	protected ErrorModel buildErrorObject(int errCode, String errMsg, String msgSuccess) {
		return new ErrorModel(errCode, errMsg, msgSuccess);
	}
}
