package ind.web.nhp.controller.be;

import ind.web.nhp.base.Constants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LogoutController extends BaseBackendController {

	@RequestMapping("/admin/logout")
	public String put(HttpServletRequest request, RedirectAttributes redirectAttributes) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(Constants.NHP_USER_ID);
		}
		return "redirect:/admin/login";
	}
}
