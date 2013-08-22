package ind.web.nhp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ind.web.nhp.base.BaseController;

@Controller
public class ErrorController extends BaseController {

	private static final String VIEW_NAME = "error";

	@RequestMapping("error")
	public String handler() {
		return VIEW_NAME;
	}
}
