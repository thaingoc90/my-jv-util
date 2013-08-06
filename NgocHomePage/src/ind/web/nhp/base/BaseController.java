package ind.web.nhp.base;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class BaseController {

	@ModelAttribute("staticResourceRoot")
	public String modelStaticResourceRoot(HttpServletRequest request) {
		return request.getContextPath() + "/" + Constants.RESOURCE_PATH_MAPPING + "/";
	}
}
