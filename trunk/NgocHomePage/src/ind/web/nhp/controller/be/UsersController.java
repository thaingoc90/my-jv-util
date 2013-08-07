package ind.web.nhp.controller.be;

import ind.web.nhp.us.IUsManager;
import ind.web.nhp.us.IUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UsersController extends BaseBackendController {
	private static final String VIEW_NAME = "be_home";

	@Autowired
	private IUsManager usManager;

	@RequestMapping(value = { "/admin/users" }, method = RequestMethod.GET)
	public String get(Model model) {
		IUser user = usManager.getUser(1);
		model.addAttribute("user", user.getId());
		return VIEW_NAME;
	}
}
