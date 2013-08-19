package ind.web.nhp.controller.be;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ProfileController extends BaseBackendController {

	private static final String VIEW_NAME = "be_profile";

	@RequestMapping(value = { "/admin/profile", "/admin/profile/**" })
	public String get(HttpServletRequest request, Model model) {
		return VIEW_NAME;
	}
}
