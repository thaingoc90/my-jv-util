package ind.web.nhp.controller;

import java.io.File;

import ind.web.nhp.base.BaseController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @RequestMapping(value = { "/test" }, method = RequestMethod.GET)
    public String get(HttpServletRequest req, HttpServletResponse resp) {
	return VIEW_NAME;
    }

    @RequestMapping(value = { "/test" }, method = RequestMethod.POST)
    public String post(@RequestParam("name") String name, @RequestParam("file") MultipartFile file,
	    Model model, HttpServletRequest request) {
	if (!file.isEmpty()) {
	    try {
		byte[] bytes = file.getBytes();
		File folder = new File(servletContext.getRealPath("/storage/file/1232/123"));
		if (!folder.exists()) {
		    folder.mkdirs();
		}
		model.addAttribute("abc", bytes.toString());
	    } catch (Exception e) {
		e.printStackTrace();
		return "redirect:test";
	    }
	    // store the bytes somewhere
	    return "redirect:home";
	} else {
	    return "redirect:test";
	}
    }

}
