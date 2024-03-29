package ind.web.nhp.controller;

import ind.web.nhp.base.BaseController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController extends BaseController {

	private static final String VIEW_NAME = "home";

	@RequestMapping(value = { "/", "/*" }, method = RequestMethod.GET)
	public String get(HttpServletRequest req, HttpServletResponse resp) {
		return VIEW_NAME;
	}
}