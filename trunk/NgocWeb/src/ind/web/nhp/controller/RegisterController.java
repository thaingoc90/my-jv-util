package ind.web.nhp.controller;

import ind.web.nhp.form.UserFEForm;
import ind.web.nhp.us.SimpleAuthenticationAgent;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RegisterController extends BaseFrontendController {

	public static final String VIEW_NAME = "register";

	@Autowired
	private SimpleAuthenticationAgent aa;

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {
		List<String> dobDays = new LinkedList<>();
		Map<String, String> dobMonths = new LinkedHashMap<>();
		List<String> dobYears = new LinkedList<>();

		for (int i = 1; i <= 31; i++) {
			dobDays.add(String.valueOf(i));
		}
		for (int i = 1; i <= 12; i++) {
			dobMonths.put(String.valueOf(i), new DateFormatSymbols().getMonths()[i - 1]);
		}
		Calendar now = Calendar.getInstance();
		int curYear = now.get(Calendar.YEAR);
		for (int i = curYear - 10; i >= curYear - 80; i--) {
			dobYears.add(String.valueOf(i));
		}

		model.addAttribute("dobDays", dobDays);
		model.addAttribute("dobMonths", dobMonths);
		model.addAttribute("dobYears", dobYears);
		return VIEW_NAME;
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String post(@ModelAttribute UserFEForm form, HttpServletRequest request) {
		return "redirect:home";
	}
}
