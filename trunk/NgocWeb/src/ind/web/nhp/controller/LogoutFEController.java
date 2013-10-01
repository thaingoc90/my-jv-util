package ind.web.nhp.controller;

import ind.web.nhp.base.Constants;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LogoutFEController extends BaseFrontendController {

	@RequestMapping(value = "/logout")
	@ResponseBody
	public Map<String, Object> handle(HttpServletRequest request, HttpServletResponse response) {
		try {
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.removeAttribute(Constants.NHP_FE_USER_ID);
			}
			Cookie cookie = new Cookie(Constants.NHP_FE_USER_ID, "");
			cookie.setMaxAge(0);
			response.addCookie(cookie);
			return createAjaxOk("Logout successful!");
		} catch (Exception e) {
			return createAjaxResult(Constants.AJAX_STATUS_ERROR, "Error: Logout fail!");
		}

	}
}
