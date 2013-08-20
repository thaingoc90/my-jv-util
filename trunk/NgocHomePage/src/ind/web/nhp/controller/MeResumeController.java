package ind.web.nhp.controller;

import ind.web.nhp.base.BaseController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MeResumeController extends BaseController {

	private final static String VIEW_NAME = "fe_resume";

	@RequestMapping(value = "/me/resume")
	public String handler() {
		return VIEW_NAME;
	}
}
