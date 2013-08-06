package ind.web.nhp.controller.be;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DashboardController extends BaseBackendController {

	private static final String VIEW_NAME = "be_home";

	@RequestMapping(value = {"/admin", "/admin/*"}, method = RequestMethod.GET)
	public String get(HttpServletRequest req, HttpServletResponse resp) {
		return VIEW_NAME;
	}
}
