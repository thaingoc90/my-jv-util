package ind.web.nhp.controller.be;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ModelAttribute;

import ind.web.nhp.base.BaseController;
import ind.web.nhp.base.Constants;
import ind.web.nhp.utils.UrlUtils;

public abstract class BaseBackendController extends BaseController {

	@ModelAttribute("baseUrl")
	public String modelBaseUrl(HttpServletRequest request) {
		String url = UrlUtils.getBaseUrl(request);
		url += "/" + Constants.MODULE_ADMIN + "/";
		return url;
	}
}
