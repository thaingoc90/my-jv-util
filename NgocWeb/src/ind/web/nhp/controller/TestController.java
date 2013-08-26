package ind.web.nhp.controller;

import ind.web.nhp.base.BaseController;
import ind.web.nhp.base.ErrorConstants;
import ind.web.nhp.utils.PaperclipUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class TestController extends BaseController {

	private static final String VIEW_NAME = "fe_test";

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private PaperclipUtils pcUtils;

	@RequestMapping(value = { "/test" }, method = RequestMethod.GET)
	public String get(HttpServletRequest req) {
		return VIEW_NAME;
	}

	@RequestMapping(value = { "/test" }, method = RequestMethod.POST)
	public String post(@RequestParam("name") String name, @RequestParam("file") MultipartFile file,
			Model model, HttpServletRequest request) {
		if (file != null) {
			try {
				pcUtils.uploadFile("test", file, 10000000, ".mp3, .rar, .zip");
			} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute(ErrorConstants.MODEL_ERRORS, new String[] { e.getMessage() });
				return get(request);
			}
			// store the bytes somewhere
			return "redirect:home";
		} else {
			return "redirect:test";
		}
	}

}
