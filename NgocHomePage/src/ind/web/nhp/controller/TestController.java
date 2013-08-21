package ind.web.nhp.controller;

import ind.web.nhp.base.BaseController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gmr.web.multipart.GMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TestController extends BaseController {

	private static final String VIEW_NAME = "fe_test";

	@RequestMapping(value = { "/test" }, method = RequestMethod.GET)
	public String get(HttpServletRequest req, HttpServletResponse resp) {
		return VIEW_NAME;
	}

	@RequestMapping(value = { "/test" }, method = RequestMethod.POST)
	public String post(@RequestParam("name") String name, @RequestParam("file") GMultipartFile file,
	        Model model, HttpServletRequest request) {
		if (!file.isEmpty()) {
			try {
				byte[] bytes = file.getBytes();
				model.addAttribute("abc", bytes.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// store the bytes somewhere
			return "redirect:home";
		} else {
			return "redirect:test";
		}
	}

}
